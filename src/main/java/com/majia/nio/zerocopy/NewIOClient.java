package com.majia.nio.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewIOClient {

    public static void main(String[] args) throws Exception {

        // 创建 SocketChannel
        SocketChannel socketChannel = SocketChannel.open();

        // 创建连接
        socketChannel.connect(new InetSocketAddress("127.0.0.1",7001));

        // 得到一个文件
        String fileName = "E:\\nettyTest/test.zip";
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();

        // 准备发送
        long startTime = System.currentTimeMillis();

        /**
         * 在 linux 下，一个transferTo 方法就可以完成传输
         * 在 windows 下，一次调用 transferTo 只能发送8M，就需要分段传输文件，而且要注意 传输时的位置
         * transferTo 底层使用到零拷贝
         */
        // 获取代码运行的操作系统
        String osName = System.getProperties().getProperty("os.name");

        long transferCount = 0;
        if(osName.contains("Win")){ // 运行系统为windows 系统
            long size = fileChannel.size();
            // 判断 文件是否 大于 8M
            if (size <= 8 * 1024 * 1024){ // 如果文件 不大于 8M
                transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
            }else {
                // 先判断 文件大小是否能被 8M 整除
                long isExactly = size % (8 * 1024 * 1024);
                // 循环次数
                long i = size / (8 * 1024 * 1024);
                if(isExactly == 0){ // 如果能被 8M 整除
                    for (int j = 0; j <= i  ; j++) {
                        transferCount += fileChannel.transferTo(j* 8 * 1024 *1024, 8 * 1024 *1024, socketChannel);
                    }
                }else { // 如果不能被 8M 整除
                    i = i +1;
                    for (int j = 0; j <= i  ; j++) {
                        if(j == i){
                            transferCount += fileChannel.transferTo(j* 8 * 1024 *1024, size - (8 * 1024 *1024) * (j-1), socketChannel);
                        }
                        transferCount += fileChannel.transferTo(j* 8 * 1024 *1024, 8 * 1024 *1024, socketChannel);
                    }
                }
            }

        }
        if (osName.contains("Linux")){ // 运行系统为 Linux 系统
            transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
        }
        System.out.println("发送的总的字节数 = " + transferCount + " 耗时： " + (System.currentTimeMillis() - startTime));
    }
}
