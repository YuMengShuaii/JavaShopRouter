package com.enation.javashop.android.jrouter.exception;

/**
 * 路径查询失败
 */

public class RouterPathNorFoundException extends RuntimeException {

    public RouterPathNorFoundException(String message) {
        super(message);
    }
}
