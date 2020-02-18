package com.majia.testTcp;

public class TestOthers {
    public static void main(String[] args) {
        // 查找代码运行在什么操作系统
        String osName = System.getProperties().getProperty("os.name");
        System.out.println("操作系统的名称为： " + osName);
    }
}
