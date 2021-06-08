package gameTest;

/**
 * @ClassName CharMultipy
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/22 10:40
 * @Version 1.0
 **/
public class CharMultipy {
    public static void main(String[] args) {
        char a = '1';
        char b = '9';
        System.out.println(new CharMultipy().multiply("0", "1"));
        System.out.println(new CharMultipy().subOne("100"));
    }

    public String subOne(String num) {

        if("0".equals(num) || num == null || num.length() == 0) {
            return "0";
        }

        char[] n = num.toCharArray();

        int len = n.length;
        int[] nums = new int[len];

        int i = len - 1;

        int carry = -1;

        for(; i >= 0; i--) {
            int temp = (n[i] - '0') + carry;

            if(temp < 0) {
                nums[i] = 10 + temp;
            }else {
                nums[i] = temp;
                carry = 0;
            }
        }

        StringBuilder res = new StringBuilder();
        int start = 0;
        for(; start < nums.length; start ++) {
            if(nums[start] != 0) {
                break;
            }
        }

        for(; start < nums.length; start ++) {
            res.append(nums[start]);
        }

        return res.toString();
    }

    public String multiply(String num1, String num2) {
        // 单个位数 相乘
        // 最后对齐
        // 两个整数相加
        return add(num1, num2);
    }



    public String add(String num1, String num2) {
        int carry = 0;

        char[] n1 = num1.toCharArray();
        char[] n2 = num2.toCharArray();

        int len1 = n1.length;
        int len2 = n2.length;

        int[] nums = new int[Math.max(len1, len2) + 1];
        int i = len1 - 1, j = len2 - 1, k = nums.length - 1;
        for(; i >= 0 && j >= 0; i --, j--) {
            int temp = (n1[i] - '0') + (n2[j] - '0') + carry;
            nums[k --] = temp % 10;
            carry = temp / 10;
        }


        // 如果位数不一致
        while(k >= 0 && i >= 0) {
            int temp = (n1[i --] - '0') + carry;
            nums[k --] = temp % 10;
            carry = temp / 10;
        }

        while(k >= 0 && j >= 0) {
            int temp = (n1[j --] - '0') + carry;
            nums[k --] = temp % 10;
            carry = temp / 10;
        }

        nums[0] = carry;

        StringBuilder res = new StringBuilder();

        int start = nums[0] == 1 ? 0 : 1;

        for(; start < nums.length; start ++) {
            res.append(nums[start]);
        }
        return res.toString();
    }
}
