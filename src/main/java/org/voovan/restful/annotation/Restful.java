package org.voovan.restful.annotation;

import java.lang.annotation.*;

/**
 * Restful方法 Annotation 类
 *
 * @author helyho
 *         <p>
 * Vestful Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Restful {
        String method() default "GET";      //方法所采用 HTTP 请求方法
        String desc() default "";             //方法的描述,用于生成说明页面
}
