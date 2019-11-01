package com.gitproject;

public class TestUser {
    public static void main(String[] args) {
        System.out.println("这是1.0分支新增的输出");
        System.out.println("测试冲突！");
        User user = new User();
        user.setAddress("广东佛山");
        user.setAge(16);
        user.setName("Jack");
        System.out.println(user.toString());
    }
}
