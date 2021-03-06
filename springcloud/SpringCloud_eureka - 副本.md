# SpringCloud_Eureka

~~~markdown
# 服务注册与发现包括两部分，一个是服务器端，另一个是客户端。

- Server是一个公共服务，为Client提供服务注册和发现的功能，维护注册到自身的Client的相关信息，同时提供接口给Client获取注册表中其他服务的信息，使得动态变化的Client能够进行服务间的相互调用。

- lient将自己的服务信息通过一定的方式登记到Server上，并在正常范围内维护自己信息一致性，方便其他服务发现自己，同时可以通过Server获取到自己依赖的其他服务信息，完成服务调用，还内置了负载均衡器，用来进行基本的负载均衡。
~~~



# Eureka参数配置详解

https://www.jianshu.com/p/e2bebfb0d075

# 一. Eureka搭建

## 1. Eureka服务端,客户端搭建

1. **服务端**

+ 导包：eureka-server，spring-boot-stater-web

+ 启动器添加：@EnableEurekaServer

+ yml配置

  ~~~yml
  server:
    port: 8888
  eureka:
    client:
      service-url:
        defaultZone: http://127.0.0.1:8888/eureka
      fetch-registry: false
      register-with-eureka: false
  spring:
    application:
      name: eureka-server
  ~~~

2. **客户端**

+ 导包：eureka-client，spring-boot-stater-web

+ Hoxton.SR3后的版本不需要添加@EnableEurekaClient

+ yml配置

  ~~~yml
  server:
    port: 8081
  eureka:
    client:
      service-url:
        defaultZone: http://127.0.0.1:8888/eureka
  spring:
    application:
      name: eureka-client
  ~~~

## 2. 搭建双节点服务端Eureka

1. host主机名

   > 如果不配置主机名
   >
   > 1. yml则需用127.0.0.1,而不是相应的eureka1.com和eureka2.com
   > 2. 直接使用ip地址，会出现unavailable-replicas（部分服务不可用）

![image-20210928131033532](Ming_SpringCloud_eureka.assets/image-20210928131033532.png)

2. ---

   yml配置

~~~yml
---
spring:
  profiles: 8090
eureka:
  instance:
    hostname: eureka1.com
  client:
    serviceUrl:
      defaultZone: http://eureka2.com:8091/eureka/ 
server:
  port: 8090
---
spring:
  profiles: 8091
eureka:
  instance:
    hostname: eureka2.com
  client:
    serviceUrl:
      defaultZone: http://eureka1.com:8090/eureka/
server:
  port: 8091

~~~

3. ---

   Edit Configurations

![image-20210928130443502](Ming_SpringCloud_eureka.assets/image-20210928130443502.png)

4. ---

   需要两个节点都启动，一个节点启动会报错

+ **8090端口**：

![image-20211025120734842](SpringCloud_eureka - 副本.assets/image-20211025120734842.png)

+ **8091端口**：

![image-20211025120753231](SpringCloud_eureka - 副本.assets/image-20211025120753231.png)

## 3. 搭建三节点服务端Eureka（客户端集群也可这么搭）

> 1. host文件
> 2. defaultzone写：http://{instance.hostname}:{port}/eureka
> 3. 注册，拉取设置true
> 4. edit configuration设置

1. host文件

![image-20211001161328898](1.Ming_SpringCloud_eureka.assets/image-20211001161328898.png)

2. yml

   ~~~yml
   # 可写可不写这个属性都会成功
   spring:
     application:
       name: spring-cloud
   eureka:
     client:
       service-url:
         defaultZone: http://eureka1.com:8701/eureka/,http://eureka2.com:8702/eureka/,http://eureka3.com:8703/eureka/
       fetch-registry: true
       register-with-eureka: true
   ---
   eureka:
     instance:
       hostname: eureka1.com
   server:
     port: 8701
   spring:
     profiles: 8701
   ---
   eureka:
     instance:
       hostname: eureka2.com
   server:
     port: 8702
   spring:
     profiles: 8702
   ---
   eureka:
     instance:
       hostname: eureka3.com
   server:
     port: 8703
   spring:
     profiles: 8703
   ~~~
   
