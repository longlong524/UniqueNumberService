# UniqueNumberService
唯一id生成系统服务端

# 原理
利用数据库的原子性生成唯一id。

# 架构
典型的CS架构

# 高可用性
高可用性可以通过部署多个UniqueNumberService，每个UniqueNumberService负责不同的id段来实现。


# 使用方法
1. 创建表

  CREATE TABLE `number` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `num` bigint(20) DEFAULT '1',
  `limitNum` bigint(20) DEFAULT '100',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

num是起始数字
limitNum是终止数字（包含）

2. 配置application.properties


3. 启动UniquNumApplication

4. 使用
num为获取数量
post to http://localhost:7653/getNum
  {
  "scretKey":"hfiwoefdhnbfekklwefw",
  "num":10
  }

返回值：
end为终止id
start为起始id
  {
    "code": 200,
    "end": 55230101,
    "start": 55230092,
    "success": true
  }

