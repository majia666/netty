package com.majia.testTcp;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestTcpGatewayService {
    /**
     * 存放客户端连接
     */
    public static Map<String, Channel> map = new ConcurrentHashMap<String, Channel>();
    /**
     * 添加一个客户端连接
     * @param uid
     * @param channel
     */
    public static void add(String uid,Channel channel){
        if (map.containsKey(uid)) {
            Channel temp =map.get(uid);
            if (!(temp.isActive() && temp.isWritable())){
                map.remove(uid);
                map.put(uid, channel);
            }
        } else {
            map.put(uid, channel);
        }
        System.out.println("添加一个客户端uid : " +uid + "当前客户端数量  :"+getClientSize());
    }
    /**
     * 移除一个客户端连接
     * @param uid
     */
    public static void remove(String uid){
        Channel channel = map.remove(uid);
        channel.close();
        System.out.println("删除一个客户端uid : " +uid + "  当前客户端数量  :"+ getClientSize());
    }
    /**
     * 当前客户端数量
     * @return
     */
    public static int getClientSize(){
        return map.size();
    }

}
