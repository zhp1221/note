# JWT

# 基本用法

导包

~~~xml
  <!--引入jwt-->
        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
            <version>3.4.0</version>
        </dependency>
~~~

创建token

~~~java
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
~~~

验证token

> getClaim中的asString，asInt，根据前面放入的类型选择用哪个

```java
void verifyToken(){
    JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("123")).build();
    DecodedJWT verify = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJudW0iOjEyMywiZXhwIjoxNjMzNzQ3Nzk0LCJ1c2VyaWQiOiIxMjMiLCJ1c2VybmFtZSI6InhpYW9jaGVuIn0.FsyyODenj_lGUZ-6LsnRETw3uTCWdmpDXo3hIkPctUA");
    System.out.println(verify.getClaim("userid").asString());
    System.out.println(verify.getClaim("username").asString());
    System.out.println(verify.getClaim("num").asInt());
}
```

# Springboot

entity

~~~java
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class User {
    String id;
    String name;
    String password;
}
~~~

JWTUtils

~~~java
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
~~~



controller

> post请求，用postman在header中添加token信息
>
> 如若单纯使用浏览器不行，因为没有Header头信息

~~~java
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
~~~

interceptor

~~~java
public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, Object> map = new HashMap<>();
        //获取请求头中令牌
        String token = request.getHeader("token");
        try {
            JWTUtils.verify(token);//验证令牌
            return true;//放行请求
        } catch (SignatureVerificationException e) {
            e.printStackTrace();
            map.put("msg","无效签名!");
        }catch (TokenExpiredException e){
            e.printStackTrace();
            map.put("msg","token过期!");
        }catch (AlgorithmMismatchException e){
            e.printStackTrace();
            map.put("msg","token算法不一致!");
        }catch (Exception e){
            e.printStackTrace();
            map.put("msg","token无效!!");
        }
        map.put("state",false);//设置状态
        //将map 专为json  jackson
        String json = new ObjectMapper().writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}
~~~

config

~~~java
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/user/test")         //其他接口token验证
                .excludePathPatterns("/user/login");  //所有用户都放行
    }
}
~~~

