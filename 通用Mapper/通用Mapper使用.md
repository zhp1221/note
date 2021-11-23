# 通用Mapper使用

[TOC]

[通用Mapper官方网站](https://mapperhelper.github.io/docs/)

## 常用方法

### Select

| 方法                             | 说明                                                         |
| -------------------------------- | ------------------------------------------------------------ |
| List select(T record)            | 根据实体中的属性值进行查询，查询条件使用等号                 |
| T selectByPrimaryKey(Object key) | 根据主键字段进行查询，方法参数必须包含完整的主键属性，查询条件使用等号 |
| T selectOne(T record)            | 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号 |
| int selectCount(T record)        | 根据实体中的属性查询总数，查询条件使用等号                   |

### Insert

| 方法                          | 说明                                                   |
| ----------------------------- | ------------------------------------------------------ |
| int insert(T record)          | 保存一个实体，null的属性也会保存，不会使用数据库默认值 |
| int insertSelective(T record) | 保存一个实体，null的属性不会保存，会使用数据库默认值   |

### Update

| 方法                                      | 说明                                     |
| ----------------------------------------- | ---------------------------------------- |
| int updateByPrimaryKey(T record)          | 根据主键更新实体全部字段，null值会被更新 |
| int updateByPrimaryKeySelective(T record) | 根据主键更新属性不为null的值             |

### Delete

| 方法                               | 说明                                               |
| ---------------------------------- | -------------------------------------------------- |
| int delete(T record)               | 根据实体属性作为条件进行删除，查询条件使用等号     |
| int deleteByPrimaryKey(Object key) | 根据主键字段进行删除，方法参数必须包含完整的主键属 |

### Example 方法

> 还可延伸Creteria获取更多的方法调用
>
> ~~~java
> Example example = new Example(AppointManagement.class);
> Example.Criteria criteria = example.createCriteria();
> ~~~

| 方法                                                         | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| List selectByExample(Object example)                         | 根据Example条件进行查询                                      |
| int selectCountByExample(Object example)                     | 根据Example条件进行查询总数                                  |
| int updateByExample(@Param("record") T record, @Param("example") Object example) | 根据Example条件更新实体`record`包含的全部属性，null值会被更新 |
| int updateByExampleSelective(@Param("record") T record, @Param("example") Object example) | 根据Example条件更新实体`record`包含的不是null的属性值        |
| int deleteByExample(Object example)                          | 根据Example条件删除数据                                      |

第一个方发的额外补充：

> selectByExample(Object example)
>
> 这个查询支持通过`Example`类指定查询列， 通过`selectProperties`方法指定查询列

## 快速上手

> 使用mysql数据库，springboot，配置略

### **依赖**

~~~xml
		<!--通用Mapper启动器-->
		<dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper-spring-boot-starter</artifactId>
            <version>2.1.5</version>
        </dependency>
		<!--通用Mapper依赖-->
        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper</artifactId>
            <version>3.4.6</version>
        </dependency>
~~~

### 代码

**bean**

~~~java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_word")
public class TWord {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    @Column(name = "name")
    private String name;
}
~~~

**mapper接口**

> 注意Mapper<>的包
>
> ```
> import tk.mybatis.mapper.common.Mapper;
> ```

~~~java
import tk.mybatis.mapper.common.Mapper;

public interface TWordMapper extends Mapper<TWord> {
    List<TWord> select();
}
~~~

**调用**

~~~java
@SpringBootTest
class TkMapperApplicationTests {
    @Autowired
    private TWordMapper tWordMapper;

    @Test
    void contextLoads() {
        List<TWord> tWords = tWordMapper.selectAll();
        for (TWord tWord : tWords) {
            System.out.println(tWord);
        }
    }

}
~~~

