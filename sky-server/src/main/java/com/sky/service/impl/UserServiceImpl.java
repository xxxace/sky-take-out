package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 获取openId
        String openid = getOpenid(userLoginDTO.getCode());

        // 判断是否有openId，没用则失败，抛出业务异常
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 有则查找是否有这个openId的用户
        User user = userMapper.getUserByOpenid(openid);
        // 没用则自动创建
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.insert(user);
        }
        // 返回
        return user;
    }

    private String getOpenid(String code){
        HashMap<String, String> query = new HashMap<>();
        query.put("appid", weChatProperties.getAppid());
        query.put("secret", weChatProperties.getSecret());
        query.put("js_code", code);
        query.put("grant_type", "authorization_code");
        String responseJson = HttpClientUtil.doGet(WX_LOGIN, query);

        JSONObject jsonObject = JSONObject.parseObject(responseJson);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
