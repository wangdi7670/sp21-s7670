package gitlet;

import java.io.File;
import java.util.*;

/**
 * @author: Wingd
 * @date: 2022/7/27 19:20
 */
public class DoWork {
    private Staged staged;

    private Head head;

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
        // initialize .gitlet
        Repository.initDir();

        // create
        Commit initCommit = new Commit();
        Branch branch = new Branch(initCommit.getCommitId(), "master");
        Head head = new Head(branch.getBranchName(), initCommit.getCommitId());
        Staged staged = new Staged();

        // persist
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

        // Staging an already-staged file overwrites the previous entry in the staging area with the new contents
        byte[] content = Utils.readContents(file);
        Blob blob = new Blob(fileName, content);
        blob.save();
        if (staged == null) {
            staged = Staged.readFromFile();
        }

        staged.putStagedForAdd(fileName, blob.getBlobId());

        if (head == null) {
            head = Head.readFromFile();
        }

        // If the current working version of the file is identical to the version in the current commit,
        // do not stage it to be added, and remove it from the staging area if it is already there
        Commit currentCommit = Commit.readFromFile(head.getCommitId());
        Map<String, String> fileName2blobId = currentCommit.getFileName2blobId();
        if (fileName2blobId.containsKey(fileName)) {
            if (fileName2blobId.get(fileName).equals(blob.getBlobId())) {
                staged.getStagedForAdd().remove(fileName);
                staged.getStagedForRemoval().remove(fileName);
            }
        }

