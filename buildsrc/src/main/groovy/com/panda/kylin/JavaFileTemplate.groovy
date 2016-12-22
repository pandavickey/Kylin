package com.panda.kylin;

/**
 * Created by panda on 16/12/20.
 */

public class JavaFileTemplate {
    public static def getContent() {
        return """
package com.panda.kylin;

import java.util.ArrayList;
import java.util.List;

public class PatchBox {

       static List<String> patchs = new ArrayList();

       public static List<String> getPatchClasses() {
              return patchs;
       }
}
                """
    }
}
