package com.enation.javashop.android.jrouter.external.model;

import com.enation.javashop.android.jrouter.external.annotation.Router;
import com.enation.javashop.android.jrouter.external.enums.RouterType;

import java.util.Map;

import javax.lang.model.element.Element;

/**
 * 路由基础Model
 */

public class RouterModel {

    /**
     * 路由类型 such as Activity
     */
    private RouterType type;

    /**
     *   类型
     */
    private Element rawType;

    /**
     * Class
     */
    private Class<?> destination;   // Destination

    /**
     * 跳转path
     */
    private String path;            // Path of route

    /**
     * group组
     */
    private String group;           // Group of route

    /**
     * 优先级 默认-1
     */
    private int priority = -1;      // The smaller the number, the higher the priority

    /**
     * 附加参数
     */
    private int extra;              // Extra data

    /**
     * 参数类型
     */
    private Map<String, Integer> paramsType;  // Param type

    /**
     * 构造函数
     */
    public RouterModel() {
    }

    /**
     * 构造RouterModel对象
     * @param type          跳转类型
     * @param destination   转换类型
     * @param path          路径
     * @param group         组
     * @param paramsType    传参类型
     * @param priority      优先级
     * @param extra         附加参数
     * @return
     */
    public static RouterModel build(RouterType type, Class<?> destination, String path, String group, Map<String, Integer> paramsType, int priority, int extra) {
        return new RouterModel(type, null, destination, path, group, paramsType, priority, extra);
    }

    /**
     *
     * @param route         路由类型
     * @param rawType       转换类型
     * @param type          对象类型
     * @param paramsType    参数类型
     */
    public RouterModel(Router route, Element rawType, RouterType type, Map<String, Integer> paramsType) {
        this(type, rawType, null, route.path(), route.module(), paramsType, route.priority(), route.extras());
    }

    /**
     * 构造RouterModel对象
     * @param type          跳转类型
     * @param destination   转换类型
     * @param path          路径
     * @param group         组
     * @param paramsType    传参类型
     * @param priority      优先级
     * @param extra         附加参数
     * @return
     */
    public RouterModel(RouterType type, Element rawType, Class<?> destination, String path, String group, Map<String, Integer> paramsType, int priority, int extra) {
        this.type = type;
        this.destination = destination;
        this.rawType = rawType;
        this.path = path;
        this.group = group;
        this.paramsType = paramsType;
        this.priority = priority;
        this.extra = extra;
    }

    public Map<String, Integer> getParamsType() {
        return paramsType;
    }

    public RouterModel setParamsType(Map<String, Integer> paramsType) {
        this.paramsType = paramsType;
        return this;
    }

    public Element getRawType() {
        return rawType;
    }

    public RouterModel setRawType(Element rawType) {
        this.rawType = rawType;
        return this;
    }

    public RouterType getType() {
        return type;
    }

    public RouterModel setType(RouterType type) {
        this.type = type;
        return this;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public RouterModel setDestination(Class<?> destination) {
        this.destination = destination;
        return this;
    }

    public String getPath() {
        return path;
    }

    public RouterModel setPath(String path) {
        this.path = path;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public RouterModel setGroup(String group) {
        this.group = group;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public RouterModel setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public int getExtra() {
        return extra;
    }

    public RouterModel setExtra(int extra) {
        this.extra = extra;
        return this;
    }

    @Override
    public String toString() {
        return "RouteModel{" +
                "type=" + type +
                ", rawType=" + rawType +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", priority=" + priority +
                ", extra=" + extra +
                '}';
    }
}
