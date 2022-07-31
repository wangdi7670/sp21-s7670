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
     * return all the branch names
     * @return
     */
    public static String[] listAllBranchNames() {
        String[] branchNames = FOLDER.list();
        return branchNames;
    }

    /**
     * persist a branch whose file name is its branch name
     */
    public void save() {
        File file = Utils.join(FOLDER, branchName);
        Utils.writeObject(file, this);
    }

    /**
     * read branch from given branch name
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
     * return commit mapped by this branch
     * @return :
     */
    public Commit getCommit() {
        Commit commit = Commit.readFromFile(commitId);
        return commit;
    }

    /**
     * whether given branch exist
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
