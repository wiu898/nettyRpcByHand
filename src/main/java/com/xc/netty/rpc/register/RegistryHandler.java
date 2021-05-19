package com.xc.netty.rpc.register;

import com.xc.netty.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义Handler
 *
 * @author lichao chao.li07@hand-china.com 5/19/21 6:02 PM
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    //注册中心容器 - 存储扫描类的容器
    private static ConcurrentHashMap<String,Object> registryMap =
            new ConcurrentHashMap<String,Object>();

    //存放扫描到的所有类名
    private List<String> classNames = new ArrayList<String>();

    public RegistryHandler(){
        //com.xc.netty.rpc.provider 下的全部扫描进来
        //扫描所有需要注册的类
        scannerClass("com.xc.netty.rpc.provider");
        //将扫描到的类注册到容器中
        doResiger();
    }

    private void scannerClass(String packageName) {
        //通过报名获取到类路径
        URL url = this.getClass().getClassLoader().getResource(
                packageName.replaceAll("\\.","/"));
        //读取类路径文件夹下所有文件
        File dir = new File(url.getFile());
        for(File file : dir.listFiles()){
            //如果是文件夹需要进行递归操作
            if(file.isDirectory()){
                scannerClass(packageName + "." + file.getName());
            }else {
                classNames.add(packageName + "." + file.getName().replace(".class","").trim());
            }
        }
    }

    private void doResiger() {
        if(classNames.size() == 0){
            return;
        }
        for(String className : classNames){
            try{
                Class clazz = Class.forName(className);
                //用接口名字作为key
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(),clazz.newInstance());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol) msg;
        if(registryMap.containsKey(request.getClassName())){
            //用反射直接调用Provider的方法
            Object provider = registryMap.get(request.getClassName());
            Method method = provider.getClass().getMethod(request.getMethodName(),request.getParams());
            result = method.invoke(provider,request.getValues());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    /*
     * 处理异常的方法
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