        staged.save();
    }


    /**
     * commit : When we commit, only the HEAD and active branch move
     *
     * @param msg
     */
    public void commit(String msg) {
        checkInitialize();
        if (msg.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        // If no files have been staged,
        Staged staged = Staged.readFromFile();
        if (staged.getStagedForRemoval().isEmpty() && staged.getStagedForAdd().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        doCommit(msg);
    }

    private void doCommit(String msg) {
        // create new commit
        if (head == null) {
            head = Head.readFromFile();
        }
        Commit oldCommit = head.getCurrentCommit();
        Commit newCommit = new Commit(oldCommit.getCommitId(), msg);
        newCommit.setFileName2blobId(oldCommit.getFileName2blobId());

        // get staged area
        if (staged == null) {
            staged = Staged.readFromFile();
        }
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
        currentBranch.move(newCommit.getCommitId());
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

        // If the file is neither staged nor tracked by the head commit, print the error message
        head = head == null ? Head.readFromFile() : head;
        staged = staged == null ? Staged.readFromFile() : staged;
        Commit currentCommit = head.getCurrentCommit();

        if (!staged.containsFileInStagedForAdd(fileName) && !currentCommit.isTrackedFile(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        if (file.isDirectory()) {
            System.out.println("not a file");
            System.exit(0);
        }

        doRm(file);
    }

    private void doRm(File file) {
        String fileName = file.getName();
        if (staged == null) {
            staged = Staged.readFromFile();
        }
        Map<String, String> stagedForAdd = staged.getStagedForAdd();

        // Unstage the file if it is currently staged for addition
        if (stagedForAdd.containsKey(fileName)) {
            stagedForAdd.remove(fileName);
        }

        //  If the file is tracked in the current commit, stage it for removal
        Commit currentCommit = Head.readFromFile().getCurrentCommit();
        if (currentCommit.isTrackedFile(fileName)) {
            staged.getStagedForRemoval().add(fileName);
            if (file.exists()) {
                file.delete();
            }
        }

        staged.save();
    }

    /**
     *  log command:
     */
    public void log() {
        if (head == null) {
            head = Head.readFromFile();
        }

        Commit commit = head.getCurrentCommit();
        Commit temp = commit;

        while (temp != null) {
            System.out.println(temp);
            temp = temp.getParentCommit();
        }
    }


    /**
     * global-log command:
     */
    public void global_log() {
        List<String> list = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
        for (String s : list) {
            Commit commit = Commit.readFromFile(s);
            System.out.println(commit);
        }
    }

    /**
     * find command: Prints out the ids of all commits that have the given commit message, one per line
     */
    public void find(String commitMessage) {
        List<String> list = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
        boolean flag = false;
        for (String fileName : list) {
            Commit commit = Commit.readFromFile(fileName);
            if (commit.getMessage().equals(commitMessage)) {
                flag = true;
                System.out.println(commit.getCommitId());
            }
        }

        if (!flag) {
            System.out.println("Found no commit with that message");
        }
    }

    /**
     * status:
     */
    public void status() {
        checkInitialize();

        List<String> staged_files = new ArrayList<>();
        List<String> removed_files = new ArrayList<>();
        List<String> changesNotStaged = new ArrayList<>();
        List<String> untracked_files = new ArrayList<>();

        fill(staged_files, removed_files, changesNotStaged, untracked_files);

        showBranches();
        showStagedFilesForAdd(staged_files);
        showStagedFilesForRemoval(removed_files);
        showModificationsNotStagedForCommit(changesNotStaged);
        showUntrackedFiles(untracked_files);
    }

    private void showBranches() {
        System.out.println("=== Branches ===");

        if (head == null) {
            head = Head.readFromFile();
        }
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


    /**
     * used by 'status command'
     * @param staged_files: staged for add
     * @param removed_files: staged for removal
     * @param changesNotStaged:
     * @param untracked_files:
     */
    private void fill(List<String> staged_files, List<String> removed_files,
                      List<String> changesNotStaged, List<String> untracked_files) {
        if (staged == null) {
            staged = Staged.readFromFile();
        }
        if (head == null) {
            head = Head.readFromFile();
        }
        // staged area
        Map<String, String> stagedForAdd = staged.getStagedForAdd();
        Set<String> stagedForRemoval = staged.getStagedForRemoval();

        // tracked files by commit
        Commit currentCommit = head.getCurrentCommit();
        Map<String, String> fileName2blobId = currentCommit.getFileName2blobId();
        // working directory file names
        List<String> plainFilesInCWD = Utils.plainFilenamesIn(Repository.CWD);

        /*
        (1), (2), (3), (4) is situation of "modified but not staged"
         */

        // staged for add
        stagedForAdd.forEach((fileName, blobId) -> {
            staged_files.add(fileName);

            // (2) Staged for addition, but with different contents than in the working directory;
            if (Repository.fileInCwdIsExist(fileName) && Repository.fileInCWDisNonEqual(fileName, blobId)) {
                changesNotStaged.add(fileName + " (modified)");
            }
            // (3) Staged for addition, but deleted in the working directory
            if (!Repository.fileInCwdIsExist(fileName)) {
                changesNotStaged.add(fileName + " (deleted)");
            }
        });

        // staged for removal
        removed_files.addAll(stagedForRemoval);

        if (plainFilesInCWD == null) {
            return;
        }

        for (String plainFileName : plainFilesInCWD) {
            if (!stagedForAdd.containsKey(plainFileName) && !fileName2blobId.containsKey(plainFileName)) {
                untracked_files.add(plainFileName);
            }

            // (1) Tracked in the current commit, changed in the working directory, but not staged;
            if (fileName2blobId.containsKey(plainFileName)) {
                boolean b = Repository.fileInCWDisNonEqual(plainFileName, fileName2blobId.get(plainFileName));
                if (b && !stagedForAdd.containsKey(plainFileName)) {
                    changesNotStaged.add(plainFileName + " (modified)");
                }
            }
        }

        // (4) Not staged for removal, but tracked in the current commit and deleted from the working directory
        fileName2blobId.forEach((fileName, blobId) -> {
            if (!Repository.fileInCwdIsExist(fileName)) {
                if (!stagedForRemoval.contains(fileName)) {
                    changesNotStaged.add(fileName + " (deleted)");
                }
            }
        });


    }

    private void display(List<String> list) {
        Collections.sort(list);
        for (String s : list) {
            System.out.println(s);
        }
        System.out.println();
    }

    private void showStagedFilesForAdd(List<String> staged_files) {
        System.out.println("=== Staged Files ===");
        display(staged_files);
    }

    private void showStagedFilesForRemoval(List<String> removed_files) {
        System.out.println("=== Removed Files ===");
        display(removed_files);
    }

    private void showModificationsNotStagedForCommit(List<String> changesNotStaged) {
        System.out.println("=== Modifications Not Staged For Commit ===");
        display(changesNotStaged);
    }

    private void showUntrackedFiles(List<String> untracked_files) {
        System.out.println("=== Untracked Files ===");
        display(untracked_files);
    }

    /**
     * checkout:
     *  (1):
     *  (2):
     *  (3):  The staging area is cleared, unless the checked-out branch is the current branch
     * @param args
     */
    public void checkout(String... args) {
        // java gitlet.Main checkout -- [file name]
        if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands");
                return;
            }
            String fileName = args[2];
            head = head == null ? Head.readFromFile() : head;
            replaceByCommitId(head.getCommitId(), fileName);
        }
        // java gitlet.Main checkout [commit id] -- [file name]
        else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands");
                return;
            }
            String commitId = args[1];
            String fileName = args[3];
            replaceByCommitId(commitId, fileName);
        }
        // java gitlet.Main checkout [branch name]
        else if (args.length == 2) {
            String branchName = args[1];
            if (!Branch.isBranchExist(branchName)) {
                System.out.println("No such branch exists.");
                System.exit(0);
            }

            head = head == null ? Head.readFromFile() : head;
            if (head.getBranchName().equals(branchName)) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }

            if (!getUntrackedFiles().isEmpty()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }

            doSwitch(branchName);
        }
        else {
            System.out.println("wrong args");
        }
    }

    /**
     * used by checkout (1) (2)
     * @param commitId
     * @param fileName
     */
    private void replaceByCommitId(String commitId, String fileName) {
        Commit commit = null;
        // if commitId is an abbreviation
        if (commitId.length() < 40) {
            List<String> list = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
            assert list != null;
            for (String s : list) {
                if (s.startsWith(commitId)) {
                    commit = Commit.readFromFile(s);
                }
            }
        } else {
            commit = Commit.readFromFile(commitId);
        }

        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Map<String, String> fileName2blobId = commit.getFileName2blobId();
        if (!fileName2blobId.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        doReplace(fileName, fileName2blobId.get(fileName));
    }

    /**+
     * used by checkout (1) (2)
     * @param fileName
     * @param blobId
     */
    private void doReplace(String fileName, String blobId) {
        File cwdFile = Utils.join(Repository.CWD, fileName);
        Blob blob = Blob.readFromFile(blobId);
        assert blob != null;
        Utils.writeContents(cwdFile, blob.getContent());
    }

    /**
     * return untracked files in CWD
     * @return
     */
    private List<String> getUntrackedFiles() {
        staged = staged == null ? Staged.readFromFile() : staged;
        Map<String, String> stagedForAdd = staged.getStagedForAdd();

        head = head == null ? Head.readFromFile() : head;
        Commit currentCommit = head.getCurrentCommit();
        Map<String, String> fileName2blobId = currentCommit.getFileName2blobId();

        // file name in CWD
        List<String> plainFilesInCWD = Utils.plainFilenamesIn(Repository.CWD);

        List<String> untracked_files = new ArrayList<>();
        assert plainFilesInCWD != null;
        for (String plainFileName : plainFilesInCWD) {
            if (!stagedForAdd.containsKey(plainFileName) && !fileName2blobId.containsKey(plainFileName)) {
                untracked_files.add(plainFileName);
            }
        }

        return untracked_files;
    }

    /**
     * used by checkout (3)
     * @param branchName
     */
    private void doSwitch(String branchName) {
        staged.clearStaged();
        staged.save();

        Branch branch = Branch.readBranchFromFile(branchName);
        head = head == null ? Head.readFromFile() : head;
        Commit currentCommit = head.getCurrentCommit();

        Commit branch2commit = branch.getCommit();

        for (String file : branch2commit.listAllTrackedFiles()) {
            Repository.write2fileInCWD(file, branch2commit.getTrackedFileBlobId(file));
        }

        for (String fileInCurrentCommit : currentCommit.listAllTrackedFiles()) {
            if (!branch2commit.isTrackedFile(fileInCurrentCommit)) {
                Repository.deleteFileInCWD(fileInCurrentCommit);
            }
        }

        head = head == null ? Head.readFromFile() : head;
        head.move(branch.getBranchName(), branch.getCommitId());
        head.save();
    }

    /**
     * branch command
     * @param branchName
     */
    public void branch(String branchName) {
        if (Branch.isBranchExist(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }

        head = head == null ? Head.readFromFile() : head;
        Branch newBranch = new Branch(head.getCommitId(), branchName);
        newBranch.save();
    }


    /**
     * rm-branch command:
     * @param branchName:
     */
    public void rmBranch(String branchName) {
        if (!Branch.isBranchExist(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        head = head == null ? Head.readFromFile() : head;
        String currentBranchName = head.getBranchName();
        if (currentBranchName.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
        }

        doRmBranch(branchName);
    }

    private void doRmBranch(String branchName) {
        File file = Utils.join(Repository.BRANCHES, branchName);
        if (file.isFile()) {
            file.delete();
        }
    }


    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * @param commitId
     */
    public void reset(String commitId) {
        checkInitialize();
        Commit givenCommit = Commit.readFromFile(commitId);
        if (givenCommit == null) {
            System.out.println("No commit with that id exists");
            System.exit(0);
        }

        List<String> untrackedFiles = getUntrackedFiles();
        for (String untrackedFile : untrackedFiles) {
            if (givenCommit.isTrackedFile(untrackedFile)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        doReset(givenCommit);
    }

    private void doReset(Commit givenCommit) {
        head = head == null ? Head.readFromFile() : head;
        Commit currentCommit = head.getCurrentCommit();

        // Removes tracked files that are not present in that commit.
        for (String trackedFile : currentCommit.listAllTrackedFiles()) {
            if (!givenCommit.isTrackedFile(trackedFile)) {
                Repository.deleteFileInCWD(trackedFile);
            }
        }

        // givenCommit overwrite CWD
        for (String trackedFile : givenCommit.listAllTrackedFiles()) {
            Repository.write2fileInCWD(trackedFile, givenCommit.getTrackedFileBlobId(trackedFile));
        }

        staged = staged == null ? Staged.readFromFile() : staged;
        staged.clearStaged();

        head.move(head.getBranchName(), givenCommit.getCommitId());
        head.save();
    }


    /**
     *  merge:
     *  1. assume two branches at the front commit
     * @param branchName
     */
    public void merge(String branchName) {
        checkInitialize();

        staged = staged == null ? Staged.readFromFile() : staged;
        if (!staged.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        if (!Branch.isBranchExist(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        head = head == null ? Head.readFromFile() : head;
        if (head.getBranchName().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        Branch givenBranch = Branch.readBranchFromFile(branchName);
        assert givenBranch != null;
        Commit givenCommit = givenBranch.getCommit();

        head = head == null ? Head.readFromFile() : head;
        Branch currentBranch = head.getCurrentBranch();
        Commit currentCommit = currentBranch.getCommit();

        Commit splitPoint = getLatestCommonAncestor(currentCommit, givenCommit);


        // If an untracked file in the current commit would be overwritten or deleted by the merge
        Set<String> untrackedFiles = Repository.listAllUntrackedFiles(staged, currentCommit);
        for (String untrackedFile : untrackedFiles) {
            // (5) case in 8 cases
            if (givenCommit.isTrackedFile(untrackedFile) && !splitPoint.isTrackedFile(untrackedFile)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }

            // (3) case in 8 cases
            if (splitPoint.isTrackedFile(untrackedFile)
                    && !currentCommit.isTrackedFile(untrackedFile)
                    && !givenCommit.isTrackedFile(untrackedFile)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        //  If the split point is the same commit as the given branch
        if (Objects.equals(splitPoint, givenCommit)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        // If the split point is the current branch
        if (Objects.equals(splitPoint, currentCommit)) {
            currentBranch.move(givenBranch.getCommitId());
            currentBranch.save();
            head.move(currentBranch.getBranchName(), givenBranch.getCommitId());
            head.save();
            System.out.println("Current branch fast-forwarded.");
        }

        doMerge(splitPoint, currentCommit, givenCommit, currentBranch, givenBranch);
    }

    /**
     * get the latest common ancestor of two branches
     * @param currentCommit
     * @param givenCommit
     * @return
     */
    private Commit getLatestCommonAncestor(Commit currentCommit, Commit givenCommit) {
        Commit c1 = currentCommit, c2 = givenCommit;

        while (!Objects.equals(c1, c2)) {
            if (c1 == null) {
                c1 = givenCommit;
            } else {
                c1 = c1.getParentCommit();
            }

            if (c2 == null) {
                c2 = currentCommit;
            } else {
                c2 = c2.getParentCommit();
            }
        }

        return c1;
    }

    private void doMerge(Commit splitPoint, Commit currentCommit, Commit givenCommit,
                         Branch currentBranch, Branch givenBranch) {

        boolean isUpdated = false;  // whether file is updated
        boolean isConflict = false;

        String msg = "Merged " + givenBranch.getBranchName() + " into " + currentBranch.getBranchName() + ".";
        Commit mergeCommit = new Commit(currentCommit.getCommitId(), givenCommit.getCommitId(), msg);

        Set<String> allFileNames = new HashSet<>();
        allFileNames.addAll(splitPoint.listAllTrackedFiles());
        allFileNames.addAll(currentCommit.listAllTrackedFiles());
        allFileNames.addAll(givenCommit.listAllTrackedFiles());

        for (String fileName : allFileNames) {
            if (splitPoint.isTrackedFile(fileName)
                    && currentCommit.isTrackedFile(fileName)
                    && givenCommit.isTrackedFile(fileName)) {
                // (1)
                if (Commit.isHaveSameFileBlobId(splitPoint, currentCommit, fileName)
                        && !Commit.isHaveSameFileBlobId(splitPoint, givenCommit, fileName)) {
                    isUpdated = true;
                    String blobId = givenCommit.getTrackedFileBlobId(fileName);
                    Repository.write2fileInCWD(fileName, blobId);

                    staged.putStagedForAdd(fileName, blobId);
                    mergeCommit.trackFile(fileName, blobId);
                }
                // (2)
                else if (Commit.isHaveSameFileBlobId(splitPoint, givenCommit, fileName)
                        && !Commit.isHaveSameFileBlobId(splitPoint, currentCommit, fileName)) {
                    // do nothing
                    Commit.otherCommitExtends(currentCommit, mergeCommit, fileName);
                }
            }

            // (3)
            if (splitPoint.isTrackedFile(fileName)
                    && Commit.isHaveSameFileBlobId(currentCommit, givenCommit, fileName)) {
                // do nothing
                Commit.otherCommitExtends(currentCommit, mergeCommit, fileName);
            }
            // (4)
            if (!splitPoint.isTrackedFile(fileName)
                    && currentCommit.isTrackedFile(fileName)
                    && !givenCommit.isTrackedFile(fileName)) {
                // do nothing
                Commit.otherCommitExtends(currentCommit, mergeCommit, fileName);
            }

            // (5)
            if (!splitPoint.isTrackedFile(fileName)
                    && givenCommit.isTrackedFile(fileName)
                    && !currentCommit.isTrackedFile(fileName)) {

                isUpdated = true;
                String blobId = givenCommit.getTrackedFileBlobId(fileName);
                Repository.write2fileInCWD(fileName, blobId);

                staged.putStagedForAdd(fileName, blobId);
                mergeCommit.trackFile(fileName, blobId);
            }

            // (6)
            if (splitPoint.isTrackedFile(fileName)
                    && currentCommit.isTrackedFile(fileName)
                    && !givenCommit.isTrackedFile(fileName)) {

                isUpdated = true;
                Repository.deleteFileInCWD(fileName);
                staged.putStagedForRemoval(fileName);
            }

            // (7)
            if (splitPoint.isTrackedFile(fileName)
                    && !Commit.isHaveSameFileBlobId(splitPoint, givenCommit, fileName)
                    && !currentCommit.isTrackedFile(fileName)) {
                // do nothing
            }

            // (8) conflict
            if (currentCommit.isTrackedFile(fileName)
                    && givenCommit.isTrackedFile(fileName)
                    && !Commit.isHaveSameFileBlobId(currentCommit, givenCommit, fileName)) {
                isConflict = true;
                String givenBlobId = givenCommit.getTrackedFileBlobId(fileName);
                String currentBlobId = currentCommit.getTrackedFileBlobId(fileName);

                String newBlobId = Repository.dealConflictAndStaged(fileName, givenBlobId, currentBlobId, staged);
                mergeCommit.trackFile(fileName, newBlobId);
            }
        }

        if (isUpdated) {
            head.move(head.getBranchName(), mergeCommit.getCommitId());
            currentBranch.move(mergeCommit.getCommitId());

            mergeCommit.save();
            head.save();
            currentBranch.save();
            staged.clearStaged();
        }

        staged.save();

        if (isConflict) {
            System.out.println("Encountered a merge conflict.");
        }


    }
}
