package my;

/**
 * @author: Wingd
 * @date: 2022/7/23 22:39
 */
public class M1 {
    public static void main(String[] args) {
        // 获取 "current working directory"
        String property = System.getProperty("user.dir");
        System.out.println(property);
    }
}
