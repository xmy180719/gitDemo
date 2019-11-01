package com.gitproject;

public class TestUser {
    public static void main(String[] args) {

        User user = new User();
        user.setAddress("广东佛山");
        user.setAge(16);
        user.setName("Jack");
        System.out.println(user.toString());
    }
}
