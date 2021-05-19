package com.xc.netty.rpc.provider;

import com.xc.netty.rpc.api.IRpcHelloService;

/**
 * description
 *
 * @author lichao chao.li07@hand-china.com 5/18/21 3:35 PM
 */
public class RpcHelloServiceImpl implements IRpcHelloService {

    @Override
    public String hello(String name) {
        return "hello " + name + "!";
    }

}
