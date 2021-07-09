package cn.bugstack.middleware.monitor.probe;

import cn.bugstack.middleware.monitor.config.MethodInfo;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class ProfilingAspect {

    public static final int MAX_NUM = 1024 * 32;

    private final static AtomicInteger index = new AtomicInteger(0);
    private final static AtomicReferenceArray<MethodInfo> methodTagArr = new AtomicReferenceArray<>(MAX_NUM);
    private final static Map<Integer, List<String>> methodParameterGroup = new ConcurrentHashMap<>();

    public static int generateMethodId(MethodInfo tag) {
        int methodId = index.getAndIncrement();
        if (methodId > MAX_NUM) return -1;
        methodTagArr.set(methodId, tag);
        return methodId;
    }

    public static synchronized void setMethodParameterGroup(final int methodId, String parameterName) {
        List<String> parameterList = methodParameterGroup.computeIfAbsent(methodId, k -> new ArrayList<>());
        parameterList.add(parameterName);
    }

    public static void point(final long startNanos, final int methodId, Object[] requests, Object response) {
        MethodInfo method = methodTagArr.get(methodId);
        List<String> parameters = methodParameterGroup.get(methodId);
        System.out.println("监控 - Begin By ASM");
        System.out.println("方法：" + method.getFullClassName() + "." + method.getMethodName());
        System.out.println("入参：" + JSON.toJSONString(parameters) + " 入参类型：" + JSON.toJSONString(method.getParameterTypeList()) + " 入数[值]：" + JSON.toJSONString(requests));
        System.out.println("出参：" + method.getReturnParameterType() + " 出参[值]：" + JSON.toJSONString(response));
        System.out.println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)");
        System.out.println("监控 - End\r\n");
    }

    public static void point(final long startNanos, final int methodId, Object[] requests, Throwable throwable) {
        MethodInfo method = methodTagArr.get(methodId);
        List<String> parameters = methodParameterGroup.get(methodId);
        System.out.println("监控 - Begin By ASM");
        System.out.println("方法：" + method.getFullClassName() + "." + method.getMethodName());
        System.out.println("入参：" + JSON.toJSONString(parameters) + " 入参类型：" + JSON.toJSONString(method.getParameterTypeList()) + " 入数[值]：" + JSON.toJSONString(requests));
        System.out.println("异常：" + throwable.getMessage());
        System.out.println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)");
        System.out.println("监控 - End\r\n");
    }

    public static void point(final long startNanos, final int methodId, Object[] requests) {
        MethodInfo method = methodTagArr.get(methodId);
        List<String> parameters = methodParameterGroup.get(methodId);
        System.out.println("监控 - Begin By ASM");
        System.out.println("方法：" + method.getFullClassName() + "." + method.getMethodName());
        System.out.println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)");
        System.out.println("监控 - End\r\n");
    }

}
