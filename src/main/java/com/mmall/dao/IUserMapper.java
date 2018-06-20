package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

/**
 * @title
 * @Author huangjiarui
 * @date: 2018-06-20
 */
public interface IUserMapper {

    int checkUsername(String username);

    int checkEmail(String email);

    int checkEmailByUserId(@Param("email") String email,@Param("userId")Integer userId);

    int checkPassword(@Param("userId") Integer userId , @Param("password") String password);

    int checkAnswer(@Param("username")String username,@Param("question") String question,@Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username , @Param("passwordNew") String passwordNew);

    User selectLogin(@Param("username") String username , @Param("password") String password);

    String selectQuestionByUsername(String username);
}
