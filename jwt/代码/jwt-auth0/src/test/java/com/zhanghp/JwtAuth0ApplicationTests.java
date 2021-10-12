package com.zhanghp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;


class JwtAuth0ApplicationTests {

    @Test
    void createToken() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND,100);
        String token = JWT.create()
                .withClaim("userid","123")
                .withClaim("username","xiaochen")
                .withClaim("num",123)
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256("123"));
        System.out.println(token);
    }
    @Test
    void verifyToken(){
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("123")).build();
        DecodedJWT verify = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJudW0iOjEyMywiZXhwIjoxNjMzNzQ3Nzk0LCJ1c2VyaWQiOiIxMjMiLCJ1c2VybmFtZSI6InhpYW9jaGVuIn0.FsyyODenj_lGUZ-6LsnRETw3uTCWdmpDXo3hIkPctUA");
        System.out.println(verify.getClaim("userid").asString());
        System.out.println(verify.getClaim("username").asString());
        System.out.println(verify.getClaim("num").asInt());
    }
}
