package ds.guang.majing.common.util;

import java.util.*;

/**
 * 一些常见的算法
 *
 * @ClassName Algorithm
 * @Author guangmingdexin
 * @Date 2021/6/2 20:14
 * @Version 1.0
 **/
public class Algorithm {


    /**
     * 获取排序数组中 num 的个数（二分算法）
     *
     * @param sort 排序数组
     * @param num 需要匹配的数字
     * @return 数组中 num 的个数
     */
    public static int sortCountArr(List<Integer> sort, int num) {
        int right = 0;
        int left = sort.size() - 1;

        // 寻找右边界
        while (right <= left) {
            int m = (right + left) / 2;
            if(sort.get(m) <= num) {
                right = m + 1;
            }else {
                left = m - 1;
            }
        }

        int i = right;

        if(left >= 0 && sort.get(left) != num) {
            return 0;
        }
         right = 0;
         left = sort.size() - 1;
        // 寻找左边界
        while (right <= left) {
            int m = (right + left) / 2;
            if(sort.get(m) >= num) {
                left = m - 1;
            }else {
                right = m + 1;
            }
        }
        int j = left;
        return i - j - 1;
    }


    /**
     * 提取所有将牌（如果要胡，将牌是必须存在的，而且将牌可能会有多种，手牌必须有序）
     * 取一对将牌，如果没有将牌了，则表示不能胡
     * 将手牌去除选取的将牌，然后判断剩下的牌能否全部形成顺子或者克子
     * 从剩余的牌中最左边的牌开始 , 如果只有一张这样的牌那么这张牌A就只能当作顺子的开头
     * 如果有两张这样的牌 , 因为已经有了一对将而这两张也不能组成克子 , 所以这两张只能当作两个顺子的开头
     *  如果有三张这样的牌 , 可以组成刻子，也可以组成顺子(两种选择一致)
     *  {
     *      AAA （优先组成刻子）
     *      AAABBBCCC
     *  }
     *  如果有四张这样的牌,同样可以组成刻子或者顺子(都可以，优先组成刻子)
     *  {
     *
     *      AAAABBBBCCCC
     *      AAA ABC
     *  }
     * for example:
     *   {1,1,2,2,2,3,4,11,12,12,13,13,14,1}
     *
     *   1. {1,1}(将牌) , {1,2,2,2,3,4,11,12,12,13,13,14}(余牌)
     *
     * 　　2. {2,2}(将牌) , {1,1,1,2,3,4,11,12,12,13,13,14}(余牌)
     *
     * 　　3. {12,12}(将牌) , {1,1,1,2,2,2,3,4,11,13,13,14}(余牌)
     *
     * 　　4. {13,13}(将牌) , {1,1,1,2,2,2,3,4,11,12,12,14}(余牌)
     *
     *   步骤二: 余牌数量为0 则返回 “能胡牌” 否则进入下一步 .
     *
     * 步骤三: 判断余牌前三张是否相同 相同-> 步骤四 ; 不同 -> 步骤五.
     *
     * 步骤四: 移除余牌中的前三张牌 , 返回步骤二.
     *
     * 步骤五: 若余牌中第一个数为N , 则判断是否有N + 1 与 N + 2 同时存在与余牌中 , 有将N , n+1 , n+2 从余牌中移除并返回 步骤二 , 否则返回 步骤一
     *
     *  {2,2}(将牌) , {1,1,1,2,3,4,11,12,12,13,13,14}(余牌)
     *
     * 　　　　步骤二 –> 步骤三 –> 步骤四 == {2,3,4,11,12,12,13,13,14}(余牌) –>
     *
     * 　　　　步骤二 –> 步骤三 –> 步骤五 == {11,12,12,13,13,14}(余牌)–>
     *
     * 　　　　步骤二 –> 步骤三 –> 步骤五 == {12,13,14}(余牌)–>
     *
     * 　　　　步骤二 –> 步骤三 –> 步骤五 == {}(余牌) –>
     *
     * 　　　　步骤二 “能胡牌”
     *
     * @param cards 手牌
     * @return 是否胡牌
     */
    public static boolean isHu(List<Integer> cards) {
        if(cards == null) {
            return false;
        }

        // 胡的牌的个数必须是 2 或 5 或 8 或 11 或 14
        if (cards.size() % 3 != 2 && cards.size() <= 14) {
            return false;
        }

        List<Integer> duiZi = getDuiZi(cards);
        for (Integer e : duiZi) {
            // 保护性拷贝（所有修改不会改变原数组）
            List<Integer> copyCards = new ArrayList<>(cards);
            // 去掉对子
            copyCards.remove(e);
            copyCards.remove(e);

            if (copyCards.size() <= 0 || cards.size() == 7) {
                return true;
            }

            if(isHuDfs(copyCards)) {
                return true;
            }
        }

        return false;
    }



