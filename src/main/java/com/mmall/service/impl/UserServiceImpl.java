package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.IUserMapper;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @title
 * @Author huangjiarui
 * @date: 2018-06-20
 */

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IUserMapper iUserMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int i = iUserMapper.checkUsername(username);
        if (i == 0) {
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        //todo 密码登陆MD5
        String s = MD5Util.MD5EncodeUtf8(password);

        User user = iUserMapper.selectLogin(username, s);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误！");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {

        ServerResponse<String> validu = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validu.isSuccess()) {
            return validu;
        }

        ServerResponse<String> valide = this.checkValid(user.getUsername(), Const.EMAIL);
        if (!valide.isSuccess()) {
            return valide;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int insert = userMapper.insert(user);
        if (insert == 0) {
            return ServerResponse.createByErrorMessage("注册失败！");
        }
        return ServerResponse.createBySuccessMessage("注册成功！");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //开始校验
            if (Const.USERNAME.equals(type)) {
                int i = iUserMapper.checkUsername(str);
                if (i > 0) {
                    return ServerResponse.createByErrorMessage("用户已存在！");
                }
            }

            if (Const.USERNAME.equals(type)) {
                int checkEmail = iUserMapper.checkEmail(str);
                if (checkEmail > 0) {
                    return ServerResponse.createByErrorMessage("Email已存在！");
                }
            }

        } else {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return ServerResponse.createBySuccessMessage("校验成功！");
    }

    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
        ServerResponse serverResponse = this.checkValid(username, Const.USERNAME);
        if (serverResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        String questionByUsername = iUserMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(questionByUsername)) {
            return ServerResponse.createBySuccessMessage(questionByUsername);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的！");
    }

    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int checkAnswer = iUserMapper.checkAnswer(username, question, answer);
        if (checkAnswer > 0) {
            //说明校验正确的,生成一个token
            String forgetToken = UUID.randomUUID().toString();
            //将forgetToken存入TokenCache中
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误！");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，需要传递token");
        }
        ServerResponse serverResponse = this.checkValid(username, Const.USERNAME);
        if (serverResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或过期！");
        }
        if (StringUtils.equals(forgetToken, token)) {
            String encodeUtf8 = MD5Util.MD5EncodeUtf8(passwordNew);
            int updatePasswordByUsername = iUserMapper.updatePasswordByUsername(username, encodeUtf8);
            if (updatePasswordByUsername > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功！");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token。");
        }
        return ServerResponse.createByErrorMessage("修改密码失败！");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count(1)，如果不指定id，那么结果就是true了
        int checkPassword = iUserMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if (checkPassword == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误！");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateByPrimaryKeySelective = userMapper.updateByPrimaryKeySelective(user);
        if (updateByPrimaryKeySelective > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功！");
        }
        return ServerResponse.createByErrorMessage("密码更新失败！");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username 不能被更新
        //email需要进行一次校验，校验新的email是不是已经存在，并且存在的email如果相同的话，不能使我们当前这个用户的
        int checkEmailByUserId = iUserMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (checkEmailByUserId > 0) {
            return ServerResponse.createByErrorMessage("email已经存在，请更换email再尝试更新！");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setUpdateTime(new Date());

        int updateByPrimaryKeySelective = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateByPrimaryKeySelective > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败！");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPhone(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
