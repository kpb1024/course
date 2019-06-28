package cn.kangpb.course.controller;

import cn.kangpb.course.dao.CourseMapper;
import cn.kangpb.course.pojo.Course;
import cn.kangpb.course.service.CourseService;
import cn.kangpb.course.service.DistributedCourseService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/course")
public class CourseController {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseService courseService;
    @Autowired
    private DistributedCourseService distributedCourseService;

    @GetMapping
    public String index(Model model){
        List<Course> courses = courseService.getAll();
        model.addAttribute("courses", courses);
        return "index";
    }

    @GetMapping(value = "getCourse/{cid}")
    public String getCourse(@PathVariable("cid") int cid, Model model) {
        distributedCourseService.zkSECKILL(cid);
        int sid = new Random().nextInt(1000);
        model.addAttribute("sid", sid);
        if (courseMapper.isSuccess(cid, sid) != null){
            model.addAttribute("result", "抢课成功");
        } else {
            model.addAttribute("result", "抢课失败");
        }
        distributedCourseService.reset(cid);
        return "getCourse";
    }

}
