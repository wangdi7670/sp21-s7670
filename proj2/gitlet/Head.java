package gitlet;

/**
 * @author: Wingd
 * @date: 2022/7/27 17:36
 *
 * The Head points to the active branch
 */
public class Head {
    private Branch activeBranch;

    public Branch getActiveBranch() {
        return activeBranch;
    }

    public void setActiveBranch(Branch activeBranch) {
        this.activeBranch = activeBranch;
    }
}
