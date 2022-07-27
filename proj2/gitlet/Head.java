package gitlet;

import java.io.File;
import java.io.Serializable;

/**
 * @author: Wingd
 * @date: 2022/7/27 17:36
 *
 * The Head points to the active branch
 */
public class Head implements Serializable {
    private String commitId;

    public static final File FOLDER = Repository.HEAD_DIR;

    public Head(String commitId) {
        this.commitId = commitId;
    }

    public void save() {
        File file = Utils.join(FOLDER, "HEAD");
        Utils.writeObject(file, this);
    }


    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
