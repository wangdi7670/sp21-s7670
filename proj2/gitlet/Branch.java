package gitlet;

import java.io.Serializable;
import java.nio.channels.SelectionKey;

/**
 * @author: Wingd
 * @date: 2022/7/27 17:37
 *
 * A branch is a pointer to a commit
 */
public class Branch implements Serializable {
    private String commitId;

    private String branchName;

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
