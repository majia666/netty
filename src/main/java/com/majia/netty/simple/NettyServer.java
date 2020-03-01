package com.majia.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args){

        // 创建BossGroup 和 WorkerGroup
        /**
         * 说明
         * 1. 创建两个线程组 bossGroup 和 workerGroup
         * 2. bossGroup 只是处理连接请求，真正的客户端业务处理，会交给workerGroup
         * 3. 两个都是无限循环
         * 4. bossGroup 和 workerGroup 含有的子线程(NioEventLoop) 的个数 默认实际cpu核数 * 2
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            // 创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 使用链式编程进行配置
            bootstrap.group(bossGroup,workerGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用NioSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个通道初始化对象（匿名对象）
                        // 给pipeline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("客户端 socketchannel hashcode= " + ch.hashCode());// 可以使用一个集合管理 SocketChannel，再推送消息时，可以将业务加入到各个channel 对应的NioEventLoop 的taskQueue 或者 scheduledTaskQueue
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });//  给我们的workerGroup 的EventLoop 对应的管道设置处理器
            System.out.println("... 服务端 is ready ...");
            // 绑定一个端口并且同步，生成了一个 ChannelFuture 对象
            // 启动服务器（并绑定端口）
            ChannelFuture cf = bootstrap.bind(6668).sync();

            // 给cf 注册监听器，监控我们关心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (cf.isSuccess()){
                        System.out.println("监听端口 6668 成功");
                    }else {
                        System.out.println("监听端口 6668 失败");
                    }
                }
            });
            // 对关闭通道进行监听
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println("NettyServer main 产生异常：" + e.getMessage() );
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
