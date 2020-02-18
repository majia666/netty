package com.majia.testTcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 实际业务处理类
 */
public class TestTcpNioServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("--------服务注册 通道 -------");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {

        System.out.println("server--->收到客户端发的消息:" + msg);
        ctx.writeAndFlush("Received your message !\n"+msg+'\n');
        System.out.println("server--->服务端返回的信息:" + msg+" # ");
        TestTcpGatewayService.add("11", ctx.channel());
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//   ctx.writeAndFlush("Received your message !\n");
//   ctx.writeAndFlush("你好 !\n");
        super.channelActive(ctx);
        System.out.println("--------通道激活-------");
    }


    /**
     * 响应netty 心跳  这个可以给客户端发送一些特定包 来标识
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            //多少秒没有读
            if (e.state() == IdleState.READER_IDLE) {
                // System.out.println("读超时.....");
            } else if (e.state() == IdleState.WRITER_IDLE) {//多少秒没有写
                // ctx.close();
             // System.out.println("写超时.....");
            }else if (e.state() == IdleState.ALL_IDLE) { //总时间
//                System.out.println("总超时.....");
           // ctx.close();
           // 可以发包了， 然后发出去后  客户单回应一个 这时就不会进入read了 来判断心跳
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        try {
            super.exceptionCaught(ctx, cause);
        } catch (Exception e) {
            System.out.println("服务异常 ....关闭连接");
            ctx.close();
        }
    }
}
