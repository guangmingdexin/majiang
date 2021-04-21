package imageloader;

/**
 * @ClassName BinaryTest
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/13 16:29
 * @Version 1.0
 **/
public class BinaryTest {
    public static void main(String[] args) {
        int res = 0;
        res = 1 << 1;
        System.out.println(res);


    }

    public int search(int[] nums, int target) {
        // 简单 二分
        // 确定左边界 及 右边界
        if(nums == null || nums.length == 0) {
            return -1;
        }

        int left = 0;
        int right = nums.length - 1;

        while(left <= right) {
            int middle = (left + right) / 2;

            if(nums[middle] == target) {
                return middle;
            }

            // 左半边有序
            if(nums[left] <= nums[middle]) {
                // 判断 target 位置
                if(target > nums[left] && target < nums[middle]) {
                    right = middle - 1;
                }else {
                    left = middle + 1;
                }
            }else {
                // 右半边有序
                if(target > middle && target < nums[right]) {
                    left = middle + 1;
                }else {
                    right = middle - 1;
                }
            }
        }

        return -1;

    }
}
