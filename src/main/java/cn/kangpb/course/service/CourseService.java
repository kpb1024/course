package cn.kangpb.course.service;

import cn.kangpb.course.dao.CourseMapper;
import cn.kangpb.course.pojo.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class CourseService {
    private final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1, 10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired private CourseMapper courseMapper;

    @Transactional(readOnly = true)
    public List<Course> getAll(){
        LOGGER.info("获取所有课程");
        return courseMapper.getAll();
    }


    public Result testBasicSECKILL(int cid) {
        reset(cid);
        int num = 1000;
        final CountDownLatch latch = new CountDownLatch(num);//N个购买者
        reset(cid);
        final long CourseId = cid;
        LOGGER.info("开始秒杀一(会出现超卖)");
        for(int i=0;i<num;i++){
            final int sid = i;
            executor.execute(() -> {
                Result result = startSECKILL(cid, sid);
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
    public Result startSECKILL(int cid, int sid) {
        int number = courseMapper.getNum(cid);
        if(number>0){
            courseMapper.minusOne(cid);
            courseMapper.addStudentCourse(cid, sid, 1);
            //支付
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
