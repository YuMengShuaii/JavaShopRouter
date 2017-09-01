package com.enation.javashop.android.jrouter.utils;

import android.net.Uri;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  文本处理工具类
 */

public class TextUtils {

    /**
     * 判空
     * @param cs  字符
     * @return    result
     */
    public static boolean isEmpty(final CharSequence cs) {
            return cs == null || cs.length() == 0;
        }

        /**
         * 打印堆栈信息
         */
        public static String formatStackTrace(StackTraceElement[] stackTrace) {
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : stackTrace) {
                sb.append("    at ").append(element.toString());
                sb.append("\n");
            }
            return sb.toString();
        }

    /**
     * 获取uri携带的参数
     * @param rawUri uri
     * @return      参数map
     */
    public static Map<String, String> splitQueryParameters(Uri rawUri) {
            String query = rawUri.getEncodedQuery();

            if (query == null) {
                return Collections.emptyMap();
            }

            Map<String, String> paramMap = new LinkedHashMap<>();
            int start = 0;
            do {
                int next = query.indexOf('&', start);
                int end = (next == -1) ? query.length() : next;

                int separator = query.indexOf('=', start);
                if (separator > end || separator == -1) {
                    separator = end;
                }

                String name = query.substring(start, separator);

                if (!android.text.TextUtils.isEmpty(name)) {
                    String value = (separator == end ? "" : query.substring(separator + 1, end));
                    paramMap.put(Uri.decode(name), Uri.decode(value));
                }

                start = end + 1;
            } while (start < query.length());

            return Collections.unmodifiableMap(paramMap);
        }

    /**
     * 获取|左边的值
     * @param key 文本
     * @return    值
     */
        public static String getLeft(String key) {
            if (key.contains("|") && !key.endsWith("|")) {
                return key.substring(0, key.indexOf("|"));
            } else {
                return key;
            }
        }

    /**
     * 获取|右边的值
     * @param key 文本
     * @return    值
     */
        public static String getRight(String key) {
            if (key.contains("|") && !key.startsWith("|")) {
                return key.substring(key.indexOf("|") + 1);
            } else {
                return key;
            }
        }

    }
