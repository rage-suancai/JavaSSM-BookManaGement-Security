package book.manager.mapper;

import book.manager.entity.Book;
import book.manager.entity.Borrow;
import book.manager.entity.BorrowDetails;
import org.apache.ibatis.annotations.*;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.List;

@Mapper
public interface BookMapper {

    @Select("select * from book") // 查询所有书籍信息
    List<Book> allBook();

    @Delete("delete from book where bid = #{bid}") // 删除书籍信息
    void deleteBook(int bid);

    @Insert("insert into book(title, `desc`, price) values(#{title}, #{desc}, #{price})") // 添加书籍信息
    void addBook(@Param("title") String title, @Param("desc") String desc, @Param("price") double price);

    @Insert("insert into borrow(bid, sid, `time`) values(#{bid}, #{sid}, NOW())") // 添加借阅信息
    void addBorrow(@Param("bid") int bid, @Param("sid") int sid);

    @Delete("delete from borrow where bid = #{bid} and sid = #{sid}") // 删除借阅信息
    void deleteBorrow(@Param("bid") int bid, @Param("sid") int sid);

    @Select("select * from borrow") // 查询书籍借阅信息
    List<Borrow> BorrowList();
    @Select("select * from borrow where sid = #{sid}")
    List<Borrow> borrowListBySid(int sid);
    @Select("select * from book where bid = #{bid}")
    Book getBookById(int bid);

    @Results({ // 借阅 学生信息联查
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "title", property = "book_title"),
            @Result(column = "name", property = "user_name"),
            @Result(column = "time", property = "time"),
    })
    @Select("select * from borrow left join book on book.bid = borrow.bid " +
            "                     left join student on borrow.sid = student.sid")
    List<BorrowDetails> borrowDetailsList();

    @Select("select count(*) from book") // 查询书籍数量
    int getBookCount();

    @Select("select count(*) from borrow") // 查询借阅数量
    int getBorrowCount();

}
