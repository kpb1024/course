package cn.kangpb.course.rest;

import cn.kangpb.course.service.CourseService;
import cn.kangpb.course.service.DistributedCourseService;
import cn.kangpb.course.service.Result;
import cn.kangpb.course.utils.kafka.KafkaSender;
import cn.kangpb.course.utils.redis.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Api(tags = "分布式锁")
@RestController
@RequestMapping("/distributedCourse")
public class DistributedSECKILLController {
    private final static Logger LOGGER = LoggerFactory.getLogger(DistributedSECKILLController.class);
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize+1,
            10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000));

    @Autowired
    private CourseService courseService;
    @Autowired
    private DistributedCourseService distributedCourseService;
    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation(value = "Zookeeper锁")
    @GetMapping("/ZookeeperSECKILL/{cid}")
    public String ZookeeperSECKILL(@PathVariable("cid") int cid) {
        distributedCourseService.zkSECKILL(cid);
        return "result";
    }

    @ApiOperation(value = "Kafka分布式队列")
    @PostMapping("/KafkaSECKILL")
    public Result KafkaSECKILL(int cid) {
        courseService.reset(cid);
        LOGGER.info("开始Kafka分布式队列抢课！！！");
        for (int i = 0; i < 1000; i++) {
            final int sid = i;
            Runnable task = (() -> {
               if (redisUtil.getValue(cid+"") == null) {
                   kafkaSender.sendChannelMess("seckill", cid+";"+sid);
               }
            });
            executor.execute(task);
        }
        try {
            Thread.sleep(10000);
            redisUtil.cacheValue(cid+"",null);
            int count = courseService.getSECKILLCount(cid);
            LOGGER.info("*****************************");
            LOGGER.info("*****************************");
            LOGGER.info("一共秒杀出{}件商品",count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }


}
