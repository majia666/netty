package com.majia.testTcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TestTcpServer {
    public static void main(String[] args) {
        //server 服务启动器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerEventLoopGroup = new NioEventLoopGroup();

        try {
            serverBootstrap.group(bossEventLoopGroup,workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //可以在socket接上来的时候添加很多定义逻辑
                            socketChannel.pipeline().addLast("encoder",new StringEncoder());
                            socketChannel.pipeline().addLast("decoder",new StringDecoder());
                            socketChannel.pipeline().addLast("ping",new IdleStateHandler(25,15,10, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new TestTcpNioServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            // Bind and start to accept incoming connections
            ChannelFuture future = serverBootstrap.bind(9999).sync();//
            System.out.println("开始监听 9999 端口");
            // 启动一个线程 来给客户端发消息
            // new Thread(new TestTcpServerTask()).run();
            // wait until the server socket is closed
            // in this example,this does not happen,but you can dou that to gracefully
            // shut down your server . 调用实现优雅关机
            future.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            bossEventLoopGroup.shutdownGracefully();
            workerEventLoopGroup.shutdownGracefully();
        }

    }
}
