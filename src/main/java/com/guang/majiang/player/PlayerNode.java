package com.guang.majiang.player;

/**
 * @ClassName NodePlayer
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/4 9:39
 * @Version 1.0
 **/
public class PlayerNode<E> {

    public E item;
    public PlayerNode<E> next;
    public PlayerNode<E> prev;

    public PlayerNode(PlayerNode<E> prev, E element, PlayerNode<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }

    public PlayerNode(E item) {
        this.item = item;
    }
}
