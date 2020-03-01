package com.majia.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.*;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    // 定义一个channel 组，管理所有的 channel
    // GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    private static ChannelGroup  channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 另外一种方法
//    public static List<Channel> channels = new ArrayList<Channel>();
    // 两个人单独聊天 方案：使用一个 hashmap 管理  Map<String,Channel> , 或者用 Map<User,Channel>
    //public static Map<String,Channel> channels = new HashMap<String,Channel>();
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // handerAdded 表示连接建立，一旦连接，第一个被执行
    // 将当前 channel 加入到 channelGroup
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 将该客户加入聊天的信息推送给其他在线的客户端
        /**
         * 该方法会将 channelGroup 中的所有 channel 遍历，并发送消息，我们不需要自己遍历
         */
        channelGroup.writeAndFlush(sdf.format(new Date()) + " [客户端] " + channel.remoteAddress() + " 加入聊天\n");
        channelGroup.add(channel);
    }

    // 断开连接，将 xx 客户离开信息推送给当前的在线的客户


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(sdf.format(new Date()) + " [客户端] " + channel.remoteAddress() + " 离开了\n");
        System.out.println("channelGroup size is " + channelGroup.size());
    }

    // 表示 channel 处于活动状态，提示 xx 上线了
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(sdf.format(new Date()) + " " +ctx.channel().remoteAddress() +" 上线了~");
    }
    // 表示 channel 不处于活动状态，提示 xx 离线了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(sdf.format(new Date()) + " " +ctx.channel().remoteAddress() +" 离线了~");
    }
    // 读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 获取当前channel
        Channel channel = ctx.channel();
        // 这时我们遍历 channelGroup，根据不同的情况，回送不同的消息
        channelGroup.forEach(ch -> {
            if (channel != ch){ // 不是当前用户
                ch.writeAndFlush(sdf.format(new Date()) + " [客户] " + channel.remoteAddress() + " 发送了消息： " + msg +"\n");
            }else{ // 回显自己发送的消息 给自己
                ch.writeAndFlush(sdf.format(new Date()) + " [自己]发送了消息： " + msg + "\r\n");
            }
        });
    }
    // 处理异常

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 关闭通道
        ctx.close();
    }
}
