# conda和flask过程

# conda

常用指令

~~~markdown
conda -V
conda env list
conda create you_env_name python=version
conda activate you_env_name
conda deactivate //使当前环境失效
conda remove --name you_env_name --all
~~~

# pycharm+conda+flask

搭建环境选conda，通过preferences选项->project: edge-detection->python interpreter

1. 下拉菜单选conda
2. 点击 “+” 搜索flask安装

打开terminal

**路径选择和运行文件相一致**

~~~markdown
conda activate you_env_name
export FLASK_APP=python文件
flask run

- flask run -p -h 
- p:端口 h:主机

出现异常，通过
python python文件.py
pip intall 模块
~~~



