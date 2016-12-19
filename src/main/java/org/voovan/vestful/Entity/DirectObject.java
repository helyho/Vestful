package org.voovan.vestful.Entity;

import org.voovan.tools.reflect.TReflect;

/**
 * 类文字命名
 *
 * @author helyho
 *         <p>
 *         Vestful Framework.
 *         WebSite: https://github.com/helyho/Vestful
 *         Licence: Apache v2 License
 */
public class DirectObject {

    public String createObject(String clazzName ){
        try {
            Class clazz = Class.forName(clazzName);
            Object object = TReflect.newInstance(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
