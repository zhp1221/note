# RabbitMQ

# 安装erlang，rabbitmq

## 1.**Erlang安装**

yum源替换阿里（Linux）



​	RabbitMQ是使用Erlang语言编写的，所以需要先配置Erlang

#### 1 **修改主机名**

后过主机名进行访问的，必须指定能访问的主机名。

```
# vim /etc/sysconfig/network
```

![](D:/12/123/mca/Note/分布式资料/RabbitMQ/文档/RabbitMQ.assets/RabbitMQ-06.jpg)

```
# vim /etc/hosts
```

​	新添加了一行，前面为服务器ip，空格后面添加计算机主机名

![](D:/12/123/mca/Note/分布式资料/RabbitMQ/文档/RabbitMQ.assets/RabbitMQ-07.jpg)

重新启动

#### 2 **安装依赖**

```
# yum -y install make gcc gcc-c++ kernel-devel m4 ncurses-devel openssl-devel unixODBC unixODBC-devel
```

#### 3 **上传并解压**

​	上传otp_src_22.0.tar.gz到/usr/local/tmp目录中，进入目录并解压。

 	解压时注意，此压缩包不具有gzip属性，解压参数没有z，只有xf

```
# cd /usr/local/tmp
# tar xf otp_src_22.0.tar.gz
```

#### 4 **配置参数**

​	先新建/usr/local/erlang文件夹，作为安装文件夹

```
# mkdir -p /usr/local/erlang
```

​	 进入文件夹

```
# cd otp_src_22.0
```

​	 配置参数

```
# ./configure --prefix=/usr/local/erlang --with-ssl --enable-threads --enable-smp-support --enable-kernel-poll --enable-hipe --without-javac
```

####  5 **编译并安装**

​	编译 

```
# make
```

​	 安装

```
# make install
```

####  6 **修改环境变量**

​	修改/etc/profile文件

```
# vim /etc/profile
```

​	 在文件中添加下面代码 

```
export PATH=$PATH:/usr/local/erlang/bin
```

​	运行文件，让修改内容生效

```
# source /etc/profile
```

####  7 **查看配置是否成功**

```
# erl -version
```

![](D:/12/123/mca/Note/分布式资料/RabbitMQ/文档/RabbitMQ.assets/RabbitMQ-08.jpg)

## 2. **安装RabbitMQ**

#### 1 **上传并解压**

​	上传rabbitmq-server-generic-unix-3.7.18.tar.xz到/usr/loca/tmp中

```
# cd /usr/local/tmp
# tar xf rabbitmq-server-generic-unix-3.7.18.tar.xz
```

#### 2 **复制到local下**

​	复制解压文件到/usr/local下，命名为rabbitmq

```
# cp -r rabbitmq_server-3.7.18 /usr/local/rabbitmq
```

#### 3 **配置环境变量**

```
# vim /etc/profile
```

​	在文件中添加 

```
export PATH=$PATH:/usr/local/rabbitmq/sbin
```

​	解析文件

```
# source /etc/profile
```

####  4 **开启web管理插件**

​	进入rabbitmq/sbin目录

```
# cd /usr/local/rabbitmq/sbin
```

 	查看插件列表

```
# ./rabbitmq-plugins list
```

 	生效管理插件

```
# ./rabbitmq-plugins enable rabbitmq_management
```

####  5 **后台运行**

​	启动rabbitmq。

```
# ./rabbitmq-server -detached
```

​	停止命令，如果无法停止，使用kill -9 进程号进行关闭

```
# ./rabbitmqctl stop_app
```

#### 6 **查看web管理界面**

​	默认可以在安装rabbitmq的电脑上通过用户名：guest密码guest进行访问web管理界面

​	端口号：15672（放行端口，或关闭防火墙）

​	在虚拟机浏览器中输入：

​	<http://localhost:15672>

## 3. **RabbitMq账户管理**

#### 1 **创建账户**

​	语法：./rabbitmqctl add_user username password

```
# cd /usr/local/rabbitmq/sbin
# ./rabbitmqctl add_user zhanghp 123
```

#### 2 **给用户授予管理员角色**

​	其中smallming为新建用户的用户名

```
# ./rabbitmqctl set_user_tags zhanghp administrator
```

####  3 **给用户授权**

​	“/” 表示虚拟机

​	mashibing 表示用户名

​	".*" ".*" ".*" 表示完整权限

```
# ./rabbitmqctl set_permissions -p "/" zhanghp ".*" ".*" ".*"
```

####  4 **登录**

​	使用新建账户和密码在windows中访问rabbitmq并登录 

​	在浏览器地址栏输入：

​	ip为linux的网络

> 通过ifconfig查看

​	<http://ip:15672/>

 	用户名：zhanghp

​	密码：123

# 交换机

https://blog.csdn.net/qq_39240270/article/details/94202815

​	交换器负责接收客户端传递过来的消息，并转发到对应的队列中。在RabbitMQ中支持四种交换器

​	1.Direct Exchange：直连交换器（默认）

​	2.Fanout Exchange：扇形交换器

​	3.Topic Exchange：主题交换器

​	4.Header Exchange：首部交换器

## 创建consumer，provider

yml编写：

~~~properties
# linux服务器的ip（在linux启动rabbitmq）
spring.rabbitmq.host=192.168.2.104
spring.rabbitmq.username=zhanghp
spring.rabbitmq.password=123
~~~

## provider

@configuration

~~~java
@Configuration
public class DirectExchange_Config {
    @Bean
    public Queue queue(){
        return new Queue("myqueue1");
    }

    // 四种交换器
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("DIRECT_EXCHANGE");
    }
    // 队列绑定交换器
    @Bean
    public Binding bind(){
        return BindingBuilder.bind(queue()).to(directExchange()).with("key.1");
    }
   
}
~~~

@Test

~~~java
@SpringBootTest
class RabbotmqExchangeProviderApplicationTests {

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    void direct() {
        amqpTemplate.convertAndSend("DIRECT_EXCHANGE");
        System.out.println("发送成功");
    }
}
~~~

## consumer

@Component

~~~java
@Component
public class DemoReceive {
    @RabbitListener(queues = "myqueue")
    public void demo(String msg){
        System.out.println("收到消息11:"+msg);
    }
}
~~~

