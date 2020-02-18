package com.majia.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel01 {
    public static void main(String[] args) throws IOException {
        String str = "hello,马佳";
        //创建一个输出流  --> channel
         FileOutputStream fileOutputStream = new FileOutputStream("E:\\nettyTest/file01.txt");
         //通过 fileOutputStream 获取对应的 FileChannel
        // 这个fileChannel 真实类型是 FileChannelImpl
        FileChannel channel = fileOutputStream.getChannel();

        // 创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 将 str 放入到byteBuffer
        byteBuffer.put(str.getBytes());
        // 对 ByteBuffer 进行 flip
        byteBuffer.flip();

        // 将 ByteBuffer 数据写入到 fileChannel
        channel.write(byteBuffer);
        fileOutputStream.close();


    }
}
