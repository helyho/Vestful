package org.voovan.vestful.Entity;

import org.voovan.tools.ObjectPool;
import org.voovan.tools.json.JSON;
import org.voovan.tools.log.Logger;
import org.voovan.tools.reflect.TReflect;
import org.voovan.vestful.VestfulGlobal;
import org.voovan.vestful.annotation.Param;
import org.voovan.vestful.annotation.Restful;

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

    private ObjectPool objectPool = VestfulGlobal.getObjectPool();

    /**
     * 构造一个对象
     * @param className 对象字符串描述
     * @param params 参数
     * @return 返回新建的对象
     * @throws ReflectiveOperationException
     */
    @Restful( method="GET", desc="This is a method description. test return with object")
    public int createObject(
            //@Param(name="className", desc = "Class full path name.")
            String className,
            //@Param(name="params", desc = "Constructor method param")
            Object ...params) throws ReflectiveOperationException {
        Object object = TReflect.newInstance(className, params);
        Logger.simple(object.hashCode());
        return objectPool.add(object);
    }

    /**
     * 调用对象池中对象的指定方法
     * @param pooledObjectId 对象在对象池中的 ID
     * @param methodName  方法名
     * @param params 参数集合
     * @return 方法返回值
     */
    @Restful( method="GET", desc="This is a method description. test return with object")
    public String invoke(
            @Param(name="pooledObjectId", desc="Object id in ObjectPool.")
            int pooledObjectId,
            @Param(name="methodName", desc="name of which method you want to invoke.")
            String methodName,
            @Param(name="pooledObjectId", desc="Method invoke params.")
            Object ...params) throws ReflectiveOperationException {
        Object obj = objectPool.get(pooledObjectId);
        Object result = TReflect.invokeMethod(obj,methodName,params);
        return JSON.toJSON(result);

    }

    public String genScript(String className, int clazzPoolId){
        return "";
    }
}
