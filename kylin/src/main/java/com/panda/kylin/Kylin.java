package com.panda.kylin;

/**
 * Created by panda on 16/12/8.
 */

public interface Kylin {
    Object dispatchMethod(Object host, String methodNameHashcode, Object[] params);
}
