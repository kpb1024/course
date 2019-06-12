package cn.kangpb.course.pojo;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Getter
@Setter
@Alias("Course")
public class Course {
    private int cid;
    private String cname;
    private int courseterm;
    private int courseyear;
    private int coursepoint;
    private String coursetype;
    private String tname;
    private int coursevolume;
}
