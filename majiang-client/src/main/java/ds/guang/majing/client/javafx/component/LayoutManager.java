package ds.guang.majing.client.javafx.component;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.Stack;

/**
 *
 * 界面管理器，实质就是栈，当玩家进行游戏界面跳转时，可以通过改数据结果保存界面
 * 返回上一次游戏界面
 * 不是线程安全的
 *
 * @author guangmingdexin
 */
public class LayoutManager {

    /**
     *
     */
    private LinkedList<Layout> layoutStack;

    @Getter
    @Setter
    private Layout curLayout;


    public final static LayoutManager INSTANCE = new LayoutManager();

    private LayoutManager() {
        layoutStack = new LinkedList<>();
    }


    public Layout prev() {

        if(layoutStack.isEmpty()) {
            throw new IllegalArgumentException("当前已是最初界面");
        }

        return layoutStack.pop();
    }


    public void next(Layout layout) {
        layoutStack.push(layout);
    }
}
