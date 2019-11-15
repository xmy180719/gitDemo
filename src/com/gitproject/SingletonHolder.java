package com.gitproject;

public class SingletonHolder {
    public static class Singleton {
        private static final SingletonHolder instance = new SingletonHolder();
    }
    private SingletonHolder(){}
    public static final SingletonHolder getInstance(){
        return Singleton.instance;
    }
}
