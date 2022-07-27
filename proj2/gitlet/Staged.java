package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Wingd
 * @date: 2022/7/27 19:16
 */
public class Staged implements Serializable, Dumpable {
    /** key 是文件名， value是blobId */
    private Map<String, String> stagedForAdd;

    /** key 是文件名， value是blobId */
    private Map<String, String> stagedForRemoval;

    private static final String FILE_NAME = "staging";

    /** 存储该对象的文件夹 */
    public static final File FOLDER = Repository.STAGING_AREA;

    public Staged() {
        stagedForAdd = new HashMap<>();
        stagedForRemoval = new HashMap<>();
    }

    public Map<String, String> getStagedForAdd() {
        return stagedForAdd;
    }

    public void setStagedForAdd(Map<String, String> stagedForAdd) {
        this.stagedForAdd = stagedForAdd;
    }

    public Map<String, String> getStagedForRemoval() {
        return stagedForRemoval;
    }

    public void setStagedForRemoval(Map<String, String> stagedForRemoval) {
        this.stagedForRemoval = stagedForRemoval;
    }

    /**
     * 持久化
     */
    public void save() {
        File f = Utils.join(FOLDER, FILE_NAME);
        Utils.writeObject(f, this);
    }

    public static Staged readFromFile() {
        File f = Utils.join(FOLDER, FILE_NAME);
        Staged staged = Utils.readObject(f, Staged.class);
        return staged;
    }

    @Override
    public void dump() {

    }
}
