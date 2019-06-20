package cn.kangpb.course.rest;

import cn.kangpb.course.service.CourseService;
import cn.kangpb.course.service.DistributedCourseService;
import cn.kangpb.course.service.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "分布式锁")
@RestController
@RequestMapping("/distributedCourse")
public class DistributedSeckillController {


    @Autowired
    private DistributedCourseService distributedCourseService;

    @ApiOperation(value = "基础实现+限流注解")
    @PostMapping("/ZookeeperSEKILL")
    public Result ZookeeperSEKILL(int cid) {
        return distributedCourseService.zkSECKILL(cid);
    }


}
