package cn.kangpb.course.rest;

import cn.kangpb.course.service.CourseService;
import cn.kangpb.course.service.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "RestFul Apis")
@RestController
@RequestMapping("/course")
public class SeckillController {


    @Autowired
    private CourseService courseService;

    @ApiOperation(value = "基础实现+限流注解")
    @PostMapping("/basic")
    public Result basicSECKILL(int cid) {
        return courseService.testBasicSECKILL(cid, false);
    }

    @ApiOperation(value = "本机锁")
    @PostMapping("basicLock")
    public Result basicSECKILLWithLock(int cid) {

        return courseService.testBasicSECKILL(cid, true);
    }

    @ApiOperation(value = "AOP锁实现")
    @PostMapping("/AOPLock")
    public Result AOPLock(int cid) {
        return courseService.aopSECKILL(cid);
    }

    @ApiOperation(value = "悲观锁")
    @PostMapping("/Pessimistic")
    public Result pessimisticConcurrencyControl(int cid) {
        return courseService.ppcSECKILL(cid);
    }

}
