![](http://git.oschina.net/uploads/images/2016/0510/122514_7d971a34_116083.jpeg)
=======================================================================================
#####基于 Voovan 开发的通用 Restful 服务框架。旨在为广大开发者提供一个快速、稳定、功能丰富、自产文档的 Restful 框架。目的是在完成业务实现的同时,说明文档、接口服务等同时完成。
 
#####接口服务演示地址:[http://vsetful.voovan.com/test!](http://vsetful.voovan.com/test!)
#####接口方法演示地址:[http://vsetful.voovan.com/test/testWithReturnObject!](http://vsetful.voovan.com/test/testWithReturnObject!)
#### 特点:
 - 仅仅通过两个注解就可以自动生成完善的Restful 接口说明文档。
 - 自动识别路径中的变量、常规请求变量、URLEncode以及MUTILPART提交变量。
       变量来源优先: 路径变量 > URLEncode以及MUTILPART提交变量 > 常规请求变量。
 - 支持 HTTP 协议中的方法以及任意的自定义 HTTP 方法。
 - 对方法的参数个数、返回值类型等没有要求,可以使用任意类型。
 - 如果方法返回的是一个自定义的对象,会自动转换成 JSON 字符串的形式返回。
 - 由于 HTTP 协议发送的请求为字符串,所以方法的参数类型目前仅支持常规的 Java 基础类型。
 - 需要访问说明文档,请在对应的接口URL后增加!, 如接口路径为/test, 访问文档则通过/test!来访问。
 
------------------------------------------
  
####注解说明:
- Restful注解
    - 使用方式: 在方法上使用。
    - 参数: 
         - method / HTTP 请求的方法类型。例如: GET、POST、PUT 等等。
         - desc   / 关于方法的描述信息。
     
- Param注解
    - 使用方式: 在方法的参数上使用。
    - 参数: 
         - name: 参数名称。
         - desc: 关于参数的描述信息。

------------------------------------------

####配置文件说明:
```JSON
[
    {
      "name":"test",
      "route":"/test/", 
      "classPath":"org.voovan.test.restful.TestClass",
      "desc":"this is a test interface."
    }
]
```
 - name: 接口名称
 - route: 服务搜寻的路由,如果没有这个节点,则默认值为 classPath 的类名,例如:org.voovan.restful.TestClass则默认 route 为/TestClass
 - classPath: 提供接口的类说明
 - desc: 接口描述信息
 
--------------------------------------------

####服务部署说明:
   服务配置请参照 Voovan 的说明文档,主要配置/conf/web.json 文件即可。

----------------------------------------------

####使用举例:
测试代码请参照:[TestClass.java](https://git.oschina.net/helyho/Vestful/blob/master/src/test/java/org/voovan/test/restful/TestClass.java)
    