package com.panda.kylin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by panda on 16/12/8.
 */

public class Kylin {

    protected Object dispatchMethod(Object host, Kylin kylin, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException {
        for (Method method : getClass().getDeclaredMethods()) {
            PatchMethodName patchMethodName = method.getAnnotation(PatchMethodName.class);
            if (methodName.hashCode() == patchMethodName.value().hashCode()) {
                return method.invoke(kylin, host, params);
            }
        }
        return null;
    }
}
