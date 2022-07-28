package gitlet;

// TODO: any imports you need here

import javax.swing.*;
import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

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
    private String date;

    private Map<String, String> fileName2blobId;

    public Commit() {
        this("null", "00:00:00 UTC, Thursday, 1 January 1970", "initial commit");
    }

    public Commit(String parent, String message) {
        this(parent, new Date().toString(), message);
    }

    public Commit(String parent, String date, String message) {
        this.parent = parent;
        this.date = date;
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

    public static Commit readFromFile(String commitId) {
        File file = Utils.join(FOLDER, commitId);
        Commit commit = Utils.readObject(file, Commit.class);
        return commit;
    }

    private void setId() {
        id = Utils.sha1(parent, date, message);
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
    public void dump() {
        System.out.println("msg: " + message + "\n"
        + "date: " + date + "\n"
        + "parent: " + parent + "\n");
    }
}
