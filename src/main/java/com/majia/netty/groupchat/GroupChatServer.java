package com.majia.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class GroupChatServer {

    private int port; // 监听端口

    public GroupChatServer(int port){
        this.port = port;
    }

    // 编写 run 方法,处理客户端请求
    public void run(){
        // 创建两个线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(0);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .option(ChannelOption.SO_BACKLOG,128)
                            .childOption(ChannelOption.SO_KEEPALIVE,true)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    // 获取到 pipeline
                                    ChannelPipeline pipeline = ch.pipeline();
                                    // 向 pipeline 加入解码器
                                    pipeline.addLast("decoder",new StringDecoder());
                                    // 向 pipeline 加入编码器
                                    pipeline.addLast("encoder",new StringEncoder());
                                    // 加入自己的业务处理 handler
                                    pipeline.addLast(new GroupChatServerHandler());
                                }
                            });
            System.out.println("netty 服务器启动");
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 监听关闭
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println("GroupServer run 方法产生异常：" + e.getMessage());
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new GroupChatServer(7000).run();
    }
}
