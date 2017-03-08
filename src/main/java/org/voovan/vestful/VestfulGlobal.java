package org.voovan.vestful;

import org.voovan.tools.ObjectPool;

/**
 * Vestful的全局配置
 *
 * @author helyho
 * <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
public class VestfulGlobal {

    private static ObjectPool objectPool = new ObjectPool(60);

    public static ObjectPool getObjectPool(){
        return objectPool;
    }
}
