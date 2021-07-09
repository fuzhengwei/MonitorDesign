package cn.bugstack.middleware.monitor.test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class ApiTest {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        String helloWorld = new ByteBuddy()
                .subclass(Object.class)
                .method(named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(ApiTest.class.getClassLoader())
                .getLoaded()
                .newInstance()
                .toString();

        System.out.println(helloWorld);
    }

}