3. Edit Configurations

![image-20211001161439657](1.Ming_SpringCloud_eureka.assets/image-20211001161439657.png)

4. 页面显示：

![image-20211001161522845](1.Ming_SpringCloud_eureka.assets/image-20211001161522845.png)

+ 不写spring.appliation.name

![image-20211001162022178](1.Ming_SpringCloud_eureka.assets/image-20211001162022178.png)

## 4. 搭建三节点unavailable-replicas情况

### 4.1 为什么写上向注册中心注册就变成了available-replicas

> 如果写成false，则底层源码调用注册时注册不进来，app为空，导致返回false，如源码流程图

~~~yml
fetch-registry: true
register-with-eureka: true
~~~

### 4.2 available底层实现原理：

> available底层原理：**yml写的地址名和收到的注册表中的地址要一致**

![available-replicas底层原理](1.Ming_SpringCloud_eureka.assets/available-replicas底层原理.jpg)

### 4.3 unavailable-replicas所带来的的问题

> ip都正常，是没有问题
>
> 如果ip2网卡坏了，拉取的服务是ip2,会导致调不通的问题

<img src="1.Ming_SpringCloud_eureka.assets/未命名文件.jpg" alt="未命名文件" style="zoom:50%;" />

# 二. Eureka原理

## 1. Renew：心跳，续租 

> Eureka客户需要每30秒发送一次心跳来续租
>
> 10:00 00 第一次
>
> 10:00 30
>
> 10:01
>
> 10:01 30 最后
>
> 更新通知Eureka服务器实例仍然是活动的。如果服务器在90秒内没有看到更新，它将从其注册表中删除实例

~~~properties
#续约发送间隔默认30秒，心跳间隔
eureka.instance.lease-renewal-interval-in-seconds=5
# 续约到期时间（默认90秒）
eureka.instance.lease-expiration-duration-in-seconds=60
~~~

## 2. 自我保护

+ 机制

  > Eureka在CAP理论当中是属于AP ， 也就说当产生网络分区时，Eureka保证系统的可用性，但不保证系统里面数据的一致性
  >
  > 默认开启，服务器端容错的一种方式，即短时间心跳不到达仍不剔除服务列表里的节点

+ 自我保护机制引入

  > 默认情况下，Eureka Server在一定时间内，没有接收到某个微服务心跳，会将某个微服务注销（90S）。但是当网络故障时，微服务与Server之间无法正常通信，上述行为就非常危险，因为微服务正常，不应该注销。
  >
  > Eureka Server通过自我保护模式来解决整个问题，当Server在短时间内丢失过多客户端时，那么Server会进入自我保护模式，会保护注册表中的微服务不被注销掉。当网络故障恢复后，退出自我保护模式。

**思想：宁可保留健康的和不健康的，也不盲目注销任何健康的服务。**

#### 自我保护触发

**客户端每分钟续约数量小于客户端总数的85%时会触发保护机制**

> 为什么乘以 2：
> 默认情况下，注册的应用实例每半分钟续租一次，那么一分钟心跳两次，因此 x 2 。
>
> 服务实例数：10个，期望每分钟续约数：10 * 2=20，期望阈值：20*0.85=17，自我保护少于17时 触发。

+ 服务器端开启自我保护：

~~~properties
#关闭自我保护模式
eureka.server.enable-self-preservation=false
#失效服务间隔  eureka server清理无效节点的时间间隔，默认60000毫秒，即60秒
eureka.server.eviction-interval-timer-in-ms=3000
~~~

# 三. Eureka的Rest服务调用

官方文档

https://github.com/Netflix/eureka/wiki/Eureka-REST-operations

