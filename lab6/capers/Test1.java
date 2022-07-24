package capers;

// import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author: Wingd
 * @date: 2022/7/23 23:57
 */
public class Test1 {
    public static void main(String[] args) {
        // test_serialize();
        // deserialize();
        // test_path();
        // test_path2();
        test_create();
    }

    static void test_create() {
        File cwd = new File(System.getProperty("user.dir"));
        File story = Utils.join(cwd, ".capers", "story.txt");
        System.out.println(story.getPath());

        if (!story.exists()) {
            try {
                story.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void test_path() {
        File capers = Utils.join("capers");
        System.out.println(capers.getPath());
    }

    static void test_path2() {
        File CWD = new File(System.getProperty("user.dir"));
        System.out.println(CWD.getPath());

        File f1 = Utils.join(CWD, ".capers");
        System.out.println(f1.getPath());
    }

    public static void test_serialize() {
        File f = new File("capers\\dogs\\a.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Dog d = new Dog("a", "breed", 2);
        Utils.writeObject(f, d);
    }

    static void deserialize() {
        File f = new File("capers\\dogs\\a.txt");
        Dog dog = Utils.readObject(f, Dog.class);
        System.out.println(dog.toString());
    }


    // @Test
    public void test1() {
        String text = "123";
        File f = new File("capers\\story.txt");
        Utils.writeContents(f, text + "\n");
    }

    // @Test
    public void test2() {
        Dog dog = new Dog("Mammoth", "品种", 10);
    }

    // @Test
    public void test3() {
        File file = CapersRepository.CAPERS_FOLDER;
        System.out.println(file.getPath());

        File f2 = Utils.join(file, "story.txt");
        System.out.println(f2.getPath());
    }
}
