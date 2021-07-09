package cn.bugstack.middleware.monitor.config;

import java.util.List;

public class MethodInfo {

    private String fullClassName;
    private String simpleClassName;
    private String methodName;
    private String desc;
    private List<String> parameterTypeList;
    private String returnParameterType;

    public MethodInfo() {
    }

    public MethodInfo(String fullClassName, String simpleClassName, String methodName, String desc, List<String> parameterTypeList, String returnParameterType) {
        this.fullClassName = fullClassName;
        this.simpleClassName = simpleClassName;
        this.methodName = methodName;
        this.desc = desc;
        this.parameterTypeList = parameterTypeList;
        this.returnParameterType = returnParameterType;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getParameterTypeList() {
        return parameterTypeList;
    }

    public void setParameterTypeList(List<String> parameterTypeList) {
        this.parameterTypeList = parameterTypeList;
    }

    public String getReturnParameterType() {
        return returnParameterType;
    }

    public void setReturnParameterType(String returnParameterType) {
        this.returnParameterType = returnParameterType;
    }
}
