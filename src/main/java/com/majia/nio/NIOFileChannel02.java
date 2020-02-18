package com.majia.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel02 {
    public static void main(String[] args) throws IOException {

        // 创建文件的输入流
        File file = new File("E:\\nettyTest/file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
         //通过 fileInputStream 获取对应的 FileChannel
        // 这个fileChannel 真实类型是 FileChannelImpl
        FileChannel channel = fileInputStream.getChannel();

        // 创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());

        // 将 通道的数据读入到 Buffer 中
        channel.read(byteBuffer);

        // 将 ByteBuffer 的字节数据转换成字符串
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();

    }
}
