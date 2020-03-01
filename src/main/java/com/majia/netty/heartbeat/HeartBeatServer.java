package com.majia.netty.heartbeat;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartBeatServer {
    public static void main(String[] args) {
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
                           // 加入一个netty 提供 IdleStateHandler
                            /**
                             * 说明：
                             * 1. IdleStateHandler 是netty 提供的处理空闲状态的处理器
                             * 2. long readerIdleTime 表示多长时间没有读，就会发送一个心跳检测包检测是否连接
                             * 3. long writerIdleTime 表示多长时间没有写，就会发送一个心跳检测包检测是否连接
                             * 4. long allIdleTime 示多长时间没有读写，就会发送一个心跳检测包检测是否连接
                             * 5. 文档说明
                             * Triggers an {@link IdleStateEvent} when a {@link Channel} has not performed read, write, or both operation for a while.
                             * 6. 当 IdleStateEvent 触发后，就会传递给管道的下一个handler处理，通过调用（触发）下一个handler 的 userEventTriggered方法，
                             *    在该方法中取处理 IdleStateEvent（读空闲、写空闲、读写空闲）
                             */
                            pipeline.addLast("IdleStateHandler",new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            // 加一个对空闲检测进一步处理的handler（自定义）
                            pipeline.addLast("HeartBeatServerHandler",new HeartBeatServerHandler());
                        }
                    });
            System.out.println("netty 服务器启动");
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
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
}
