# Python

## pycharm软件

创建项目同时，配置python语言解释器interpreter

mac可通过终端输入：**python3**打开python解释器进行python编辑

## 数据类型api

~~~markdown
# string 不可变
- string.index(value)
- string.replace(source,targer)
- string[index]
# number
- 运算符：+ = * / % // **(幂)
- abs(),round(),pow(),ceil(),floor(),sqrt()开平方
# list 可变 []
- 列表元素不限制类型
- list[start:end] [strart,end)
- 增: list.extend() , append() , insert(index,value)
- 删: list.remove(value),clear,pop()
- 查: list.index(value),count(value)
- list.sort(),reverse(),copy()
# list含list，二维数组
[[],[]]
遍历：双重for循环
# 元组 不可变 ()
- tuple[index]
# 字典 可变 {}
- 查: dict[value],get(key,value)若key->!exit output->value
~~~

## 方法

~~~markdown
# input() 
- print(input(message)) （input() -> return string)
# 强转:int(),str(),double(),bool()
~~~

## 函数

~~~markdown
# 创建函数
def name(value,value2..):
		code
# 调用函数
name(value,value2..)
# return
~~~

## 流程控制

or，and ，not非

~~~markdown
# if 
if 判断:
	 执行语句
elif:
	 执行语句
else:
	 执行语句 	
# while
while 判断语句:
			执行语句
# for
- for value in 可迭代因子:
			代码块
- 可迭代因子: string,list,元组,字典,range()...
~~~

## 异常

~~~python
try:
  num = 10/0
# 
except:
  # print("error")
except ZeroDivisionError as err:
  print(err)
~~~

## 读取外部文件

~~~markdown
# 读
file = open("**.txt","r") 读
										  w   写,覆盖文件
							 				a 	将信息添加到文件的末尾
							 				r+	读写
file.close()关闭文件，打开文件后也要确保关闭文件
file.readable() -> return bool
file.readline()读完后，指针移至下一行
file.readlines()读取所有行
file.readlines()[line]
# 写
file = open("***","a")
file.write("qwe")
file.close
~~~

## 模块pip

~~~markdown
pip --version
pip install python-docx
pip uninstall python-docx
~~~

## 类和对象

~~~python
class Student:
  def __init__(self)
~~~

另一个文件调用

~~~python
# from py文件 import 类
from Student import Student
s1 = Student() 
~~~

## 类函数

~~~python
class Question:
    def __init__(self,questions,answer):
        self.questions = questions
        self.answer = answer
    def honor(self):
        if self.answer>10:
            return True
        if self.answer<5 and self.answer>1:
            return False
~~~

调用

~~~python
from Question import Question
q = Question("zh",1)
print(q.honor)
~~~

## 继承

父类

~~~markdown
class Chef:
    def make_fish(self):
        print("fish is finishing")
~~~

子类

~~~python
from Chef import Chef
class ChineseChef(Chef):
    def make_another(self):
        print("another is finishing")
    # 可重写父类方法
    # def make_fish(self):
    #     print("Chinese fish is finishing")
~~~

## Conda

~~~markdown
# pycharm终端切换版本
conda activate py36
~~~

## Flask

~~~markdown
# pycharm终端运行
export FLASK_APP=hello   hello为python文件
flask run
~~~

## 练习题

1. 计算器：input（）实现 

2. if，输入3个数，输出最大的，return

3. 计算器，通过输入算数符号，来进行 加减乘除

4. 猜数，限制次数

5. 写一个实现幂的函数，ps: for

6. 多项选择题，ps：类

   ~~~python
   from Question import Question
   selects = ["what color are the apple\n(a)red\n(b)white","what color are the bananas\n(a)yellow\n(b)purple"]
   questions = [Question(selects[0],"a"),Question(selects[1],"a")]
   def run_test(questions):
       score = 0
       for question in questions:
           # 遍历列表得到每一个Question类  
           answer = input(question.questions)
           if answer ==question.answer:
               score += 1
       return score
   print(run_test(questions))
   ~~~

   Question

   ~~~python
   class Question:
       def __init__(self,questions,answer):
           self.questions = questions
           self.answer = answer
   ~~~

   