package my;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author: Wingd
 * @date: 2022/7/19 16:25
 */
public class Test1 {
    public static void main(String[] args) {

        int[] src = new int[]{1, 2, 3, 4 ,5};
        int[] dest = new int[10];
        System.arraycopy(src, 0, dest, 0, src.length);
        System.out.println(Arrays.toString(dest));
    }

    @Test
    public void test1() {
        System.out.printf("%2s\n", "王");
        System.out.println(" 迪");

        System.out.printf("%45.2f", 3.1);
    }
}