> 可通过postman软件，指定用get，post等方式等进行以下查看

## 1. 注册到eureka的服务信息查看

 get: {ip:port}/eureka/apps

## 2. 注册到eureka的具体的服务查看

 get: {ip:port}/eureka/apps/{appname}/{instanceId}

## 3. 服务续约

 put：{ip:port}/eureka/apps/{appname}/{instanceId}?lastDirtyTimestamp={}&status=up

## 4. 更改服务状态

 put：{ip:port}/eureka/apps/{appname}/{instanceId}/status?lastDirtyTimestamp={}&value={UP/DOWN}
 对应eureka源码的：InstanceResource.statusUpdate

## 5. 删除状态更新

 delete：{ip:port}/eureka/apps/{appname}/{instanceId}/status?lastDirtyTimestamp={}&value={UP/DOWN}

## 6. 删除服务

 delete: {ip:port}/eureka/apps/{appname}/{instanceId}

## 7. 更新元数据

PUT /eureka/eureka/apps/**appID**/**instanceID**/metadata?key=value

# 四. 元数据

>  Eureka的元数据有两种：标准元数据和自定义元数据。
> 标准元数据：主机名、IP地址、端口号、状态页和健康检查等信息，这些信息都会被发布在服务注册表中，用于服务之间的调用。
> 自定义元数据：可以使用eureka.instance.metadata-map配置，这些元数据可以在远程客户端中访问，但是一般不改变客户端行为，除非客户端知道该元数据的含义。
>
> 可以在配置文件中对当前服务设置自定义元数据，可后期用户个性化使用
>
> 元数据可以配置在eureka**服务器**和eureka的**客户端**上

~~~properties
eureka.instance.metadata-map.zhanghp=hp
~~~

+ 通过{ip:port}/eureka/status查看metadata

<img src="Ming_SpringCloud_eureka.assets/image-20210926195034633.png" alt="image-20210926195034633" style="zoom:50%;" />

+ 自定义元数据的作用

  > 调用服务之前，通过服务端{ip:port}/eureka/status，查询instanceInfo中的metadata，看这个机器是谁的

# 五. EurekaClient

+ DiscoveryClient

  > org.springframework.cloud.client.discovery定义用来服务发现的客户端接口，是客户端进行服务发现的核心接口，是spring cloud用来进行服务发现的顶级接口，在common中可以看到其地位。在Netflix Eureka和Consul中都有具体的实现类。

  ~~~java
  String description();//获取实现类的描述。
  List<String> getServices();//获取所有服务实例id。
  List<ServiceInstance> getInstances(String serviceId);//通过服务id查询服务实例信息列表。
  ~~~

+ LoadBalancerClient

  > LoadBalancerClient取=寻找服务的简单流程:
  > Ribbon 里面封装了 获取全部服务的接口(getallserverlists的方法), 其中通过filter过滤一部分服务,然后, Ribbon 的IRule对象, 选择一个有效的服务返回给调用方,(其中IRul默认是通过轮训的方式,返回一个服务).
  > springcloud提供了LoadBalancerClient,它继承了ribbon的接口.我们之间引入这个LoadBalancerClient就可以进行服务的查找

~~~java
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.client.RestTemplate;
@RestController
public class EurekaClientDemo {
    @Autowired
    DiscoveryClient discoveryClient;
    // 负载均衡客户端
    @Autowired
    LoadBalancerClient lb;
    
