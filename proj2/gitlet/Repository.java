package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 *
 *
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory.
     *
     *  .gitlet
     *      |--objects
     *          |--commits
     *          |--blobs
     *
     *      |--ref
     *          |--head
     *          |--branches
     *
     *      |--staging area
     *
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");

    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");

    public static final File REF_DIR = join(GITLET_DIR, "ref");

    public static final File HEAD_DIR = join(REF_DIR, "head");

    public static final File BRANCHES = join(REF_DIR, "branches");

    public static final File STAGING_AREA = join(GITLET_DIR, "branches");

    public static void main(String[] args) {
        destroy();
    }

    public static void destroy() {
        delete(GITLET_DIR.getPath());
    }

    public static void delete(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            String[] list = f.list();
            for (String s : list) {
                delete(path + "\\" + s);
            }
        }

        f.delete();
    }

    /**
     * gitlet是否初始化
     * @return
     */
    public static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

    public static void initDir() {
        mkdir(GITLET_DIR);
        mkdir(OBJECTS_DIR);
        mkdir(COMMITS_DIR);
        mkdir(BLOBS_DIR);
        mkdir(REF_DIR);
        mkdir(HEAD_DIR);
        mkdir(BRANCHES);
        mkdir(STAGING_AREA);
    }

    public static void mkdir(File file) {
        if (file.exists()) {
            throw new GitletException("文件已经存在");
        }
        file.mkdir();
    }
}