    /**
     * 判断是否能胡牌
     * @param cards 牌组
     */
    private static boolean isHuDfs(List<Integer> cards) {


        if(cards.size() == 0) {
            return true;
        }

        Integer item = cards.get(0);

        // 判断能否组成刻子
        if(sortCountArr(cards, item) >= 3) {
            cards.remove(item);
            cards.remove(item);
            cards.remove(item);
            return isHuDfs(cards);
        }else {
            // 组成顺子
            Integer index1 = item + 1;
            Integer index2 = item + 2;
            if(binarySearch(cards, index1, false) >= 0 && binarySearch(cards, index2, false) >= 0) {
                cards.remove(item);
                cards.remove(index1);
                cards.remove(index2);
                return isHuDfs(cards);
            }

            return false;
        }

    }

    /**
     * 返回手牌中所有的麻将对子
     *
     * @param cards 手牌
     * @return 麻将对子
     */
    public static List<Integer> getDuiZi(List<Integer> cards) {
        List<Integer> dui = new ArrayList<>();
        for (int i = 0; i < cards.size() - 1; i++) {
            Integer e = cards.get(i);
            if(e.equals(cards.get(i + 1))) {
                if(!(i > 0 && cards.get(i - 1).equals(e))) {
                    dui.add(e);
                }
            }
        }

        return dui;
    }


    /**
     *
     * 在牌组中去掉一张牌
     *
     * @param cards 手牌
     * @param card  要移出的牌
     * @return 去掉牌后的牌组
     */
    public static List<Integer> removeOne(List<Integer> cards, Integer card) {
        if(cards == null || card == null ) {
            return cards;
        }

        int index = binarySearch(cards, card, false);

        if(index >= 0) {
            cards.remove(index);
        }

        return cards;
    }

    /**
     *
     * 二分查找
     *
     * @param nums 手牌集合
     * @param num 手牌
     * @param isInsert 是否需要插入
     * @return 插入的下标
     */
    public static int binarySearch(List<Integer> nums, Integer num, boolean isInsert) {

        // 1.确定范围 [0, nums.len - 1] 闭区间
        // 2. [left, mid - 1], [mid + 1, right]
        // 3. 循环条件：left <= right
        // 4. 存在元素，不存在元素 在 nums 中

        int low = 0;
        int high = nums.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Integer e = nums.get(mid);
            if(e.equals(num)) {
                return mid;
            }else if(e.compareTo(num) > 0){
                high = mid - 1;
            }else {
                low = mid + 1;
            }
        }
        // 没有找到元素
        // 分别处理如下四种情况
        // 目标值在数组所有元素之前  [0, -1]
        // 目标值等于数组中某一个元素  return middle;
        // 目标值插入数组中的位置 [left, right]，return  right + 1
        // 目标值在数组所有元素之后的情况 [left, right]， return right + 1
        return isInsert ? high + 1 : (-low + 1);

    }




    /**
     *
     * 从 sort list 中查找是否出现四个重复的值
     *
     * @param nums
     * @return
     */
    public static List<Integer> sortCountFour(List<Integer> nums) {

        List<Integer> four = new ArrayList<>();

        for (Integer num : nums) {
            if(sortCountArr(nums, num) == 4) {
                four.add(num);
            }
        }
        return four;
    }

    public static boolean sortCountThree(List<Integer> nums, Integer num) {
        return sortCountArr(nums, num) == 3;
    }


    public static void main(String[] args) {
//        List<Integer> list = Arrays.asList(11, 11, 17, 103, 105, 106, 107, 109, 109, 1004, 1005, 1006, 1009);
//        List<Integer> copyList = new ArrayList<>(list);

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(4);
        list.add(7);
        list.add(9);

        System.out.println("list：" + list);


        //
//        list.add(1);
//        list.add(2);
//        list.add(2);
//        list.add(10);
//        list.add(11);
//        list.add(11);
//        for (int i = 1; i <= 2; i++) {
//            int index = Algorithm.binarySearch(copyList, 109);
//            if (index < 0) {
//                throw new NullPointerException("手牌中不存在这张牌！");
//            }
//            System.out.println("index：" + index);
//            System.out.println("copyList；" + copyList);
//            copyList.remove(index);
//        }

//        int[] h = {11, 12, 13, 14, 14, 15, 16, 16, 16, 16, 17, 18, 19, 19};
//        int[] d = {11, 12, 12, 13, 13, 14, 15, 15};
//
//      // List<Integer> l = Arrays.asList(11, 12, 13, 14, 14, 15, 16, 16, 16, 16, 17, 18, 19, 19);
//        List<Integer> l = Arrays.asList(11, 12, 12, 13, 13, 14, 15, 15);
//       // System.out.println(isHu(l));
//       sortInsert(list, 18);
//        sortInsert(list, -1);
////       sortInsert(list, 9);
//       sortInsert(list, 11);
//        System.out.println(list);


    }
}
