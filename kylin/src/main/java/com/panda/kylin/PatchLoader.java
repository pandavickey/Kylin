package com.panda.kylin;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by panda on 16/12/8.
 */

public class PatchLoader {


    public static void loadPatch(Context context, String patchPath) {

        try {
            DexClassLoader dexClassLoader = new DexClassLoader(patchPath, context.getCacheDir().getPath(), null, context.getClassLoader());

            Class<?> patchBoxClass = Class.forName("com.panda.kylin.PatchBox", true, dexClassLoader);
            List<String> classNames = (List<String>) patchBoxClass.getMethod("getPatchClasses").invoke(null);

            for (String className : classNames) {
                Class<?> patchClass = dexClassLoader.loadClass(className);
                Object patchInstance = patchClass.newInstance();
                PatchClassName bugClassName = patchClass.getAnnotation(PatchClassName.class);
                Class<?> bugClass = context.getClassLoader().loadClass(bugClassName.value());
                Field kylinField = bugClass.getDeclaredField("mKylin");
                kylinField.setAccessible(true);
                kylinField.set(null, patchInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
