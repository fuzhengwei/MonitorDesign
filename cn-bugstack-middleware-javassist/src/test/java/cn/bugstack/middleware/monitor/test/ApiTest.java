package cn.bugstack.middleware.monitor.test;

import javassist.*;

import java.lang.reflect.Method;

public class ApiTest {

    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();

        CtClass ctClass = pool.makeClass("cn.bugstack.middleware.javassist.MathUtil");

        // 属性字段
        CtField ctField = new CtField(CtClass.doubleType, "π", ctClass);
        ctField.setModifiers(Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL);
        ctClass.addField(ctField, "3.14");

        // 方法：求圆面积
        CtMethod calculateCircularArea = new CtMethod(CtClass.doubleType, "calculateCircularArea", new CtClass[]{CtClass.doubleType}, ctClass);
        calculateCircularArea.setModifiers(Modifier.PUBLIC);
        calculateCircularArea.setBody("{return π * $1 * $1;}");
        ctClass.addMethod(calculateCircularArea);

        // 方法；两数之和
        CtMethod sumOfTwoNumbers = new CtMethod(pool.get(Double.class.getName()), "sumOfTwoNumbers", new CtClass[]{CtClass.doubleType, CtClass.doubleType}, ctClass);
        sumOfTwoNumbers.setModifiers(Modifier.PUBLIC);
        sumOfTwoNumbers.setBody("{return Double.valueOf($1 + $2);}");
        ctClass.addMethod(sumOfTwoNumbers);
        // 输出类的内容
        ctClass.writeFile();

        // 测试调用
        Class clazz = ctClass.toClass();
        Object obj = clazz.newInstance();

        Method method_calculateCircularArea = clazz.getDeclaredMethod("calculateCircularArea", double.class);
        Object obj_01 = method_calculateCircularArea.invoke(obj, 1.23);
        System.out.println("圆面积：" + obj_01);

        Method method_sumOfTwoNumbers = clazz.getDeclaredMethod("sumOfTwoNumbers", double.class, double.class);
        Object obj_02 = method_sumOfTwoNumbers.invoke(obj, 1, 2);
        System.out.println("两数和：" + obj_02);
    }

}
