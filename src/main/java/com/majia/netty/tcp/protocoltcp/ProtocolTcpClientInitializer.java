package com.majia.netty.tcp.protocoltcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ProtocolTcpClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MessageProtocolEncoder());// 加入编码器
        pipeline.addLast(new MessageProtocolDecoder());// 加入解码器
        pipeline.addLast(new ProtocolTcpClientHandler());
    }
}
