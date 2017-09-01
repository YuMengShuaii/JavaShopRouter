package com.enation.javashop.android.jrouter.exception;

/**
 *  未初始化异常
 */

public class UnInitException extends RuntimeException {

    public UnInitException(String detailMessage) {
        super(detailMessage);
    }
}