# Sentry

## sentry 服务端

### 依赖环境

- docker
- docker-compose

### 仓库地址

https://github.com/getsentry/onpremise.git

### 启动步骤

- 从以上仓库clone下代码

- SENTRY_IMAGE=getsentry/sentry:83b1380 ./install.sh

- 可以加上```RUN sed -i 's|deb.debian.org|mirrors.aliyun.com|g' /etc/apt/sources.list```来加速apt-get的速度

- docker-compose up -d

## Sentry Web

Sentry Web 的默认端口为9000，第一次登陆时会创建管理员账号。

### Projects

不同的项目创建不同的Project，错误日志会以Project聚合，不同的开发人员可以关注不同的Project

### Issues

Issues是最主要的错误展示的列表，以错误的名字(Exception的类名)聚合。

点击标题进入详情，可以查看具体信息，如错误出现次数，用户数，最新出现的时间(定位到哪次提交出的问题)，最新出现的时间(如果近三个月没有再出现，一般认为auto fixed)，

错误的上下文(用户信息，浏览器，系统版本，请求头，cookie，请求参数)，错误栈等。

### Alerts

可以设置通知的方式(一般为邮件，如需配置钉钉，可以在sentry/requirements.txt里加入sentry-dingding~=0.0.3)，通知的频率。

## Sentry 客户端(java代码部分)

文档参看https://docs.sentry.io/platforms/java/

### 使用实例(以Servlet为例)

- 在pom.xml里加入

```xml
    <dependency>
        <groupId>io.sentry</groupId>
        <artifactId>sentry</artifactId>
        <version>1.7.30</version>
    </dependency>
```

- 在web.xml里加入

```xml
    <listener>
        <listener-class>io.sentry.servlet.SentryServletRequestListener</listener-class>
        <!--这个listener是用来为每一个请求初始化上下文的，上下文信息会保存在ThreadLocal中-->
    </listener>
```

- 配置一个全局的错误处理的handler

```xml
    <servlet>
        <servlet-name>Error</servlet-name>
        <servlet-class>cn.topic.sentry.ErrorServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>Error</servlet-name>
        <url-pattern>/error</url-pattern>
    </servlet-mapping>
    <error-page>
        <exception-type>java.lang.Throwable</exception-type>
        <location>/error</location>
    </error-page>
```

- ErrorServlet的init方法，配置sentry的地址，这里的地址是在Sentry web上创建project时获取的。

```java
    String dsn = "http://e45e27b11b6448d2b2a7b428d85a1065@127.0.0.1:9000/2";
    Sentry.init(dsn);
```

- 定义一个processError方法，通过Sentry.capture方法发送错误信息。

- 注意：在其他线程里的异步任务不共享context内容。

### Sentry的其他插件

- [logback](https://docs.sentry.io/platforms/java/guides/logback/)
- [spring](https://docs.sentry.io/platforms/java/guides/spring/)
- [log4j](https://docs.sentry.io/platforms/java/guides/log4j/)
- [log4j2](https://docs.sentry.io/platforms/java/guides/log4j2/)

## 欢迎大家补充完善
