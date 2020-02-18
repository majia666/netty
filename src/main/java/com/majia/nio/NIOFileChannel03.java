package com.majia.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel03 {
    public static void main(String[] args) throws IOException {

        // 创建 输入流
        FileInputStream fileInputStream = new FileInputStream("E:\\nettyTest/1.txt");
        FileChannel channel01 = fileInputStream.getChannel();

        //创建输出流
        FileOutputStream fileOutputStream = new FileOutputStream("E:\\nettyTest/2.txt");
        FileChannel channel02 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        while (true){// 循环读取

            // 这里有一个重要的操作，一定不能忘了
            /*
            public final Buffer clear() {
                position = 0;
                limit = capacity;
                mark = -1;
                return this;
            }*/
            byteBuffer.clear();// 清空 buffer
            int read = channel01.read(byteBuffer);
            System.out.println("read = " +read);
            if (read == -1 ){ // 表示读完
                break;
            }
            // 将 buffer 中的数据写入到 channel02 --》2.txt
            byteBuffer.flip();

            channel02.write(byteBuffer);
        }
        // 关闭相关输入流
        fileInputStream.close();
        fileOutputStream.close();

    }
}
