package com.majia.netty.tcp.protocoltcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ProtocolTcpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MessageProtocolDecoder());// 解码器
        pipeline.addLast(new MessageProtocolEncoder());// 编码器
        pipeline.addLast(new ProtocolTcpServerHandler());
    }
}
