package com.guang.majiang.event;

import com.guang.majiang.common.Direction;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerNode;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * @ClassName SortedHandCardTask
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/8 11:19
 * @Version 1.0
 **/
public class SortedHandCardTask implements Runnable {

    private PlayerNode<Player> p;

    public SortedHandCardTask(PlayerNode<Player> p) {
        this.p = p;
    }

    @Override
    public void run() {
        // 获取当前玩家手牌
        // 快速排序，交换的时候同时变换 x, y
        if(p.item.getDirection() == Direction.UNDER) {
            List<CardImage> handCard = p.item.getPlayerCard().getCards();
            quickSort(handCard, 0, handCard.size() - 1);
        }
    }

    public void quickSort(List<CardImage> handCard, int left, int right) {
        if(left < right) {
            int m = pivot(handCard, left, right);
            quickSort(handCard, left, m - 1);
            quickSort(handCard, m + 1, right);
        }
    }

    private int pivot(List<CardImage> handCard, int left, int right) {
        CardImage pivot = handCard.get(left);
        while (left < right) {
            while (left < right && pivot.compareTo(handCard.get(right)) <= 0) {
                right --;
            }
            if(left < right) {
                // 交换
                swap(handCard, left, right);
                // 将比支点值小的记录向前移动
                left ++;
            }

            while (left < right && pivot.compareTo(handCard.get(left)) > 0) {
                left ++;
            }
            if(left < right) {
                swap(handCard, left, right);
                // 将比支点值大的记录向后移动
                right --;
            }
        }
        handCard.set(left, pivot);
        return left;
    }

    private void swap(List<CardImage> handCard, int i, int j) {
        // 除了交换位置
        // 图片的 x , y 也需要交换
        double iX = handCard.get(i).getImageView().getX();
        double iY = handCard.get(i).getImageView().getY();
        double jX = handCard.get(j).getImageView().getX();
        double jY = handCard.get(j).getImageView().getY();
        Collections.swap(handCard, i, j);
        handCard.get(i).getImageView().setX(iX);
        handCard.get(i).getImageView().setY(iY);
        handCard.get(j).getImageView().setX(jX);
        handCard.get(j).getImageView().setY(jY);
    }
}
