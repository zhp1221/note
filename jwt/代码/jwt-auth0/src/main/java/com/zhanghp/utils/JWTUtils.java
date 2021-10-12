package com.zhanghp.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * @author: zhanghp
 * @version: 1.0
 */
public class JWTUtils {
    private static final String SINGNATURE = "!@#(*&shdf123";

    public static String getToken(Map<String,String> map){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,7);
        // 创建JWT builder
        JWTCreator.Builder builder = JWT.create();
        // payload
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        String token = builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SINGNATURE));
        return token;
    }

    /**
     * 验证token合法性
     * @param token
     * @return
     */
    public static DecodedJWT verify(String token){
        return JWT.require(Algorithm.HMAC256(SINGNATURE)).build().verify(token);
    }
}
