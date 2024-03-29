package gitlet;

import java.io.File;
import java.io.Serializable;

/**
 * @author: Wingd
 * @date: 2022/7/27 17:36
 *
 * The Head points to the active branch
 */
public class Head implements Serializable, Dumpable {
    private String branchName;

    private String commitId;

    public static final File FOLDER = Repository.HEAD_DIR;
    public static final String FILE_NAME = "HEAD";

    public Head(String branchName, String commitId) {
        this.branchName = branchName;
        this.commitId = commitId;
    }

    /**
     * move head
     * @param branchName: moved branch name
     * @param commitId: commit id of given branch
     */
    public void move(String branchName, String commitId) {
        this.branchName = branchName;
        this.commitId = commitId;
    }

    /**
     * persist, file name is 'HEAD'
     */
    public void save() {
        File file = Utils.join(FOLDER, FILE_NAME);
        Utils.writeObject(file, this);
    }

    public static Head readFromFile() {
        File file = Utils.join(FOLDER, FILE_NAME);
        Head head = Utils.readObject(file, Head.class);
        return head;
    }

    /**
     * return current commit
     * @return
     */
    public Commit getCurrentCommit() {
        Commit currentCommit = Commit.readFromFile(commitId);
        return currentCommit;
    }


    /**
     * return Branch mapped by HEAD
     * @return
     */
    public Branch getCurrentBranch() {
        Branch currentBranch = Branch.readBranchFromFile(branchName);
        return currentBranch;
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
        File f = Utils.join(Repository.BRANCHES, branchName);
        Branch branch = Utils.readObject(f, Branch.class);
        System.out.println("HEAD: ");
        branch.dump();
    }
}
