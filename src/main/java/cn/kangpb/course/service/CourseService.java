package cn.kangpb.course.service;

import cn.kangpb.course.dao.CourseMapper;
import cn.kangpb.course.pojo.Course;
import cn.kangpb.course.utils.ServiceLimit;
import cn.kangpb.course.utils.ServiceLock;
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
public class CourseService {
    private final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1, 10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));
    private Lock lock = new ReentrantLock(true);
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired private CourseMapper courseMapper;

    @Transactional(readOnly = true)
    public List<Course> getAll(){
        LOGGER.info("获取所有课程");
        return courseMapper.getAll();
    }


    public Result testBasicSECKILL(int cid, boolean lock) {
        reset(cid);
        int num = 1000;
        final CountDownLatch latch = new CountDownLatch(num);//N个购买者
        LOGGER.info("开始抢课{}(会出现超卖)",lock?"二":"一");
        for(int i=0;i<num;i++){
            final int sid = i;
            executor.execute(() -> {
                Result result;
                if (lock){
                     result = startSECKILLLock(cid, sid);
                }
                else result = startSECKILL(cid, sid);

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

    public Result ppcSECKILL(int cid) {
        int num = 1000;
        final CountDownLatch latch = new CountDownLatch(num);//N个购买者
        reset(cid);
        LOGGER.info("开始抢课五(正常、数据库锁最优实现)");
        for(int i=0;i<1000;i++){
            final int sid = i;
            Runnable task = () -> {
                Result result = startSECKILLAOPLock(cid, sid);
                LOGGER.info("用户:{}{}",sid,result.get("msg"));
                latch.countDown();
            };
            executor.execute(task);
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


    public Result aopSECKILL(int cid) {
        reset(cid);
        int num = 1000;
        final CountDownLatch latch = new CountDownLatch(num);//N个购买者
        LOGGER.info("运用AOP锁抢课开始");
        for(int i=0;i<num;i++){
            final int sid = i;
            executor.execute(() -> {
                Result result = startSECKILLAOPLock(cid, sid);
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


    @Transactional
    @ServiceLock
    public Result startSECKILLAOPLock(int cid, int sid) {
        int number = courseMapper.getNum(cid);
        if(number>0){
            courseMapper.minusOne(cid);
            courseMapper.addStudentCourse(cid, sid, 1);
        }else{
            return Result.error(SECKILLStatEnum.END);
        }
        return Result.ok(SECKILLStatEnum.SUCCESS);
    }

    @Transactional
    public Result startSECKILLLock(int cid, int sid) {
        try {
            lock.lock();
            int number = courseMapper.getNum(cid);
            if(number>0){
                courseMapper.minusOne(cid);
                courseMapper.addStudentCourse(cid, sid, 1);
            }else{
                return Result.error(SECKILLStatEnum.END);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        //释放锁后返回抢课成功信息
        return Result.ok(SECKILLStatEnum.SUCCESS);
    }


    @Transactional
    @ServiceLimit   //去掉即是最普通的实现
    public Result startSECKILL(int cid, int sid) {
        int number = courseMapper.getNum(cid);
        if(number>0){
            courseMapper.minusOne(cid);
            courseMapper.addStudentCourse(cid, sid, 1);
            return Result.ok(SECKILLStatEnum.SUCCESS);
        }else{
            return Result.error(SECKILLStatEnum.END);
        }
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
