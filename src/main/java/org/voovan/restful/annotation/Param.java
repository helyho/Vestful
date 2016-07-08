package org.voovan.restful.annotation;

import java.lang.annotation.*;

/**
 * 方法参数 Annotation 类
 *
 * @author helyho
 * <p>
 * RestfulService Framework.
 * WebSite: https://github.com/helyho/Vestful
 * Licence: Apache v2 License
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)

public @interface Param {
    String name() default "";  //参数名称,对应 Http 的请求参数名称
    String desc() default "";  //参数描述,用于生成说明页面
}
