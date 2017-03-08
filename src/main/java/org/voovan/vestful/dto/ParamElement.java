package org.voovan.vestful.dto;

/**
 * 参数描述对象
 *
 * @author helyho
 *         <p>
 *         Restful Framework.
 *         WebSite: https://github.com/helyho/Vestful
 *         Licence: Apache v2 License
 */
public class ParamElement {
    private String name;
    private String desc;
    private Class clazz;


    public ParamElement(String name, String desc, Class clazz){
        this.name = name;
        this.desc = desc;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Class getClazz() {
        return clazz;
    }

}
