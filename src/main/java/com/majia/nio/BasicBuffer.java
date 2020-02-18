package com.majia.nio;

import java.nio.IntBuffer;

public class BasicBuffer {
    public static void main(String[] args) {
        //举例说明Buffer 的使用（简单说明）
        // 创建一个Buffer，大小为 5，即可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);
        // 向Buffer存放数据
//        intBuffer.put(10);
//        intBuffer.put(11);
//        intBuffer.put(12);
//        intBuffer.put(13);
//        intBuffer.put(14);

        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }
        // 如何从buffer的读取数据
        // 将buffer转换，读写切换；
        intBuffer.flip(); // 这个 很重要！！！！
        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }

    }
}
