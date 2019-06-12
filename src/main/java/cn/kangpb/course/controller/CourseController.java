package cn.kangpb.course.controller;

import cn.kangpb.course.dao.CourseMapper;
import cn.kangpb.course.pojo.Course;
import cn.kangpb.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String index(Model model){
        List<Course> courses = courseService.getAll();
        model.addAttribute("courses", courses);
        return "index";
    }

}
