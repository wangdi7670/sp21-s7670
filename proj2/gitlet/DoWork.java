package gitlet;

import java.io.File;
import java.util.*;

/**
 * @author: Wingd
 * @date: 2022/7/27 19:20
 */
public class DoWork {

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
        if (!Repository.isInitialized()) {
            System.out.println("还未初始化呢~~");
            System.exit(0);
        }

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
        staged.save();

        // 加入的blob要是和当前commit中的版本一致，则从staged area中删除
        Head head = Head.readFromFile();
        Commit currentCommit = Commit.readFromFile(head.getCommitId());
        Map<String, String> fileName2blobId = currentCommit.getFileName2blobId();
        if (fileName2blobId.containsKey(fileName)) {
            if (fileName2blobId.get(fileName).equals(blob.getBlobId())) {
                staged.getStagedForAdd().remove(fileName);
                staged.save();
            }
        }
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
