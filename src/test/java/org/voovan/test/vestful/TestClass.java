package org.voovan.test.vestful;

import org.voovan.tools.json.JSON;
import org.voovan.vestful.annotation.Param;
import org.voovan.vestful.annotation.Restful;

/**
 * 类文字命名
 *
 * @author helyho
 *         <p>
 *         RestfulService Framework.
 *         WebSite: https://github.com/helyho/Vestful
 *         Licence: Apache v2 License
 */
public class TestClass {

    @Restful( method="GET", desc="This is a method description. test param with MoreObject")
    public static  String testWithMoreObject(@Param(name="test1",desc="Param name is test1, type is String")
                                                   String test1,
                                           @Param(name="test2",desc="Param name is test2. type is MoreObject")
                                                   Object ...test2){
        return test1+" "+ JSON.toJSON(test2);
    }

    @Restful( method="GET", desc="This is a method description. test return with object")
    public static TestResult testWithReturnObject(@Param(name="test1",desc="Param name is test1, type is String")
                                         String test1,
                                 @Param(name="test2",desc="Param name is test2. type is int")
                                         int test2){
        return new TestResult(test1,test2);
    }

    @Restful( method="POST", desc="This is a method description. test String param")
    public static String testWithString(@Param(name="test1",desc="Param name is test1, type is String")
                                 String test1,
                                 @Param(name="test2",desc="Param name is test2. type is String")
                                 String test2){
        return "Param1="+test1+" , Param2="+test2;
    }

    @Restful(method="PUT", desc="This is a method description. test int param")
    public static String testWithInt(@Param(name="test1",desc="Param name is test1. type is String")
                              String test1,
                              @Param(name="test2",desc="Param name is test2. type is int")
                              int test2){
        return "Param1="+test1+" , Param2="+test2;
    }

    @Restful(method="LOCK", desc="This is a method description. test float param")
    public static String testWithFloat(@Param(name="test1")
                                String test1,
                                @Param(name="test2")
                                float test2){
        return "Param1="+test1+" , Param2="+test2;
    }

    @Restful(method="LOCK", desc="This is a method description. test double param")
    public static String testWithDouble(@Param(name="test1",desc="Param name is test1. type is String")
                                 String test1,
                                 @Param(name="test2",desc="Param name is test2. type is double")
                                 double test2){
        return "Param1="+test1+" , Param2="+test2;
    }

    @Restful(method="LOCK", desc="This is a method description. test boolean param")
    public static String testWithBoolean(@Param(name="test1",desc="Param name is test1. type is String")
                                  String test1,
                                  @Param(name="test2",desc="Param name is test2. type is boolean")
                                  boolean test2){
        return "Param1="+test1+" , Param2="+test2;
    }

    @Restful(method="LOCK", desc="This is a method description. test long param")
    public static String testWithLong(@Param(name="test1",desc="Param name is test1. type is String")
                               String test1,
                               @Param(name="test2",desc="Param name is test2. type is long")
                               long test2){
        return "Param1="+test1+" , Param2="+test2;
    }

    @Restful(method="LOCK", desc="This is a method description. test short param")
    public static String testWithShort(@Param(name="test1",desc="Param name is test1. type is String")
                                String test1,
                                @Param(name="test2",desc="Param name is test2. type is short")
                                short test2){
        return "Param1="+test1+" , Param2="+test2;
    }

    @Restful(method="LOCK", desc="This is a method description. test byte param")
    public static String testWithByte(@Param(name="test1",desc="Param name is test1. type is String")
                               String test1,
                               @Param(name="test2",desc="Param name is test2.  type is byte")
                               byte test2){
        return "Param1="+test1+" , Param2="+test2;
    }

    @Restful(method="LOCK", desc="This is a method description. test char param")
    public static String testWithChar(@Param(name="test1",desc="Param name is test1. type is String")
                               String test1,
                               @Param(name="test2",desc="Param name is test2. type is char")
                               char test2){
        return "Param1="+test1+" , Param2="+test2;
    }
}
