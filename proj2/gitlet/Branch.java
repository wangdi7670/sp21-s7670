package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.channels.SelectionKey;
import java.util.List;

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
     * 返回所有 branch 的名字
     * @return
     */
    public static String[] listAllBranchNames() {
        String[] branchNames = FOLDER.list();
        return branchNames;
    }

    /**
     * 持久化，文件名是分支名
     */
    public void save() {
        File file = Utils.join(FOLDER, branchName);
        Utils.writeObject(file, this);
    }

    /**
     * 从文件中读出Branch
     * @param branchName
     * @return
     */
    public static Branch readBranchFromFile(String branchName) {
        File file = Utils.join(FOLDER, branchName);
        if (!file.exists()) {
            return null;
        }

        Branch branch = Utils.readObject(file, Branch.class);
        return branch;
    }

    /**
     * 获取该branch指向的 commit
     * @return :
     */
    public Commit getCommit() {
        Commit commit = Commit.readFromFile(commitId);
        return commit;
    }

    /**
     * 判断某个分支是否存在
     * @param branchName
     * @return
     */
    public static boolean isBranchExist(String branchName) {
        File file = Utils.join(Repository.BRANCHES, branchName);
        return file.exists();
    }

    public void move(String commitId) {
        this.commitId = commitId;
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
    public String toString() {
        return "Branch{" +
                "commitId='" + commitId + '\'' +
                ", branchName='" + branchName + '\'' +
                '}';
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
