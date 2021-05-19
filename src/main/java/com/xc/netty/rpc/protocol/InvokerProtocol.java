package com.xc.netty.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * description
 *
 * @author lichao chao.li07@hand-china.com 5/18/21 6:54 PM
 */
@Data
public class InvokerProtocol implements Serializable {

    private String className;    //类名

    private String methodName;   //方法名

    private Class<?>[] params;   //形参列表

    private Object[] values;     //实参列表




}