    @RequestMapping("/client")
    public String client() {
        // 获取所有的服务实例id
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            System.out.println(service);
        }
        return "client";
    }

    @RequestMapping("/client2")
    public Object client2() {
        // 通过服务id查询服务实例信息列表。
        List<ServiceInstance> instances = discoveryClient.getInstances("eureka-client");
        for (ServiceInstance instance : instances) {
            System.out.println(ToStringBuilder.reflectionToString(instance));
        }
        return discoveryClient.getInstances("eureka-client");
    }

    @RequestMapping("/client3")
    public String client3() {
        // ribbon 完成客户端的负载均衡，过滤掉down了的节点
        ServiceInstance instance = lb.choose("eureka-client");
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/client";
        System.out.println("url" + url);
        RestTemplate restTemplate = new RestTemplate();
        // 相当于httpclient，请求网址获得/client的返回值
        String respStr = restTemplate.getForObject(url, String.class);
        System.out.println("respStr:" + respStr);
        return "xxoo";
    }
}
~~~

# 六. Actuator监控应用

## 1. 开启监控

+ 服务端的包eureka-server中包含了actuator

+ 客户端导包

  ~~~xml
  <!-- 上报节点信息 -->
  <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
  ~~~

## 2. 默认端点

> Spring Boot 2.0 的Actuator只暴露了health和info端点，提供的监控信息无法满足我们的需求
>
> 在1.x中有n多可供我们监控的节点，官方的回答是为了安全….

## 3. 开启所有端点

yml配置（客户端，服务端都可）

~~~yml
management:
  endpoints:
    web:
      exposure:
        include: '*'
~~~

+ 通过控制台打印信息访问

![image-20210926211956622](Ming_SpringCloud_eureka.assets/image-20210926211956622.png)

------

所有端点开启后的api列表

~~~json
{"_links":{"self":{"href":"http://localhost:8081/actuator","templated":false},"beans":{"href":"http://localhost:8081/actuator/beans","templated":false},"caches-cache":{"href":"http://localhost:8081/actuator/caches/{cache}","templated":true},"caches":{"href":"http://localhost:8081/actuator/caches","templated":false},"health":{"href":"http://localhost:8081/actuator/health","templated":false},"health-path":{"href":"http://localhost:8081/actuator/health/{*path}","templated":true},"info":{"href":"http://localhost:8081/actuator/info","templated":false},"conditions":{"href":"http://localhost:8081/actuator/conditions","templated":false},"configprops":{"href":"http://localhost:8081/actuator/configprops","templated":false},"configprops-prefix":{"href":"http://localhost:8081/actuator/configprops/{prefix}","templated":true},"env":{"href":"http://localhost:8081/actuator/env","templated":false},"env-toMatch":{"href":"http://localhost:8081/actuator/env/{toMatch}","templated":true},"loggers":{"href":"http://localhost:8081/actuator/loggers","templated":false},"loggers-name":{"href":"http://localhost:8081/actuator/loggers/{name}","templated":true},"heapdump":{"href":"http://localhost:8081/actuator/heapdump","templated":false},"threaddump":{"href":"http://localhost:8081/actuator/threaddump","templated":false},"metrics-requiredMetricName":{"href":"http://localhost:8081/actuator/metrics/{requiredMetricName}","templated":true},"metrics":{"href":"http://localhost:8081/actuator/metrics","templated":false},"scheduledtasks":{"href":"http://localhost:8081/actuator/scheduledtasks","templated":false},"mappings":{"href":"http://localhost:8081/actuator/mappings","templated":false},"refresh":{"href":"http://localhost:8081/actuator/refresh","templated":false},"features":{"href":"http://localhost:8081/actuator/features","templated":false},"serviceregistry":{"href":"http://localhost:8081/actuator/serviceregistry","templated":false}}}
~~~

## 4.api端点功能

**1. Health**

> 会显示系统状态
>
> {"status":"UP"}

![image-20210926212127729](Ming_SpringCloud_eureka.assets/image-20210926212127729.png)

---

**2. shutdown**

> 用来关闭节点
>
> 开启远程关闭功能（运维能方便调用通过ip:port/actuator/shutdown就可关闭节点）
>
> 需要手动开启，必须是post请求

~~~yml
management:
    endpoint:
        shutdown:
          enabled: true
~~~

---

**3. configprops** 

> 获取应用中配置的属性信息报告 

---

