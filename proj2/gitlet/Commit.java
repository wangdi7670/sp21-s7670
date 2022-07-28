package gitlet;

// TODO: any imports you need here


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  简化版本的 gitlet 不考虑子目录
 *
 *  When we commit, only the HEAD and active branch move
 *  @author TODO
 */
public class Commit implements Serializable, Dumpable {

    public static final File FOLDER = Repository.COMMITS_DIR;

    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** sha-1算法生成id */
    private String id;

    /** The message of this Commit. */
    private String message;

    /** 用id表示父节点 */
    private String parent;

    /** 创建时候的日期 */
    private String timeStamp;

    private Map<String, String> fileName2blobId;

    public Commit() {
        this("null", "Wed Dec 31 16:00:00 1969 -0800", "initial commit");
    }

    public Commit(String parent, String message) {
        this(parent, dateToTimeStamp(new Date()), message);
    }


    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    public Commit(String parent, String timeStamp, String message) {
        this.parent = parent;
        this.timeStamp = timeStamp;
        this.message = message;
        fileName2blobId = new HashMap<>();
        setId();
    }

    /**
     * 文件名是 id
     */
    public void save() {
        File file = Utils.join(FOLDER, id);
        Utils.writeObject(file, this);
    }

    /**
     * 根据id从文件中读取
     * @param commitId
     * @return
     */
    public static Commit readFromFile(String commitId) {
        File file = Utils.join(FOLDER, commitId);
        Commit commit = Utils.readObject(file, Commit.class);
        return commit;
    }

    /**
     * 返回 parentCommit
     * @return
     */
    public Commit getParentCommit() {
        if (parent.equals("null")) {
            return null;
        }
        return readFromFile(parent);
    }

    private void setId() {
        id = Utils.sha1(parent, timeStamp, message);
    }

    public String getId() {
        return id;
    }


    public Map<String, String> getFileName2blobId() {
        return fileName2blobId;
    }

    public void setFileName2blobId(Map<String, String> fileName2blobId) {
        this.fileName2blobId = fileName2blobId;
    }

    @Override
    public String toString() {
        return "==\n" +
                "commit " + id + "\n" +
                "Date: " + timeStamp + "\n" +
                message + "\n";
    }

    @Override
    public void dump() {
        System.out.println("msg: " + message + "\n"
        + "date: " + timeStamp + "\n"
        + "parent: " + parent + "\n");

        System.out.println("tracked files: ");
        for (String s : fileName2blobId.keySet()) {
            System.out.println(s);
        }
        System.out.println();
    }

    public String getMessage() {
        return message;
    }
}
