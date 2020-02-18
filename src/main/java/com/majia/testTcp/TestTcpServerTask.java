package com.majia.testTcp;

import io.netty.channel.Channel;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class TestTcpServerTask implements  Runnable {
    @Override
    public void run() {
        Channel channel = null;
        Map.Entry<String, Channel> entry = null;
        while (true) {
            Iterator<Map.Entry<String, Channel>> it = TestTcpGatewayService.map.entrySet().iterator();
            while (it.hasNext()) {
                entry = it.next();
                channel = entry.getValue();
                if (channel.isActive() && channel.isWritable()) {
                    entry.getValue().writeAndFlush(new Date() + "我是测试的服务器端向客户端发数据");
                } else {
                    channel.close();
                    it.remove();
                    System.out.println("通道不能连接uid:{ },服务器关闭和删除它 "+ entry.getKey());
// logger.info("channel cannot connect uid : {}, server close and remove it" ,entry.getKey());
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