**4. env** 

>  获取应用所有可用的环境属性报告 

---

**5. Mappings**

> 获取应用所有Spring Web的控制器映射关系报告

---

**6. info** 

>  获取应用自定义的信息 

---

**7. metrics**

> 返回应用的各类重要度量指标信息 
>
> **Metrics**节点并没有返回全量信息，我们可以通过不同的**key**去加载我们想要的值
>
>  metrics/jvm.memory.max

---

**8. Threaddump**

> 1.x中为**dump**
>
> 返回程序运行中的线程信息 

#  七. Eureka健康检查

> 由于server和client通过心跳保持 服务状态，而只有状态为UP的服务才能被访问。看eureka界面中的status。
>
> 比如心跳一直正常，服务一直UP，但是此服务DB连不上了，无法正常提供服务。
>
> 此时，我们需要将微服务的健康状态也同步到server。只需要启动eureka的健康检查就行。这样微服务就会将自己的健康状态同步到eureka。配置如下即可。

> 心跳正常，不干活，所以需要开启健康检查

## 1. 开启手动控制

> 在client端配置：将自己真正的健康状态传播到server。

~~~yml
eureka:
  client:
    healthcheck:
      enabled: true
~~~

## 2. EurekaClient导入actuator

~~~xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
~~~

## 3. 改变健康状态的service

~~~java
@Service
public class HealthStatusService implements HealthIndicator{

	private Boolean status = true;

	public void setStatus(Boolean status) {
		this.status  = status;
	}

	@Override
	public Health health() {
		if(status)
		return new Health.Builder().up().build();
		return new Health.Builder().down().build();
	}

	public String getStatus() {
		return this.status.toString();
	}
~~~

## 4. controller

~~~java
	// service层注入
	@Autowired
    HealthStatusService hss;
	@GetMapping("/health")
	public String health(@RequestParam("status") Boolean status) {
		
		hss.setStatus(status);
		return hss.getStatus();
	}
~~~

## 5. 调用后服务端的界面

+ 输入网址

![image-20210926215937279](Ming_SpringCloud_eureka.assets/image-20210926215937279.png)

+ actuator/health界面

![image-20210926220037231](Ming_SpringCloud_eureka.assets/image-20210926220037231.png)

+ eureka-server界面

![image-20210926215839909](Ming_SpringCloud_eureka.assets/image-20210926215839909.png)

# 八. Eureka安全配置

## 1. springsecurity （服务端）

### 1.1  在服务端项目导包

```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
```

### 1.2  用户，密码设置

1. 方式一：yml配置

   ~~~properties
   spring.security.user.name=张三
   spring.security.user.password=123
   ~~~

2. 方式二：实现UserDetailsService接口

   ~~~java
   @Service
   public class UserDetailsServiceImpl implements UserDetailsService {
       @Autowired
       PasswordEncoder passwordEncoder;
       @Override
       public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
           if (!s.equals("zhangsan")) {
               throw new UsernameNotFoundException("找不到");
           }
           String encode = passwordEncoder.encode("123");
   
           return new User(s,encode, AuthorityUtils.commaSeparatedStringToAuthorityList("zhangsan"));
       }
   }
   ~~~

   + PasswordEncoder的bean注入

   ~~~java
   @Configuration
   public class MyPassEncoder {
       @Bean
       public PasswordEncoder passwordEncoder() {
           return new BCryptPasswordEncoder();
       }
   }
   ~~~

### 1.3 关闭csrf，实现跨域

> 客户端注册时，服务端的security是默认开启csrf防跨域攻击，导致注册失败

~~~java
@Configuration
public class MyWebSecurityConfiguerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }
}
~~~

## 2. SpringSecurity（客户端）

### yml配置（添加服务端的权限认证的角色，密码）

~~~yml
eureka:
  client:
    service-url:
      defaultZone: http://zhangsan:123@127.0.0.1:8888/eureka
~~~





