package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static final File STAGING_AREA = join(GITLET_DIR, "staged");



    /**
     * 删除 .gitlet 目录及子目录，文件
     */
    public static void destroy() {
        delete(GITLET_DIR.getPath());
    }

    /**
     * 删除指定路径的所有文件
     * @param path
     */
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

    /**
     * 工作区下的某文件是否存在
     * @return
     */
    public static boolean fileInCwdIsExist(String fileName) {
        File file = join(Repository.CWD, fileName);
        return file.exists() && file.isFile();
    }

    /**
     * cwd下的某文件的 sha1-id 是否和 给定的blobID 不相等
     * @param fileName: cwd 下的文件名
     * @param blobId:
     * @return
     */
    public static boolean fileInCWDisNonEqual(String fileName, String blobId) {
        File file = join(Repository.CWD, fileName);
        return !Blob.computeFileId(file).equals(blobId);
    }

    /**
     * 向工作目录中的文件写入blobId对应的文件
     * @param fileName
     * @param blobId
     */
    public static void write2fileInCWD(String fileName, String blobId) {
        File file = Utils.join(CWD, fileName);
        Blob blob = Blob.readFromFile(blobId);
        assert blob != null;
        Utils.writeContents(file, blob.getContent());
    }

    /**
     * 删除 cwd 下的文件
     * @param fileName
     */
    public static void deleteFileInCWD(String fileName) {
        File file = Utils.join(CWD, fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * read blob content
     * @param blobId
     * @return
     */
    public static byte[] readBlobIdContent(String blobId) {
        Blob blob = Blob.readFromFile(blobId);
        if (blob == null) {
            return null;
        }

        return blob.getContent();
    }

    /**
     * merge content when encounter conflict, and stage the result
     * @param fileName
     * @param givenBlobId
     * @param currentBlobId
     * @return : newBlobId
     */
    public static String dealConflictAndStaged(String fileName, String givenBlobId, String currentBlobId, Staged staged) {
        byte[] givenContent = readBlobIdContent(givenBlobId);
        byte[] currentContent = readBlobIdContent(currentBlobId);
        String firstLine = "<<<<<<< HEAD\n";
        String middle = "=======\n";
        String end = ">>>>>>>";

        File file = join(CWD, fileName);
        Utils.writeContents(file, firstLine, currentContent, middle, givenContent, end);

        Blob newBlob = new Blob(fileName, Utils.readContents(file));
        staged.putStagedForAdd(fileName, newBlob.getBlobId());

        newBlob.save();

        return newBlob.getBlobId();
    }


    /**
     * return all untracked files
     * @param staged
     * @param currentCommit
     * @return
     */
    public static Set<String> listAllUntrackedFiles(Staged staged, Commit currentCommit) {
        Set<String> set = new HashSet<>();

        List<String> plainFilesInCWD = Utils.plainFilenamesIn(Repository.CWD);
        assert plainFilesInCWD != null;

        for (String fileName : plainFilesInCWD) {
            if (!currentCommit.isTrackedFile(fileName) && !staged.containsFileInStagedForAdd(fileName)) {
                set.add(fileName);
            }
        }

        return set;
    }


    public static void main(String[] args) {
    }
}
