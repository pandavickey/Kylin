package com.panda.kylintest;

import com.panda.kylin.Kylin;
import com.panda.kylin.PatchClassName;
import com.panda.kylin.PatchMethodName;

import java.lang.reflect.Method;

/**
 * Created by panda on 16/12/22.
 */
@PatchClassName("com.panda.kylintest.Test")
public class MainActivityPatch extends Kylin {
    @PatchMethodName("testMethod")
    public String testMethod() {
        try {
            return "fix" + ((Test) getHost()).testMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
