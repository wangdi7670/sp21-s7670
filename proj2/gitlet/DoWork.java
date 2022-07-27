package gitlet;

/**
 * @author: Wingd
 * @date: 2022/7/27 19:20
 */
public class DoWork {

    public void init() {
        if (Repository.isInitialized()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        Repository.initDir();

        Commit initCommit = new Commit();
        Branch branch = new Branch(initCommit.getId(), "master");
        Head head = new Head(branch.getBranchName(), initCommit.getId());

        initCommit.save();
        branch.save();
        head.save();
    }
}
