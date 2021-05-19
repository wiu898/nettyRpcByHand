package com.xc.netty.rpc.provider;

import com.xc.netty.rpc.api.IRpcService;

/**
 * description
 *
 * @author lichao chao.li07@hand-china.com 5/18/21 4:38 PM
 */
public class RpcServiceImpl implements IRpcService {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int mult(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}
