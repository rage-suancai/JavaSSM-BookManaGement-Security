package book.manager.mapper;

import book.manager.entity.AuthUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from users where name = #{username}") // 查询用户账号信息
    AuthUser getPasswordByUsername(String username);

    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id") // 添加用户账号
    @Insert("insert into users(name, role, password) values(#{name}, #{role}, #{password})")
    int addRegisterUser(AuthUser user);
    @Insert("insert into student(uid, name, grade, sex) values(#{uid}, #{name}, #{grade}, #{sex})") // 添加学生信息
    int addStudentInfo(@Param("uid") int uid, @Param("name") String name, @Param("grade") String grade, @Param("sex") String sex);

    @Select("select sid from student where uid = #{uid}") // 查询学生学号
    Integer getSidByUserId(int uid);

    @Select("select count(*) from student") // 查询学生数量
    int getStudentCount();

}
