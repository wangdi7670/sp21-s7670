package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.channels.SelectionKey;

/**
 * @author: Wingd
 * @date: 2022/7/27 17:37
 *
 * A branch is a pointer to a commit
 */
public class Branch implements Serializable, Dumpable {
    public static final File FOLDER = Repository.BRANCHES;

    private String commitId;

    private String branchName;

    public Branch(String commitId, String branchName) {
        this.commitId = commitId;
        this.branchName = branchName;
    }

    /**
     * 持久化，文件名是分支名
     */
    public void save() {
        File file = Utils.join(FOLDER, branchName);
        Utils.writeObject(file, this);
    }

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

    @Override
    public void dump() {
        System.out.println("branchName: " + branchName);
        System.out.println("===");
        File f = Utils.join(Repository.COMMITS_DIR, commitId);
        Commit commit = Utils.readObject(f, Commit.class);
        commit.dump();
    }
}
