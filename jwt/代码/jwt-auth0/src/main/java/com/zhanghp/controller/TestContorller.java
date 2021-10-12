package com.zhanghp.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zhanghp.entity.User;
import com.zhanghp.utils.JWTUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhanghp
 * @version: 1.0
 */
@RestController
public class TestContorller {
    @GetMapping("user/login")
    public Map<String, Object> test(User user,HttpServletRequest request){
        // 数据库省略
        User user1 = new User("1","zhangsan","123");
        // 发送给前端的数据
        HashMap<String, Object> map = new HashMap<>();
        try {
            HashMap<String, String> payload = new HashMap<>();
            payload.put("id",user1.getId());
            payload.put("name",user1.getName());
            String token = JWTUtils.getToken(payload);
            // 返回的数据
            map.put("state",true);
            map.put("msg","认证成功");
            map.put("token",token);
        } catch (Exception e) {
            map.put("state",false);
            map.put("msg",e.getMessage());
        }
        return map;
    }

    @PostMapping("/user/test")
    public Map<String,Object> test(HttpServletRequest request){
        HashMap<String, Object> map = new HashMap<>();

        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        map.put("state",true);
        map.put("msg","请求成功");
        return map;
    }
}
