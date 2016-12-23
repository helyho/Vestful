package org.voovan.vestful.entity;

import org.voovan.tools.ObjectPool;
import org.voovan.tools.TFile;
import org.voovan.tools.TObject;
import org.voovan.tools.TString;
import org.voovan.tools.json.JSON;
import org.voovan.tools.log.Logger;
import org.voovan.tools.reflect.TReflect;
import org.voovan.vestful.VestfulGlobal;
import org.voovan.vestful.annotation.Param;
import org.voovan.vestful.annotation.Restful;
import org.voovan.vestful.exception.RestfulException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类文字命名
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class DirectObject {

    private static ObjectPool objectPool = VestfulGlobal.getObjectPool();
    private static String jsTemplate = getJSTemplate();
    private static List<String> packageControl = new ArrayList<String>();

    public static void setPackageControl(List<String> packageControlList){
        packageControl = packageControlList;
    }

    public static String getJSTemplate(){
        try {
            return new String(TFile.loadResource("DirectObject.js"), "UTF-8");
        }catch(UnsupportedEncodingException e){
            Logger.error("Load javascript template file error.",e);
            return null;
        }
    }

    /**
     * 构造一个对象
     * @param className 对象字符串描述
     * @param params 参数
     * @return 返回新建的对象
     * @throws ReflectiveOperationException
     */
    @Restful( method="GET", desc="Create new object in server side.")
    public static int createObject(
            @Param(name="className", desc = "Class full path name.")
            String className,
            @Param(name="params", desc = "Constructor method param")
            Object ...params) throws Exception {

        boolean createEnable = false;
        for(String packageRegex : packageControl){
            if (TString.regexMatch(className, packageRegex) != 0){
                createEnable = true;
            }
        }

        if(createEnable) {
            Object object = TReflect.newInstance(className, params);
            return objectPool.add(object);
        }else{
            throw new RestfulException("Class " + className + " not found",521,"ClassNotFound");
        }
    }

    /**
     * 调用对象池中对象的指定方法
     * @param pooledObjectId 对象在对象池中的 ID
     * @param methodName  方法名
     * @param params 参数集合
     * @return 方法返回值
     */
    @Restful( method="GET", desc="Invoke Object method.")
    public static String invoke(
            @Param(name="pooledObjectId", desc="Object id in ObjectPool.")
            int pooledObjectId,
            @Param(name="methodName", desc="name of which method you want to invoke.")
            String methodName,
            @Param(name="params", desc="Method invoke params.")
            Object ...params) throws Exception {
        Object obj = objectPool.get(pooledObjectId);
        if(obj==null){
            throw new RestfulException("Object not found, Object id: " + pooledObjectId,522,"ObjectNotFound");
        }
        params = converParam(params);
        Object result = TReflect.invokeMethod(obj,methodName,params);
        return JSON.toJSON(result);

    }

    /**
     * 从对象池中指定的对象
     * @param pooledObjectId 对象在对象池中的 ID
     * @return 方法返回值
     */
    @Restful( method="GET", desc="Invoke Object method.")
    public static void release(
            @Param(name="pooledObjectId", desc="Object id in ObjectPool.")
                    int pooledObjectId
          ) throws Exception {
        objectPool.remove(pooledObjectId);
    }

    @Restful( method="GET", desc="Get script of browser invoke server side object .")
    public static String genScript(
            @Param(name="className", desc="Which class want to generate javascript code.")
            String className) throws Exception {

        if(jsTemplate==null){
            throw new RestfulException("Load javascript template file error.",523,"ScriptTemplateNotFound");
        }

        StringBuilder funcTemplate = new StringBuilder();
        Class clazz = Class.forName(className);
        Method[] methods = TReflect.getMethods(clazz);
        for(Method method : methods){
            String methodName = method.getName();
            String funcParam = "";
            for(int i=0; i<method.getParameters().length; i++){
                funcParam = funcParam + "arg"+(i+1)+", ";
            }
            funcParam = TString.removeSuffix(funcParam.trim());

            funcTemplate.append("    this."+methodName+" = function("+funcParam+") {\r\n" );
            funcTemplate.append("        var argsArray = Array.prototype.slice.call(arguments); \r\n");
            funcTemplate.append("        var currentTime = new Date().getTime(); \n");
            funcTemplate.append("        var resultText = invokeMathod(this.objectId, \""+methodName+"\",argsArray).text;\r\n" );
            funcTemplate.append("        return eval('t' + currentTime + '=' + resultText)\r\n" );
            funcTemplate.append("    }\r\n\r\n");
        }

        String returnTemplate = jsTemplate;

        returnTemplate = returnTemplate.replace("T/*CLASS_NAME*/", clazz.getSimpleName());
        returnTemplate = returnTemplate.replace("T/*CLASS_FULL_NAME*/", className);
        returnTemplate = returnTemplate.replace("T/*METHODS*/", funcTemplate.toString().trim());

        return returnTemplate;
    }

    /**
     * 检查是否有对象池中的对象,如果有则转换为对象池的对象
     * @param params
     * @return
     */
    private static Object[] converParam(Object[] params){
        for(int i=0; i<params.length; i++){
            Object param = params[i];
            if(param instanceof Map){
                Map mapParam = TObject.cast(param,Map.class);
                if(mapParam.size() == 2 &&
                        mapParam.containsKey("objectId") &&
                        TString.isInteger(mapParam.get("objectId").toString()) &&
                        mapParam.containsKey("type") &&
                        "ServerObject".equals(mapParam.get("type"))
                ){
                    Integer objectId = Integer.valueOf(mapParam.get("objectId").toString());
                    if(objectPool.contains(objectId)) {
                        params[i] = objectPool.get(objectId);
                    }
                }
            }
        }
        return params;
    }
}
