package com.panda.kylintest;

import com.panda.kylin.Kylin;
import com.panda.kylin.PatchClassName;

/**
 * Created by panda on 16/12/13.
 */

@PatchClassName("com.panda.kylintest.MainActivity")
public class MainActivityPatch implements Kylin {
    @Override
    public Object dispatchMethod(Object host, String methodHashcode, Object[] params) {
        MainActivity mainActivity = (MainActivity) host;
        if ("getToastString".hashCode() == methodHashcode.hashCode()) {
            return getToastString();
        }
        return null;
    }

    String getToastString() {
        return "fix bug";
    }
}
