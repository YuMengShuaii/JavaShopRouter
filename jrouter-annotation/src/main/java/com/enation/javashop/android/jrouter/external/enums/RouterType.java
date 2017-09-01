package com.enation.javashop.android.jrouter.external.enums;

/**
 * 路由常量类型
 * @author LDD
 */

public enum RouterType {

    /**
     * Activity
     */
    ACTIVITY(0, "android.app.Activity"),

    /**
     * 服务
     */
    SERVICE(1, "android.app.Service"),

    /**
     * 自定义内容提供
     */
    PROVIDER(2, "com.alibaba.android.arouter.facade.template.IProvider"),

    /**
     * 原生内容提供者
     */
    CONTENT_PROVIDER(-1, "android.app.ContentProvider"),

    /**
     * 广播
     */
    BOARDCAST(-1, ""),

    /**
     * 方法
     */
    METHOD(-1, ""),

    /**
     * Fragment
     */
    FRAGMENT(-1, "android.app.Fragment"),

    /**
     * 未识别
     */
    UNKNOWN(-1, "Unknown route type");

    /**
     * 类型ID
     */
    int id;

    /**
     * 类名
     */
    String className;

    public int getId() {
        return id;
    }

    public RouterType setId(int id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public RouterType setClassName(String className) {
        this.className = className;
        return this;
    }

    RouterType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public static RouterType parse(String name) {
        for (RouterType routeType : RouterType.values()) {
            if (routeType.getClassName().equals(name)) {
                return routeType;
            }
        }

        return UNKNOWN;
    }
}
