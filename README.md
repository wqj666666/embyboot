## 项目
基于springboot实现的emby和navidrome的电报用户管理机器人

## 使用说明

1.先导入项目里sql目录的空白数据库emby.sql

2.修改application.yml里面的相关配置，在本地测试的时候，国内环境可以科学上网，配置里开启本地代理就可以直接方便测试和tg的通信了
```yml
server:  
  # 端口  
  port: 5747  
  
spring:  
  datasource:  
    url: jdbc:mysql://127.0.0.1:3306/emby?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai  
    username: 123456  
    password: 123456  
#电报机器人配置  
  #是否开启本地代理  
  telegrambot:  
    defaultBotOptions:  
      enabled: false  
  #机器人信息token和name配置  
    config:  
      token: ""  
      username: ""  
      groupChatId : ""  
      adminId: ""  
  #emby配置  
  emby:  
    config:  
      url: ""  
      apiKey: ""  
      copyFromUserId: ""  
  
  #navidrome配置  
  navidrome:  
    config:  
      url: ""  
      username: ""  
      password: ""  
  #歌曲下载配置，需要对接部署好的下载程序,没有这个程序那就不配置  
  musicDownloader:  
    config:  
      url: ""  
      downloadFolder: ""  
  # 日志文件配置的环境  
  profiles:  
    active: dev

```

3.歌曲下载是对接一个开源音乐项目，因为项目作者删库了，用不到可以删除这部分相关配置和代码。

## 项目的作用

主要实现了对于emby和navidrome的电报用户管理，很多这方面的开源项目都是python实现的，我使用springboot是为了方便功能扩展和运行更稳定。

