# 书蜗脚本

预约、签到、释放全自动一条龙服务。

使用多线程“同时”发起预约请求。

## 环境

Apache Maven  
Git

## 使用方法

```bash
# 下载源码至24小时不关机的计算机上
$ git clone https://github.com/Jianzs/shuwo.git

# 修改 src/main/resources/users.xml, 添加预约的账号信息
$ vim src/main/resources/users.xml

# 执行项目根目录下的 start.sh 脚本
$ ./start.sh
```

## 注意事项
* 必填项包括 name、phone、password、seatId、offset、deviceId、enabled 
* seatId 为座位号
* offset 见下表 参数信息
* deviceId 为长度为16的字母数字混合字符串，任意两个账户不可共用同一个 deviceId
* enabled 为 true 时，该账户生效，否则无效
* 日志输出在项目根目录的 shuwo.log 中
* 20:00前启动，当天预约生效，否则次日生效
* 关闭脚本，请使用`kill`命令


## 开发信息

用到的接口在此[接口文档](https://documenter.getpostman.com/view/3964457/SVfGzXkd?version=latest)中

### 参数信息
西电 libraryId 10000  
南馆 buildingId 8  
北馆 buildingId 33  

| 名称           | roomId | offset |
| -------------- | ------ | ------ |
| A201           | 19     | 20607  |
| A202           | 20     | 21034  |
| A302           | 21     | 21388  |
| B308电子阅览室 | 67     | 10811  |

参数并不全，如果没有想要房间的参数，可以使用 Postman 通过接口文档中接口进行查询，或者提出 issue。

代码写得很不优雅，如有问题，欢迎大佬及时指正~