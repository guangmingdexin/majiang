package com.guang.majiang.common;

/**
 * @author asus
 */

public enum CardType {

    BAMBOO("bamboo", 0),

    CHARACTER("character", 1),

    DOT("dot", 2),

    FACE_DOWN("face-down", 3);

    private int value;

    private String patten;

    CardType(String patten, int value) {
        this.patten = patten;
        this.value = value;
    }
}
