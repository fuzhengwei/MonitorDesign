package cn.bugstack.middleware.monitor;

import cn.bugstack.middleware.monitor.annotation.DoMonitor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class DoJoinPoint {

    @Pointcut("@annotation(cn.bugstack.middleware.monitor.annotation.DoMonitor)")
    public void aopPoint() {
    }

    @Around("aopPoint() && @annotation(doMonitor)")
    public Object doRouter(ProceedingJoinPoint jp, DoMonitor doMonitor) throws Throwable {
        long start = System.currentTimeMillis();
        Method method = getMethod(jp);
        try {
            return jp.proceed();
        } finally {
            System.out.println("监控 - Begin By AOP");
            System.out.println("监控索引：" + doMonitor.key());
            System.out.println("监控描述：" + doMonitor.desc());
            System.out.println("方法名称：" + method.getName());
            System.out.println("方法耗时：" + (System.currentTimeMillis() - start) + "ms");
            System.out.println("监控 - End\r\n");
        }
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

}
