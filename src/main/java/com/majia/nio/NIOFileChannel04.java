package com.majia.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel04 {
    public static void main(String[] args) throws IOException {

        // 创建相关的流
        FileInputStream fileInputStream = new FileInputStream("E:\\nettyTest/a1.png");
        FileOutputStream fileOutputStream = new FileOutputStream("E:\\nettyTest/a2.png");

        // 获取各个流对应的channel
        FileChannel sourceChannel = fileInputStream.getChannel();
        FileChannel destChannel = fileOutputStream.getChannel();

        // 使用 transferFrom 完成拷贝
        destChannel.transferFrom(sourceChannel,0,sourceChannel.size());

        // 关闭 相关的通道和流
        sourceChannel.close();
        destChannel.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
