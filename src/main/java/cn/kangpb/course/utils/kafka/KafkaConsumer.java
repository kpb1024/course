package cn.kangpb.course.utils.kafka;

import cn.kangpb.course.service.CourseService;
import cn.kangpb.course.service.Result;
import cn.kangpb.course.service.SECKILLStatEnum;
import cn.kangpb.course.utils.redis.RedisUtil;
import cn.kangpb.course.utils.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @Autowired
    private CourseService courseService;

    private static RedisUtil redisUtil = new RedisUtil();

    @KafkaListener(topics = {"seckill"})
    public void receiveMessage(String message) {
        if (message != null) {
            String[] array = message.split(";");
            Result result = courseService.startSECKILL(Integer.parseInt(array[0]), Integer.parseInt(array[1]));
            if (result == null) {
                return;
            }
            if (result.equals(Result.ok(SECKILLStatEnum.SUCCESS))) {
                WebSocketServer.sendInfo(array[0], "秒杀成功");
            } else {
                WebSocketServer.sendInfo(array[0], "秒杀失败");
                redisUtil.cacheValue(array[0],"ok");
            }
        }
    }
}
