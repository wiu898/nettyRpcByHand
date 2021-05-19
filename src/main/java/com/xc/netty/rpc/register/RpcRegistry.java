package com.xc.netty.rpc.register;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


/**
 * description
 *
 * @author lichao chao.li07@hand-china.com 5/19/21 5:44 PM
 */
public class RpcRegistry {

    private  int port = 8080;

    public RpcRegistry (int port){
        this.port = port;
    }

    public static void main(String[] args) {
        new RpcRegistry(8080).start();
    }

    private void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup  = new NioEventLoopGroup();

        try{
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            //接收客户端请求的处理流程
                            ChannelPipeline pipeline = channel.pipeline();
                            int fieldLength = 4;
                            //通用解码器设置
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,fieldLength,0,fieldLength));
                            //通用编码器
                            pipeline.addLast(new LengthFieldPrepender(fieldLength));
                            //对象编码器 - 将二进制序列化
                            pipeline.addLast("encoder", new ObjectEncoder());
                            //对象解码器 - 将二进制反序列化
                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            //自定义逻辑
                            pipeline.addLast(new RegistryHandler());
                        }
                    })
                    //开启的线程数
                    .option(ChannelOption.SO_BACKLOG,128)
                    //开启器长连接
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future = server.bind().sync();
            System.out.println("RPC register is start, listen at " + this.port);
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
