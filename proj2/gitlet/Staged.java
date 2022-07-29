package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * @author: Wingd
 * @date: 2022/7/27 19:16
 */
public class Staged implements Serializable, Dumpable {
    /** key 是文件名， value是blobId */
    private Map<String, String> stagedForAdd;

    /** key 是文件名， value是blobId */
    // private Map<String, String> stagedForRemoval;
    private Set<String> stagedForRemoval;

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        set.add("c");

        List<String> list = new ArrayList<>(set);
        System.out.println(list);
    }
    private static final String FILE_NAME = "staging";

    /** 存储该对象的文件夹 */
    public static final File FOLDER = Repository.STAGING_AREA;

    public Staged() {
        stagedForAdd = new HashMap<>();
        stagedForRemoval = new HashSet<>();
    }

    public Map<String, String> getStagedForAdd() {
        return stagedForAdd;
    }


    public Set<String> getStagedForRemoval() {
        return stagedForRemoval;
    }


    /**
     * 持久化
     */
    public void save() {
        File f = Utils.join(FOLDER, FILE_NAME);
        Utils.writeObject(f, this);
    }

    /**
     * 读出 staged 对象
     * @return
     */
    public static Staged readFromFile() {
        File f = Utils.join(FOLDER, FILE_NAME);
        Staged staged = Utils.readObject(f, Staged.class);
        return staged;
    }

    /**
     * 列出 staged area for addition 中的所有文件名
     * @return
     */
    public List<String> listAllStagedFileNamesForAdd() {
        Set<String> strings = stagedForAdd.keySet();
        List<String> list = new ArrayList<>();

        for (String s : strings) {
            list.add(s);
        }
        return list;
    }

    /**
     * 列出所有 staged for removal 中的文件名
     * @return
     */
    public List<String> listAllStagedFileNamesForRemoval() {
        List<String> list = new ArrayList<>();

        for (String s : stagedForRemoval) {
            list.add(s);
        }
        return list;
    }


    /**
     * 清空staged area
     */
    public void clearStaged() {
        stagedForAdd.clear();
        stagedForRemoval.clear();
    }


    @Override
    public void dump() {
        System.out.println("Staged dump: ");
        System.out.println("staged for addition = ");
        List<String> forAdd = listAllStagedFileNamesForAdd();
        for (String s : forAdd) {
            System.out.println(s);
        }
        System.out.println("staged for removal = ");
        List<String> forRemoval = listAllStagedFileNamesForRemoval();
        for (String s : forRemoval) {
            System.out.println(s);
        }
        System.out.println();
    }
}
