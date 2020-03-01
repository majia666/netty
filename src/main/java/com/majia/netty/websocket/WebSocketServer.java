package com.majia.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class WebSocketServer {
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
                            // 因为基于 http 协议，使用 http的编码 和解码器
                            pipeline.addLast("HttpServerCodec",new HttpServerCodec());
                            // 是以块方式写，添加 ChunkedWriterHandler 处理器
                            pipeline.addLast("ChunkedWriteHandler",new ChunkedWriteHandler());
                            /**
                             * 说明
                             * 1. http 数据在传输过程中是分段，HttpObjectAggregator,就可以将多个段聚合
                             * 2. 这就是为什么，当浏览器发送大量的数据时，就会发送多次http请求
                             */
                            pipeline.addLast("HttpObjectAggregator",new HttpObjectAggregator(8192));
                            /**
                             * 说明
                             * 1. 对应 websocket，它的数据是以帧（frame）形式传递
                             * 2. 可以看到 WebSocketFrame 下面六个子类
                             * 3. 浏览器请求时，ws://localhost:7000/hello 表示请求的uri
                             * 4. WebSocketServerProtocolHandler 核心功能是将 http 协议升级成 ws 协议，保持长连接
                             * 5. 是通过一个 状态码 101
                             */
                            pipeline.addLast("WebSocketServerProtocolHandler",new WebSocketServerProtocolHandler("/hello"));
                            //自定义的handler，业务处理逻辑
                            pipeline.addLast("WebSocketServerHandler",new WebSocketServerHandler());

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
