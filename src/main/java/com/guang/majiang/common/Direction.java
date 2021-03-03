package com.guang.majiang.common;

/**
 * @ClassName Direction
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/2/4 20:57
 * @Version 1.0
 **/
public enum  Direction {

    UNDER("下"),
    LEFT("左"),
    ABOVE("上"),
    RIGHT("右");


    private String direction;

    Direction(String s) {
        this.direction = s;
    }
}
