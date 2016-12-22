package com.panda.kylintest;

import com.panda.kylin.Kylin;
import com.panda.kylin.PatchClassName;
import com.panda.kylin.PatchMethodName;

/**
 * Created by panda on 16/12/22.
 */
@PatchClassName("com.panda.kylintest.MainActivity")
public class MainActivityPatch extends Kylin{
    @PatchMethodName("getToastString")
    public String getToastString() {
        return "fix bug";
    }
}
