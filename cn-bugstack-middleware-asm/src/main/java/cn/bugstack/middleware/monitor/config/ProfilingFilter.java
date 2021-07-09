package cn.bugstack.middleware.monitor.config;

import java.util.HashSet;
import java.util.Set;

public class ProfilingFilter {

    private static Set<String> exceptPackagePrefix = new HashSet<>();

    private static Set<String> exceptMethods = new HashSet<>();

    static {

        // 默认不注入的包
        exceptPackagePrefix.add("java/");
        exceptPackagePrefix.add("javax/");
        exceptPackagePrefix.add("sun/");
        exceptPackagePrefix.add("com/sun/");
        exceptPackagePrefix.add("com/intellij/");
        exceptPackagePrefix.add("org/jetbrains/");
        exceptPackagePrefix.add("org/slf4j");
        exceptPackagePrefix.add("com/alibaba");

        // 默认不注入的方法
        exceptMethods.add("main");
        exceptMethods.add("premain");
        exceptMethods.add("getClass");//java.lang.Object
        exceptMethods.add("hashCode");//java.lang.Object
        exceptMethods.add("equals");//java.lang.Object
        exceptMethods.add("clone");//java.lang.Object
        exceptMethods.add("toString");//java.lang.Object
        exceptMethods.add("notify");//java.lang.Object
        exceptMethods.add("notifyAll");//java.lang.Object
        exceptMethods.add("wait");//java.lang.Object
        exceptMethods.add("finalize");//java.lang.Object
        exceptMethods.add("afterPropertiesSet");//spring

    }

    public static boolean isNotNeedInject(String className) {
        if (null == className) return false;

        for (String prefix : exceptPackagePrefix) {
            if (className.startsWith(prefix)) return true;
        }

        return false;
    }

    public static boolean isNotNeedInjectMethod(String methodName){
        if (null == methodName) return false;

        return exceptMethods.contains(methodName);
    }
}
