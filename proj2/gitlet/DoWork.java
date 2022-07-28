package gitlet;

import java.beans.FeatureDescriptor;
import java.io.File;
import java.util.*;

/**
 * @author: Wingd
 * @date: 2022/7/27 19:20
 */
public class DoWork {

    private void checkInitialize() {
        if (!Repository.isInitialized()) {
            System.out.println("还未初始化呢~~");
            System.exit(0);
        }
    }

    /**
     * init command:
     */
    public void init() {
        if (Repository.isInitialized()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        doInit();
    }

    private void doInit() {
        // 创建初始的文件夹
        Repository.initDir();

        // 初始化
        Commit initCommit = new Commit();
        Branch branch = new Branch(initCommit.getId(), "master");
        Head head = new Head(branch.getBranchName(), initCommit.getId());
        Staged staged = new Staged();

        // 持久化
        initCommit.save();
        branch.save();
        head.save();
        staged.save();
    }

    /**
     *  add:
     * @param fileName
     */
    public void add(String fileName) {
        checkInitialize();

        File file = Utils.join(Repository.CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        doAdd(file);
    }


    private void doAdd(File file) {
        String fileName = file.getName();

        // 直接加入staged area, 如果之前有的话会覆盖掉之前的
        byte[] content = Utils.readContents(file);
        Blob blob = new Blob(fileName, content);
        blob.save();
        Staged staged = Staged.readFromFile();
        staged.getStagedForAdd().put(fileName, blob.getBlobId());

        // 加入的blob要是和当前commit中的版本一致，则从staged area中删除
        Head head = Head.readFromFile();
        Commit currentCommit = Commit.readFromFile(head.getCommitId());
        Map<String, String> fileName2blobId = currentCommit.getFileName2blobId();
        if (fileName2blobId.containsKey(fileName)) {
            if (fileName2blobId.get(fileName).equals(blob.getBlobId())) {
                staged.getStagedForAdd().remove(fileName);
            }
        }

        staged.save();
    }


    /**
     * commit 命令: When we commit, only the HEAD and active branch move
     *
     * @param msg
     */
    public void commit(String msg) {
        // 1. 检查初始化
        checkInitialize();

        // 2. staging area 要是空的话就直接退出
        Staged staged = Staged.readFromFile();
        if (staged.getStagedForRemoval().isEmpty() && staged.getStagedForAdd().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        doCommit(msg);
    }

    private void doCommit(String msg) {
        // create new commit
        Head head = Head.readFromFile();
        Commit oldCommit = head.getCurrentCommit();
        Commit newCommit = new Commit(oldCommit.getId(), msg);
        newCommit.setFileName2blobId(oldCommit.getFileName2blobId());

        // get staged area
        Staged staged = Staged.readFromFile();
        Map<String, String> stagedForAdd = staged.getStagedForAdd();
        Map<String, String> fileName2blobId = newCommit.getFileName2blobId();

        // staged for addition
        for (Map.Entry<String, String> entry : stagedForAdd.entrySet()) {
            String fileName = entry.getKey();
            String blobId = entry.getValue();
            fileName2blobId.put(fileName, blobId);
        }

        // staged for removal
        Set<String> stagedForRemoval = staged.getStagedForRemoval();
        for (String fileName : stagedForRemoval) {
            fileName2blobId.remove(fileName);
        }

        // clear staged
        staged.clearStaged();

        // move branch and HEAD
        Branch currentBranch = head.getCurrentBranch();
        currentBranch.move(newCommit.getId());
        head.move(currentBranch.getBranchName(), currentBranch.getCommitId());

        // persistence
        staged.save();
        newCommit.save();
        head.save();
        currentBranch.save();
    }


    /**
     * rm command:  Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit,
     * stage it for removal and remove the file from the working directory
     * if the user has not already done so (do not remove it unless it is tracked in the current commit).
     * @param fileName
     */
    public void rm (String fileName) {
        checkInitialize();

        File file = Utils.join(Repository.CWD, fileName);
        if (!file.exists()) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        if (file.isDirectory()) {
            System.out.println("必须是文件");
            System.exit(0);
        }

        do_rm(file);
    }

    private void do_rm(File file) {
        String fileName = file.getName();
        Staged staged = Staged.readFromFile();
        Map<String, String> stagedForAdd = staged.getStagedForAdd();

        // 如果在 staged area就删除
        if (stagedForAdd.containsKey(fileName)) {
            stagedForAdd.remove(fileName);
        }

        // 如果在当前commit中，就给他放到staged for removal
        Commit currentCommit = Head.readFromFile().getCurrentCommit();
        if (currentCommit.getFileName2blobId().containsKey(fileName)) {
            staged.getStagedForRemoval().add(fileName);
        }

        if (file.exists()) {
            file.delete();
        }

        staged.save();
    }


    /**
     * status:
     */
    public void status() {
        showBranches();
        showStagedFilesForAdd();
        showStagedFilesForRemoval();
    }

    private void showBranches() {
        System.out.println("=== Branches ===");

        Head head = Head.readFromFile();
        String currentBranch = head.getBranchName();
        System.out.println("*" + currentBranch);

        String[] branchNames = Branch.listAllBranchNames();
        Arrays.sort(branchNames);
        for (String name : branchNames) {
            if (name.equals(currentBranch)) {
                continue;
            }
            System.out.println(name);
        }

        System.out.println();
    }

    private void showStagedFilesForAdd() {
        System.out.println("=== Staged Files ===");
        Staged staged = Staged.readFromFile();

        List<String> strings = staged.listAllStagedFileNamesForAdd();
        Collections.sort(strings);
        for (String s : strings) {
            System.out.println(s);
        }

        System.out.println();
    }

    private void showStagedFilesForRemoval() {
        System.out.println("=== Removal Files ===");
        Staged staged = Staged.readFromFile();

        List<String> strings = staged.listAllStagedFileNamesForRemoval();
        Collections.sort(strings);
        for (String s : strings) {
            System.out.println(s);
        }
        System.out.println();
    }


    private void showModificationsNotStagedForCommit() {
        System.out.println("=== Modifications Not Staged For Commit ===");

        System.out.println();
    }


    private void showUntrackedFiles() {
        System.out.println("=== Untracked Files ===");

        System.out.println();
    }


    public static void main(String[] args) {
        // 按字典序排序
        String[] strings = {"rea", "fads", "ad", "1dfa", "ac"};
        Arrays.sort(strings);
        System.out.println(Arrays.toString(strings));
    }
}
