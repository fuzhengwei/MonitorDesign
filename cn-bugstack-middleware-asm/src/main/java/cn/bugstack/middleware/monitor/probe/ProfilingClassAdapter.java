package cn.bugstack.middleware.monitor.probe;

import cn.bugstack.middleware.monitor.config.ProfilingFilter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ProfilingClassAdapter extends ClassVisitor {

    private final String className;
    private final String fullClazzName;
    private final String simpleClassName;

    private boolean isInterface;

    public ProfilingClassAdapter(final ClassVisitor cv, String className) {
        super(ASM5, cv);
        this.className = className;
        this.fullClazzName = className.replace('/', '.');
        this.simpleClassName = className.substring(className.lastIndexOf('/') + 1);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.isInterface = (access & ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        if (!"queryUserInfo".equals(name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        // 不对接口和私有方法注入
        if (isInterface || (access & ACC_PRIVATE) != 0) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        //不对抽象方法、native方法、桥接方法、合成方法进行注入
        if ((access & ACC_ABSTRACT) != 0
                || (access & ACC_NATIVE) != 0
                || (access & ACC_BRIDGE) != 0
                || (access & ACC_SYNTHETIC) != 0) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        if ("<init>".equals(name) || "<clinit>".equals(name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        // 过滤Object类默认方法
        if (ProfilingFilter.isNotNeedInjectMethod(name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (null == mv) return null;

        return new ProfilingMethodVisitor(access, name, descriptor, mv, className, fullClazzName, simpleClassName);
    }

}
