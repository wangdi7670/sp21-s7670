package my;

import java.io.File;
import java.io.IOException;
// import capers.Utils;

/**
 * @author: Wingd
 * @date: 2022/7/23 22:54
 */
public class M2 {
    public static void main(String[] args) throws IOException {
        File f = new File("my\\dummy.txt");
        if (!f.exists()) {
            f.createNewFile();
        }

        // Ut
    }
}
