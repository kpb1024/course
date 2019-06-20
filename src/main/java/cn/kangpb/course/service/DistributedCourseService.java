package cn.kangpb.course.service;

import cn.kangpb.course.dao.CourseMapper;
import cn.kangpb.course.pojo.Course;
import cn.kangpb.course.utils.ServiceLimit;
import cn.kangpb.course.utils.ServiceLock;
import cn.kangpb.course.utils.ZookeeperLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DistributedCourseService {
    private final Logger LOGGER = LoggerFactory.getLogger(DistributedCourseService.class);
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1, 10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));
    private Lock lock = new ReentrantLock(true);
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired private CourseMapper courseMapper;

    public Result zkSECKILL(int cid) {
        reset(cid);
        int num = 10000;
        final CountDownLatch latch = new CountDownLatch(num);//N个购买者
        LOGGER.info("运用Zookeeper分布式锁抢课开始");
        for(int i=0;i<num;i++){
            final int sid = i;
            executor.execute(() -> {
                Result result = startZookeeperSECKILL(cid, sid);
                if(result!=null){
                    LOGGER.info("用户:{}{}",sid,result.get("msg"));
                }else{
                    LOGGER.info("用户:{}{}",sid,"哎呦喂，人也太多了，请稍后！");
                }
                latch.countDown();
            });
        }
        try {
            latch.await();// 等待所有人任务结束
            int count = getSECKILLCount(cid);
            LOGGER.info("一共秒杀出{}件商品",count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    public Result startZookeeperSECKILL(int cid, int sid) {
        boolean res = false;
        try {
            res = ZookeeperLock.acquire(3,TimeUnit.SECONDS);
            if (res) {
                int number = courseMapper.getNum(cid);
                if(number>0){
                    courseMapper.minusOne(cid);
                    courseMapper.addStudentCourse(cid, sid, 1);
                }else{
                    return Result.error(SECKILLStatEnum.END);
                }
            } else{
                return Result.error(SECKILLStatEnum.MUCH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res) {
                ZookeeperLock.release();
            }
        }
        return Result.ok(SECKILLStatEnum.SUCCESS);
    }

    public int getSECKILLCount(int cid) {
        return courseMapper.getSECKILLCount(cid);
    }

    @Transactional
    public void reset(int cid) {
        courseMapper.resetCourseVolume(cid);
        courseMapper.resetSuccess(cid);
    }
}
