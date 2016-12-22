package com.panda.kylin;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by panda on 16/12/22.
 */

public class KylinMethodSupport {
    public static boolean isSupport(Class patchClass, String methodName) {
        Log.e("methodName", methodName);
        for (Method method : patchClass.getDeclaredMethods()) {
            PatchMethodName annotation = method.getAnnotation(PatchMethodName.class);
            if (annotation != null) {
                if (annotation.value().hashCode() == methodName.hashCode()) {
                    return true;
                }
            }
        }
        return false;
    }
}
