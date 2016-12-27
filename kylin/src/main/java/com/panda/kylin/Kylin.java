package com.panda.kylin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by panda on 16/12/8.
 */

public class Kylin {
    private Object host;

    public Object dispatchMethod(Object host, Kylin kylin, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException {
        try {
            for (Method method : getClass().getDeclaredMethods()) {
                PatchMethodName patchMethodName = method.getAnnotation(PatchMethodName.class);
                if (methodName.hashCode() == patchMethodName.value().hashCode()) {
                    kylin.setHost(host);
                    return method.invoke(kylin, params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getHost() {
        return host;
    }

    public void setHost(Object host) {
        this.host = host;
    }
}
