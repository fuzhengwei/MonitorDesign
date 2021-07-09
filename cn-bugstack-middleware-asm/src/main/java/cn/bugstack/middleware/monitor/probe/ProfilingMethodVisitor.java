package cn.bugstack.middleware.monitor.probe;

import cn.bugstack.middleware.monitor.config.MethodInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfilingMethodVisitor extends AdviceAdapter {

    private List<String> parameterTypeList = new ArrayList<>();
    private int parameterTypeCount = 0;     // 参数个数
    private int startTimeIdentifier;        // 启动时间标记
    private int parameterIdentifier;        // 入参内容标记
    private int methodId = -1;              // 方法全局唯一标记
    private int currentLocal = 0;           // 当前局部变量值
    private final boolean isStaticMethod;   // true；静态方法，false；非静态方法
    private final String className;

    protected ProfilingMethodVisitor(int access, String methodName, String desc, MethodVisitor mv, String className, String fullClassName, String simpleClassName) {
        super(ASM5, mv, access, methodName, desc);
        this.className = className;
        // 判断是否为静态方法，非静态方法中局部变量第一个值是this，静态方法是第一个入参参数
        isStaticMethod = 0 != (access & ACC_STATIC);
        //(String var1,Object var2,String var3,int var4,long var5,int[] var6,Object[][] var7,Req var8)=="(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;IJ[I[[Ljava/lang/Object;Lorg/itstack/test/Req;)V"
        Matcher matcher = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})").matcher(desc.substring(0, desc.lastIndexOf(')') + 1));
        while (matcher.find()) {
            parameterTypeList.add(matcher.group(1));
        }
        parameterTypeCount = parameterTypeList.size();
        methodId = ProfilingAspect.generateMethodId(new MethodInfo(fullClassName, simpleClassName, methodName, desc, parameterTypeList, desc.substring(desc.lastIndexOf(')') + 1)));
    }

    private Label from = new Label(),
            to = new Label(),
            target = new Label();

    @Override
    protected void onMethodEnter() {
        // 1.方法执行时启动纳秒
        probeStartTime();
        // 2.方法入参信息
        probeMethodParameter();
        // 标志：try块开始位置
        visitLabel(from);
        visitTryCatchBlock(from,
                to,
                target,
                "java/lang/Exception");
    }

    /**
     * 方法执行时启动纳秒
     */
    private void probeStartTime() {
        // long l = System.nanoTime();
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(System.class), "nanoTime", "()J", false);
        currentLocal = newLocal(Type.LONG_TYPE);
        startTimeIdentifier = currentLocal;
        mv.visitVarInsn(LSTORE, currentLocal);
    }

    private void probeMethodParameter() {
        int parameterCount = parameterTypeList.size();
        if (parameterCount <= 0) return;
        // 1. 初始化数组
        if (parameterCount >= 4) {
            mv.visitVarInsn(BIPUSH, parameterCount); // valuebyte值带符号扩展成int值入栈。
        } else {
            switch (parameterCount) {
                case 1:
                    mv.visitInsn(ICONST_1); // 1(int)值入栈
                    break;
                case 2:
                    mv.visitInsn(ICONST_2);// 2(int)值入栈
                    break;
                case 3:
                    mv.visitInsn(ICONST_3);// 3(int)值入栈
                    break;
                default:
                    mv.visitInsn(ICONST_0);// 0(int)值入栈
            }
        }
        mv.visitTypeInsn(ANEWARRAY, Type.getDescriptor(Object.class));

        // 局部变量
        int localCount = isStaticMethod ? -1 : 0;
        // 2. 给数组赋值
        for (int i = 0; i < parameterCount; i++) {
            mv.visitInsn(DUP);
            if (i > 5) {
                mv.visitVarInsn(BIPUSH, i);
            } else {
                switch (i) {
                    case 0:
                        mv.visitInsn(ICONST_0);
                        break;
                    case 1:
                        mv.visitInsn(ICONST_1);
                        break;
                    case 2:
                        mv.visitInsn(ICONST_2);
                        break;
                    case 3:
                        mv.visitInsn(ICONST_3);
                        break;
                    case 4:
                        mv.visitInsn(ICONST_4);
                        break;
                    case 5:
                        mv.visitInsn(ICONST_5);
                        break;
                }
            }

            String type = parameterTypeList.get(i);
            if ("Z".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;", false);
            } else if ("C".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Character.class), "valueOf", "(C)Ljava/lang/Character;", false);
            } else if ("B".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Byte.class), "valueOf", "(B)Ljava/lang/Byte;", false);
            } else if ("S".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Short.class), "valueOf", "(S)Ljava/lang/Short;", false);
            } else if ("I".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
            } else if ("F".equals(type)) {
                mv.visitVarInsn(FLOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Float.class), "valueOf", "(F)Ljava/lang/Float;", false);
            } else if ("J".equals(type)) {
                mv.visitVarInsn(LLOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;", false);
                localCount++;
            } else if ("D".equals(type)) {
                mv.visitVarInsn(DLOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Double.class), "valueOf", "(D)Ljava/lang/Double;", false);
                localCount++;
            } else {
                mv.visitVarInsn(ALOAD, ++localCount);  //获取对应的参数
            }
            mv.visitInsn(AASTORE);
        }

        parameterIdentifier = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(ASTORE, parameterIdentifier);

    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
        int methodParameterIndex = isStaticMethod ? index : index - 1;  // 可以打印方法中所有入参的名称，这也可以用于后续自定义插针
        if (0 <= methodParameterIndex && methodParameterIndex < parameterTypeList.size()) {
            ProfilingAspect.setMethodParameterGroup(methodId, name);
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        //标志：try块结束
        mv.visitLabel(to);
        //标志：catch块开始位置
        mv.visitLabel(target);

        // 设置visitFrame：mv.visitFrame(Opcodes.F_FULL, 4, new Object[]{"java/lang/String", Opcodes.INTEGER, Opcodes.LONG, "[Ljava/lang/Object;"}, 1, new Object[]{"java/lang/Exception"});
        int nLocal = (isStaticMethod ? 0 : 1) + parameterTypeCount + (parameterTypeCount == 0 ? 1 : 2);
        Object[] localObjs = new Object[nLocal];
        int objIdx = 0;
        if (!isStaticMethod) {
            localObjs[objIdx++] = className;
        }
        for (String parameter : parameterTypeList) {
            if ("Z".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("C".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("B".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("S".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("I".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("F".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.FLOAD;
            } else if ("J".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.LONG;
            } else if ("D".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.DOUBLE;
            } else {
                localObjs[objIdx++] = parameter;
            }
        }
        localObjs[objIdx++] = Opcodes.LONG;
        if (parameterTypeCount > 0) {
            localObjs[objIdx] = "[Ljava/lang/Object;";
        }
        mv.visitFrame(Opcodes.F_FULL, nLocal, localObjs, 1, new Object[]{"java/lang/Exception"});

        // 异常信息保存到局部变量
        int local = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(ASTORE, local);

        // 输出参数
        mv.visitVarInsn(LLOAD, startTimeIdentifier);
        mv.visitLdcInsn(methodId);
        if (parameterTypeList.isEmpty()) {
            mv.visitInsn(ACONST_NULL);
        } else {
            mv.visitVarInsn(ALOAD, parameterIdentifier);
        }
        mv.visitVarInsn(ALOAD, local);
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(ProfilingAspect.class), "point", "(JI[Ljava/lang/Object;Ljava/lang/Throwable;)V", false);

        // 抛出异常
        mv.visitVarInsn(ALOAD, local);
        mv.visitInsn(ATHROW);

        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    protected void onMethodExit(int opcode) {
        if ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW) {
            probeMethodReturn(opcode);
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(methodId);
            // 判断入参
            if (parameterTypeList.isEmpty()) {
                mv.visitInsn(ACONST_NULL);
            } else {
                mv.visitVarInsn(ALOAD, parameterIdentifier);
            }
            // 判断出参
            if (RETURN == opcode) {
                mv.visitInsn(ACONST_NULL);
            } else if (IRETURN == opcode) {
                mv.visitVarInsn(ILOAD, currentLocal);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
            } else {
                mv.visitVarInsn(ALOAD, currentLocal);
            }
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(ProfilingAspect.class), "point", "(JI[Ljava/lang/Object;Ljava/lang/Object;)V", false);
        }
    }

    /**
     * 方法出参
     */
    private void probeMethodReturn(int opcode) {
        currentLocal = this.nextLocal;
        switch (opcode) {
            case RETURN:
                break;
            case ARETURN:
                mv.visitVarInsn(ASTORE, currentLocal); // 将栈顶引用类型值保存到局部变量indexbyte中。
                mv.visitVarInsn(ALOAD, currentLocal);  // 从局部变量indexbyte中装载引用类型值入栈。
                break;
            case IRETURN:
                visitVarInsn(ISTORE, currentLocal);
                visitVarInsn(ILOAD, currentLocal);
                break;
            case LRETURN:
                visitVarInsn(LSTORE, currentLocal);
                visitVarInsn(LLOAD, currentLocal);
                break;
            case DRETURN:
                visitVarInsn(DSTORE, currentLocal);
                visitVarInsn(DLOAD, currentLocal);
                break;
        }
    }

}
