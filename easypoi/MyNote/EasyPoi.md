# EasyPoi

## EasyPoi简介

[官方网址](http://doc.wupaas.com/docs/easypoi/easypoi-1c0u6ksp2r091)

> easypoi功能如同名字easy,主打的功能就是容易,让一个没见接触过poi的人员 就可以方便的写出Excel导出,Excel模板导出,Excel导入,Word模板导出,通过简单的注解和模板 语言(熟悉的表达式语法),完成以前复杂的写法

### 优势

- 基于注解的导入导出,修改注解就可以修改Excel
- 支持常用的样式自定义
- 基于map可以灵活定义的表头字段
- 支持一对多的导出,导入
- 支持模板的导出,一些常见的标签,自定义标签
- 支持HTML/Excel转换,如果模板还不能满足用户的变态需求,请用这个功能
- 支持word的导出,支持图片,Excel

### 选择easypoi原因

> easypoi在apache的poi的基础上进行了封装，使代码层面简化很多，不在冗余

## EasyPOI使用

### 所需依赖

> - 1.easypoi-base 导入导出的工具包,可以完成Excel导出,导入,Word的导出,Excel的导出功能
> - 2.easypoi-web 耦合了spring-mvc 基于AbstractView,极大的简化spring-mvc下的导出功能
> - 3.easypoi-annotation 基础注解包,作用与实体对象上,拆分后方便maven多工程的依赖管理

~~~xml
 		<dependency>
            <groupId>cn.afterturn</groupId>
            <artifactId>easypoi-base</artifactId>
            <version>4.1.0</version>
        </dependency>
        <dependency>
            <groupId>cn.afterturn</groupId>
            <artifactId>easypoi-web</artifactId>
            <version>4.1.0</version>
        </dependency>
        <dependency>
            <groupId>cn.afterturn</groupId>
            <artifactId>easypoi-annotation</artifactId>
            <version>4.1.0</version>
        </dependency>
~~~

### 注解

~~~markdown
# 1.注解说明
- easypoi起因就是Excel的导入导出,最初的模板是实体和Excel的对应,model–row,filed–col 这样利用注解我们 	 可以和容易做到excel到导入导出
~~~

- **@Excel** 作用到filed上面,是对Excel一列的一个描述
- **@ExcelCollection** 表示一个集合,主要针对一对多的导出,比如一个老师对应多个科目,科目就可以用集合表示
- **@ExcelEntity** 表示一个继续深入导出的实体，例如一个实体类中的属性也是实体类时
- **@ExcelIgnore** 和名字一样表示这个字段被忽略跳过这个导出
- **@ExcelTarget** 这个是作用于最外层的对象,描述这个对象的id,以便支持一个对象可以针对不同导出做出不同处理

~~~markdown
# 2.注解属性详解
~~~

#### @Excel(常用属性)

| 属性           | 类型     | 默认值           | 功能                                                         |
| :------------- | :------- | :--------------- | :----------------------------------------------------------- |
| name           | String   | null             | 列名,支持name_id                                             |
| needMerge      | boolean  | fasle            | 是否需要纵向合并单元格(用于含有list中,单个的单元格,合并list创建的多个row) |
| orderNum       | String   | “0”              | 列的排序,支持name_id                                         |
| replace        | String[] | {}               | 值得替换 导出是{a_id,b_id} 导入反过来                        |
| savePath       | String   | “upload”         | 导入文件保存路径,如果是图片可以填写,默认是upload/className/ IconEntity这个类对应的就是upload/Icon/ |
| type           | int      | 1                | 导出类型： 1 是文本 2 是图片,3 是函数,10 是数字 默认是文本   |
| width          | double   | 10               | 列宽                                                         |
| height         | double   | 10               | **列高,后期打算统一使用[@ExcelTarget](https://github.com/ExcelTarget)的height,这个会被废弃,注意** |
| isImportField  | boolean  | true             | 校验字段,看看这个字段是不是导入的Excel中有,如果没有说明是错误的Excel,读取失败,支持name_id |
| exportFormat   | String   | “”               | 导出的时间格式,以这个是否为空来判断是否需要格式化日期        |
| importFormat   | String   | “”               | 导入的时间格式,以这个是否为空来判断是否需要格式化日期        |
| format         | String   | “”               | 时间格式,相当于同时设置了exportFormat 和 importFormat        |
| databaseFormat | String   | “yyyyMMddHHmmss” | 导出时间设置,如果字段是Date类型则不需要设置 数据库如果是string 类型,这个需要设置这个数据库格式,用以转换时间格式输出 |
| numFormat      | String   | “”               | 数字格式化,参数是Pattern,使用的对象是DecimalFormat           |
| imageType      | int      | 1                | 导出类型 1 从file读取 2 是从数据库中读取 默认是文件 同样导入也是一样的 |
| suffix         | String   | “”               | 文字后缀,如% 90 变成90%                                      |

#### @ExcelCollection

| 属性     | 类型     | 默认值          | 功能                     |
| :------- | :------- | :-------------- | :----------------------- |
| id       | String   | null            | 定义ID                   |
| name     | String   | null            | 定义集合列名,支持nanm_id |
| orderNum | int      | 0               | 排序,支持name_id         |
| type     | Class<?> | ArrayList.class | 导入时创建对象使用       |

#### @ExcelEntity

| 属性 | 类型   | 默认值 | 功能   |
| :--- | :----- | :----- | :----- |
| id   | String | null   | 定义ID |

#### @ExcelTarget

| 属性 | 类型 | 默认值 | 功能 |
| :--- | :----- | :----- | :----- |
|value | String | null | 定义ID|
|height | double | 10 | 设置行高|
|fontSize | short | 11 | 设置文字大小|

## 导出Excel

### 注意事项

> + 导出的excel对象需实现序列化接口
>
> + easypoi获取excel对象的属性值，通过getter方法获取

### 导出基本数据类型

### 导出指定字段

### 导出对象中list集合属性

### 导出对象中的对象属性

### 导出一对多关系

### 导出图片

### 导出大批量数据

## 导入Excel

