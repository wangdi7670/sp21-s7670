package gitlet;

import java.io.Serializable;

/**
 * @author: Wingd
 * @date: 2022/7/27 17:36
 *
 * The Head points to the active branch
 */
public class Head implements Serializable {
    private String commitId;

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
