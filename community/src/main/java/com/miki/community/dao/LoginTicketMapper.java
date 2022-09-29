package com.miki.community.dao;

import com.miki.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    //声明方法，并使用注解的形式来实现方法
    //语法为@Insert({"sql (写完一句sql加一个空格，防止自动拼接时出错)","sql "})

    @Insert({
            "insert into login_ticket (user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true ,keyProperty = "id")/*使用注解@Options自动生成id*/
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(@Param("ticket") String ticket);

    @Update({
            "<script> ",
            "update login_ticket set status = #{status} where ticket = #{ticket} ",
            "<if test=\"ticket!=null\">",/*这里双引号需要用\转义*/
            "and 1=1",
            "</if>",
            "</script> "
    })
    /*这种实现方式也可以实现动态SQL，只需在注解内加上<script></script>脚本标签，
    * 再在里面写<if></if>脚本，语法与xml中一致*/
    int updateStatus(@Param("ticket") String ticket, @Param("status") int status);

}
