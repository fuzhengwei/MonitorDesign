package cn.bugstack.middleware.monitor.probe;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyMonitorTransformer implements ClassFileTransformer {

    private static final Set<String> classNameSet = new HashSet<>();

    static {
        classNameSet.add("cn.bugstack.middleware.test.interfaces.UserController");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            String currentClassName = className.replaceAll("/", ".");
            if (!classNameSet.contains(currentClassName)) { // 提升classNameSet中含有的类
                return null;
            }

            // 获取类
            CtClass ctClass = ClassPool.getDefault().get(currentClassName);
            String clazzName = ctClass.getName();

            // 获取方法
            CtMethod ctMethod = ctClass.getDeclaredMethod("queryUserInfo");
            String methodName = ctMethod.getName();

            // 方法信息：methodInfo.getDescriptor();
            MethodInfo methodInfo = ctMethod.getMethodInfo();

            // 方法：入参信息
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            CtClass[] parameterTypes = ctMethod.getParameterTypes();

            boolean isStatic = (methodInfo.getAccessFlags() & AccessFlag.STATIC) != 0;  // 判断是否为静态方法
            int parameterSize = isStatic ? attr.tableLength() : attr.tableLength() - 1; // 静态类型取值
            List<String> parameterNameList = new ArrayList<>(parameterSize);            // 入参名称
            List<String> parameterTypeList = new ArrayList<>(parameterSize);            // 入参类型
            StringBuilder parameters = new StringBuilder();                             // 参数组装；$1、$2...，$$可以获取全部，但是不能放到数组初始化

            for (int i = 0; i < parameterSize; i++) {
                parameterNameList.add(attr.variableName(i + (isStatic ? 0 : 1))); // 静态类型去掉第一个this参数
                parameterTypeList.add(parameterTypes[i].getName());
                if (i + 1 == parameterSize) {
                    parameters.append("$").append(i + 1);
                } else {
                    parameters.append("$").append(i + 1).append(",");
                }
            }

            // 方法：出参信息
            CtClass returnType = ctMethod.getReturnType();
            String returnTypeName = returnType.getName();

            // 方法：生成方法唯一标识ID
            int idx = Monitor.generateMethodId(clazzName, methodName, parameterNameList, parameterTypeList, returnTypeName);

            // 定义属性
            ctMethod.addLocalVariable("startNanos", CtClass.longType);
            ctMethod.addLocalVariable("parameterValues", ClassPool.getDefault().get(Object[].class.getName()));

            // 方法前加强
            ctMethod.insertBefore("{ startNanos = System.nanoTime(); parameterValues = new Object[]{" + parameters.toString() + "}; }");

            // 方法后加强
            ctMethod.insertAfter("{ cn.bugstack.middleware.monitor.probe.Monitor.point(" + idx + ", startNanos, parameterValues, $_);}", false); // 如果返回类型非对象类型，$_ 需要进行类型转换

            // 方法；添加TryCatch
            ctMethod.addCatch("{ cn.bugstack.middleware.monitor.probe.Monitor.point(" + idx + ", $e); throw $e; }", ClassPool.getDefault().get("java.lang.Exception"));   // 添加异常捕获

            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
