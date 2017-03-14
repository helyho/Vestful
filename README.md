![](http://git.oschina.net/uploads/images/2016/0510/122514_7d971a34_116083.jpeg)
=======================================================================================
#####基于 Voovan 开发的通用 Restful 服务框架。旨在为广大开发者提供一个快速、稳定、功能丰富、自产文档的 Restful 框架。目的是在完成业务实现的同时,说明文档、接口服务等同时完成。
#####这是一个 Voovan 项目的模块,需要在 Voovan HttpServer项目中添加该模块.
```json
    //配置文件:conf/web.json
    "Modules": [
        {
            "Name": "Vestful 模块",                                      //模块名称
            "Path": "/vestful",                                          //模块路径
            "ClassName": "org.voovan.vestful.RestfulModule"             //模块处理器
        }
    ]
```
#####接口服务演示:[http://vestful.voovan.org/vestful/test!](http://vestful.voovan.org/vestful/test!)
#####接口方法演示:[http://vestful.voovan.org/vestful/test/testWithReturnObject!](http://vestful.voovan.org/vestful/test/testWithReturnObject!)
#####Call java 类演示:[http://vestful.voovan.org](http://vestful.voovan.org/vestful/test!)

#####关于部署##### 
   - 将Vestful.jar复制到到目标 VoovanHttpServer 的 lib 目录
   - 并按照上面的模块配置在 web.json 中进行配置
   - 访问: http://x.x.x.x/vestful/className!

#### 特点:
 - 仅仅通过两个注解就可以自动将方法暴露成 Restful 接口，并生成完善的Restful 接口说明文档。
 - 自动识别路径中的变量、常规请求变量、URLEncode以及MUTILPART提交变量。
       变量来源优先: 路径变量 > URLEncode以及MUTILPART提交变量 > 常规请求变量。
 - 支持 HTTP 协议中的方法以及任意的自定义 HTTP 方法。
 - 由于 HTTP 协议发送的请求为字符串,所以方法的参数类型目前支持常规的 `Java基础类型`、`Collection`、`Map`及`自定义类型`。
 - 对方法的参数个数、返回值类型等没有要求,可以使用任意类型。如果方法返回的是一个自定义的对象,会自动转换成 JSON 字符串的形式返回。
 - 需要访问说明文档,请在对应的接口URL后增加!, 如接口路径为/test, 访问文档则通过/test!来访问。
 - 可以通过 js 直接在页面中操作后台 java 类,实现前后台编码无阻碍.

    **对安全性的支持：**
    * 1.支持类可访问控制,控制那些类可以被前台操作。
    * 2.支持类或者包的别名控制，对某些不想暴露类路径或者类的完全现定名的可以通过别名的方式隐藏类。
    * 3.前端实例化的对象的存活时间控制,防止 OOM。使用对象的方法等操作会默认自动刷新存活时间。
 
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
####增加一下内容到配置文件中:
```JSON
//配置文件:conf/vestful.json
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
类说明及测试页面: http://x.x.x.x/vestful/test!
   - `Vestful` 是在 web.json 中配置的模块访问路径.
   - `test` 是在 vestful.json 中配置的当前类的route参数.

类中某个方法的说明及测试页面: http://x.x.x.x/vestful/test/testWithObject!
   - `Vestful` 是在 web.json 中配置的模块访问路径.
   - `test` 是在 vestful.json 中配置的当前类的route参数
   - `testWithObject` 类中的方法名


![](http://git.oschina.net/uploads/images/2017/0314/125853_9e52bc93_116083.png "")

----------------------------------------------

###三、在页面的 JS 中操作后台的 java 对象
####修改 conf/vestful.json
####增加一下内容到配置文件中::
```JSON
//配置文件:conf/vestful.json
[
    "name": "DirectObject",
    "route": "DirectObject",
    "classPath": "org.voovan.vestful.entity.DirectObject",
    "desc": "Restful API for DirectObject",
    "params": {
      //这里是访问这个服务的完整路径(web.json中配置的Vestful模块路径+ 当前服务的类路径)
      //这里 web.json 中配置的是"/",当前类访问路径配置的是"DirectObject",所以通过/DirectObject来访问
      "route":"DirectObject",
      //这里控制 class 是否能够被前端的 js 调用
      //只有这classControl这个节点里的 class 才可以被前端 js 调用
      "classControl": [
        "java.util.ArrayList",
        "org.voovan.test.vestful.*"
      ],
      //定义类的别名
      "alias":{
        "OVTV": "org.voovan.test.vestful.TestClass"
      },
      //对象存活时间控制,单位:秒
      "objectAliveTime":60
    }
]
```
----------------------------------------------
在页面中引用 java 类到页面的 js 上下问中:
```html
<script lang="javascript" src="/DirectObject/genScript/java.util.ArrayList"></script>
```
页面引入 js 的路径为conf/vestful.json配置文件中"org.voovan.vestful.entity.DirectObject"类的 params 参数列表中route属性中的路径。
路径后增加genScript来代表后台动态生成脚本。

例如：
    例子中的配置为："route":"DirectObject"
    那么页面引入的 js 路径为：/DirectObject/genScript/类完全限定名或者别名即可将对象引入到页面中。

----------------------------------------------

在页面中使用 Java 类:
```javascript
//具体内容请参照 WEBAPP/test.html
<script lang="javascript" >
   arraylist1 = new ArrayList();
   arraylist1.add("aaaa");

   arraylist2 = new ArrayList();
   arraylist2.add("bbbb");

   //支持在前台构造的 java 对象的相互引用
   arraylist1.addAll(arraylist2)
<script/>

```
