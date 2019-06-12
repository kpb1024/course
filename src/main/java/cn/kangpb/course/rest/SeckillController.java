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

    @ApiOperation(value = "最基础的抢课功能实现")
    @PostMapping
    public Result basicSECKILL(int cid) {
        //模仿1000人抢课
        return courseService.testBasicSECKILL(cid);
    }

}
