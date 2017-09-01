package com.enation.javashop.android.jrouter.compiler.utils;

import com.enation.javashop.android.jrouter.external.enums.ValueType;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.enation.javashop.android.jrouter.compiler.utils.Constant.BOOLEAN;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.BYTE;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.DOUBEL;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.FLOAT;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.INTEGER;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.LONG;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.PARCELABLE;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.SHORT;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.STRING;

/**
 * 类型判断工具类
 */

public class TypeUtils {

    /**
     * 类型
     */
    private Types types;

    /**
     * 类元素
     */
    private Elements elements;

    /**
     * 序列化
     */
    private TypeMirror parcelableType;

    /**
     * 构造函数
     * @param types      类型
     * @param elements   类节点
     */
    public TypeUtils(Types types, Elements elements) {
        this.types = types;
        this.elements = elements;
        /**
         * 查看是否实现了PARCELABLE接口
         */
        parcelableType = this.elements.getTypeElement(PARCELABLE).asType();
    }

    /**
     * 判断类行 并返回ordinarl
     * @param element  类节点
     * @return  类型的ordinal
     */
    public int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();

        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        switch (typeMirror.toString()) {
            case BYTE:
                return ValueType.BYTE.ordinal();
            case SHORT:
                return ValueType.SHORT.ordinal();
            case INTEGER:
                return ValueType.INT.ordinal();
            case LONG:
                return ValueType.LONG.ordinal();
            case FLOAT:
                return ValueType.FLOAT.ordinal();
            case DOUBEL:
                return ValueType.DOUBLE.ordinal();
            case BOOLEAN:
                return ValueType.BOOLEAN.ordinal();
            case STRING:
                return ValueType.STRING.ordinal();
            default:
                if (types.isSubtype(typeMirror, parcelableType)) {
                    return ValueType.PARCELABLE.ordinal();
                } else {
                    return ValueType.OBJECT.ordinal();
                }
        }
    }
}
