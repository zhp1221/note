# -flask搭建及docker部署

# docker部署

生成requirements.txt

> pip freeze requirements.txt
>
> pip freeze > requirements.txt
>
> pip install -r requirements.txt

dockerfile

> ENV FLASK_APP 
>
> 指定运行文件

~~~dockerfile
FROM python:3.9.5
MAINTAINER ZHANGHP
WORKDIR /
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY . .
ENV FLASK_APP test
CMD ["python3","-m","flask","run","--host=0.0.0.0", "--port=5000"]
EXPOSE 8000
~~~

docker部署

~~~shell
docker build . -t 名字:tag 
docker run -d -p 0000:0000 名字
~~~

若有docker daemon is running?查看docker desktop是否在后台运行

