package com.seezoon.front.wechat.utils;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {

    private Map<String,Object> map = new HashMap<>();

    public MapBuilder add(String key,Object value){
        map.put(key,value);
        return this;
    }
    public Map<String,Object> build(){
        return map;
    }
}
