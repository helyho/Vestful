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
import java.lang.reflect.Parameter;
import java.util.*;

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
    private static List<String> classControls;
    private static String route = "DirectObject";
    private static Map<String,String> aliases;
    private static boolean debug = false;
    private static Map<String,String> scriptCache = new HashMap<String, String>();


    /**
     * 设置访问路径用户处理脚本
     * @param routeParam 访问路径
     */
    public static void setRoute(String routeParam){
        route = routeParam;
    }


    /**
     * 可访问类的控制
     * @param classControlParam 匹配类全路径名的正则表达式
     */
    public static void setClassControl(List<String> classControlParam){
        classControls = classControlParam;
    }

    /**
     * 可访问类的控制
     * @param aliasParam 匹配类全路径名的正则表达式
     */
    public static void setAlias(Map<String,String> aliasParam){
        aliases = aliasParam;
    }

    /**
     * 设置对象池的对象存活时间
     * @param aliveTime 对象存活时间,单位:秒
     */
    public static void setObjectAliveTime(int aliveTime){
        VestfulGlobal.getObjectPool().setAliveTime(aliveTime);
    }

    /**
     * 是否调试模式 调试模式
     * @param debugParam true: 调试模式打开,false: 调试模式关闭
     */
    public static void setDebug(boolean debugParam){
        debug = debugParam;
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
    @Restful( method="POST", desc="Create new object in server side.")
    public static String createObject(
            @Param(name="className", desc = "Class full path name.")
                    String className,
            @Param(name="params", desc = "Constructor method param")
                    Object ...params) throws Exception {

        boolean createEnable = false;

        //类访问控制
        if(classControls!=null) {
            for (String classControl : classControls) {
                if (TString.regexMatch(className, classControl) != 0) {
                    createEnable = true;
                }
            }
        }else{
            createEnable = true;
        }

        if(createEnable) {
            Object object = TReflect.newInstance(className, params);
            return objectPool.add(object);
        }else{
            throw new RestfulException("Class " + className + " not found",521,"CLASS_NOT_FOUND");
        }
    }

    /**
     * 调用对象池中对象的指定方法
     * @param pooledObjectId 对象在对象池中的 ID
     * @param methodName  方法名
     * @param params 参数集合
     * @return 方法返回值
     */
    @Restful( method="POST", desc="Invoke Object method.")
    public static String invoke(
            @Param(name="pooledObjectId", desc="Object id in ObjectPool.")
                    String pooledObjectId,
            @Param(name="methodName", desc="name of which method you want to invoke.")
                    String methodName,
            @Param(name="type", desc="Method return type [OBJECT,JSON] default is JSON.")
            String type,
            @Param(name="params", desc="Method invoke params.")
            Object ...params
            ) throws Exception {
        Object obj = objectPool.get(pooledObjectId);
        if(obj==null){
            throw new RestfulException("Object not found, Object id: " + pooledObjectId,522,"OBJECT_NOT_FOUND");
        }
        params = converParam(params);
        try{
            Object result = TReflect.invokeMethod(obj,methodName,params);

            if(result!=null){

                //对链式调用的对象进行支持,返回 this
                if(result.equals(obj)) {
                    return "this";
                }
                //返回对象,以便在 js 中能够完成调用
                else if (type.equals("OBJECT")) {
                    String objectId = objectPool.add(result);
                    String script = genObjectScript(result.getClass().getCanonicalName(), objectId);
                    script = script + ";\r\nnew " + result.getClass().getSimpleName() + "()";
                    return script;
                }
                //返回 JSON 数据
                else if(type.equals("JSON")) {
                    return JSON.toJSON(result);
                }else{
                    throw new RestfulException("Method invoke type \""+type+"\" isn't defined.");
                }
            }

        }catch(Exception e){
            if(e instanceof ReflectiveOperationException){
                throw (Exception)e.getCause();
            }else{
                throw e;
            }
        }

        return null;
    }

    /**
     * 从对象池中指定的对象
     * @param pooledObjectId 对象在对象池中的 ID
     * @return 方法返回值
     */
    @Restful( method="POST", desc="Invoke Object method.")
    public static void release(
            @Param(name="pooledObjectId", desc="Object id in ObjectPool.")
                    String pooledObjectId
          ) throws Exception {
        objectPool.remove(pooledObjectId);
    }

    /**
     * 生成对象对应的 js 脚本
     * @param className
     * @param objectId
     * @return
     * @throws Exception
     */
    private static String genObjectScript(String className, String objectId) throws Exception {
        if(jsTemplate==null){
            throw new RestfulException("Load javascript template file error.",523,"SCRIPT_TEMPLATE_NOT_FOUND");
        }

        //别名处理
        if(aliases!=null) {
            for (Map.Entry<String, String> aliasDefine : aliases.entrySet()) {
                if (className.startsWith(aliasDefine.getKey())) {
                    className = className.replace(aliasDefine.getKey(), aliasDefine.getValue());
                }
            }
        }
        String returnTemplate = null;
        String cachedScript = scriptCache.get(className);

        if(cachedScript == null){

            StringBuilder funcTemplate = new StringBuilder();
            Class clazz = Class.forName(className);
            Method[] methods = TReflect.getMethods(clazz);

            //逐个生成类的所有方法
            for(Method method : methods){
                String methodName = method.getName();

                Parameter[] parameters = method.getParameters();

                if("main".equals(methodName)){
                    continue;
                }

                String funcParam = "";
                for(int i=0; i<parameters.length; i++){
                    funcParam = funcParam + "arg"+(i+1)+", ";
                }

                funcParam = TString.removeSuffix(funcParam.trim());

                funcTemplate.append("    this."+methodName+" = function("+funcParam+ (parameters.length > 0?",":"") +" type, success, fail) {\r\n" );
                funcTemplate.append("        var args = methodArgs(arguments, type, success, fail, "+parameters.length+"); \r\n");
                funcTemplate.append("        return invokeMathod(this.objectId, \""+methodName+"\", args.type, args.success, args.fail, args.args);\r\n" );
                funcTemplate.append("    };\r\n\r\n");
            }

            returnTemplate = jsTemplate;

            returnTemplate = returnTemplate.replace("T/*CLASS_NAME*/", clazz.getSimpleName());
            returnTemplate = returnTemplate.replace("T/*CLASS_FULL_NAME*/", className);
            returnTemplate = returnTemplate.replace("T/*METHODS*/", funcTemplate.toString().trim());
            returnTemplate = returnTemplate.replace("T/*ROUTE*/", route);

            scriptCache.put(className,returnTemplate);
        }else{
            returnTemplate = scriptCache.get(className);
        }

        returnTemplate = returnTemplate.replace("T/*OBJECTID*/", objectId==null?"null":"\""+objectId+"\"");

        if(!debug) {
            returnTemplate = returnTemplate.replaceAll("[\\r\\n][\\t\\s]*", "");
        }
        return returnTemplate;
    }

    /**
     * 生成类对应的 js 脚本
     * @param className
     * @return
     * @throws Exception
     */
    @Restful( method="GET", desc="Get script of browser invoke server side object .")
    public static String genScript(
            @Param(name="className", desc="Which class want to generate javascript code.")
            String className) throws Exception {
        return genObjectScript(className, null);

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
                        "ServerObject".equals(mapParam.get("type"))
                ){
                    String objectId = mapParam.get("objectId").toString();
                    if(objectPool.contains(objectId)) {
                        params[i] = objectPool.get(objectId);
                    }
                }
            }
        }
        return params;
    }
}
