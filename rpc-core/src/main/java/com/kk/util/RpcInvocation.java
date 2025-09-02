package com.kk.util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcInvocation { 
    /**
     * 请求id
     */
    private String uuid;

    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数
     */
    private  Object[] params;
        /**
     * 参数类型
     */
    private Class<?>[] paramTypes;
    /**
     * fanhuizhi 
     */
    private Object result;
    /**
     *  服务器异常
     */
    private Throwable e;
}
