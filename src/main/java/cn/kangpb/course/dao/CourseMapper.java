package cn.kangpb.course.dao;

import cn.kangpb.course.pojo.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CourseMapper {
    @Select("select * from course")
    List<Course> getAll();

    @Select("select coursevolume from course where cid = #{cid}")
    int getNum(int cid);

    @Update("update course set coursevolume = coursevolume-1 where cid = #{cid}")
    int minusOne(int cid);

    @Insert("insert into success(cid,sid,state) values(#{cid},#{sid},#{state})")
    int addStudentCourse(int cid, int sid, int state);

    @Select("select count(*) from success where cid = #{cid}")
    int getSECKILLCount(int cid);

    @Update("update course set coursevolume = 100 where cid = #{cid}")
    void resetCourseVolume(int cid);

    @Delete("delete from success where cid = #{cid}")
    void resetSuccess(int cid);
}
