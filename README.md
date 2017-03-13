![](http://git.oschina.net/uploads/images/2016/0510/122514_7d971a34_116083.jpeg)
=======================================================================================
#####基于 Voovan 开发的通用 Restful 服务框架。旨在为广大开发者提供一个快速、稳定、功能丰富、自产文档的 Restful 框架。目的是在完成业务实现的同时,说明文档、接口服务等同时完成。
#####这是一个 Voovan 项目的模块,需要在 Voovan HttpServer项目中添加该模块.
```json
    //配置文件:conf/web.json
    "Modules": [
        {
            "Name": "Vestful 模块",                                      //模块名称
            "Path": "/Vestful",                                          //模块路径
            "ClassName": "org.voovan.vestful.RestfulModule"             //模块处理器
        }
    ]
```
#####接口服务演示地址:[http://vestful.voovan.org/test!](http://vestful.voovan.org/test!)
#####接口方法演示地址:[http://vestful.voovan.org/test/testWithReturnObject!](http://vestful.voovan.org/test/testWithReturnObject!)

#####关于部署##### 
   - 将Vestful.jar复制到到目标 VoovanHttpServer 的 lib 目录
   - 并按照上面的模块配置在 web.json 中进行配置
   - 访问: http://x.x.x.x/Vestful/className!

#### 特点:
 - 仅仅通过两个注解就可以自动将方法暴露成 Restful 接口，并生成完善的Restful 接口说明文档。
 - 自动识别路径中的变量、常规请求变量、URLEncode以及MUTILPART提交变量。
       变量来源优先: 路径变量 > URLEncode以及MUTILPART提交变量 > 常规请求变量。
 - 支持 HTTP 协议中的方法以及任意的自定义 HTTP 方法。
 - 由于 HTTP 协议发送的请求为字符串,所以方法的参数类型目前支持常规的 `Java基础类型`、`Collection`、`Map`及`自定义类型`。
 - 对方法的参数个数、返回值类型等没有要求,可以使用任意类型。如果方法返回的是一个自定义的对象,会自动转换成 JSON 字符串的形式返回。
 - 需要访问说明文档,请在对应的接口URL后增加!, 如接口路径为/test, 访问文档则通过/test!来访问。
 - 可以通过 js 直接在页面中操作后台 java 类,实现前后台编码无阻碍.
 
------------------------------------------
###一、编写业务类  
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
         
- 注解使用举例:
```java
    //类:org.voovan.test.vestful.TestClass.java

    //方法注解
    @Restful(method="LOCK", desc="This is a method description. test float param")
    public static TestResult testWithReturnObject(
                                 @Param(name="test1",desc="Param name is test1, type is String") //参数注解
                                         String test1,
                                 @Param(name="test2",desc="Param name is test2. type is int") //参数注解
                                         int test2){
        return new TestResult(test1,test2);
    }
```

------------------------------------------
###二、修改配置文件
####修改 conf/vestful.json
####配置文件说明:
```JSON
//配置文件:conf/web.json
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
测试代码请参照:[org.voovan.test.vestful.TestClass.java](https://git.oschina.net/helyho/Vestful/blob/master/src/test/java/org/voovan/test/vestful/TestClass.java)

----------------------------------------------
    
####说明文档解释:

![](http://git.oschina.net/uploads/images/2016/0711/172233_336dbdd7_116083.png)
