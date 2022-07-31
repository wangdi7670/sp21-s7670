package gitlet;

// TODO: any imports you need here


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  do not consider subdirectories
 *
 *  When we commit, only the HEAD and active branch move
 *  @author TODO
 */
public class Commit implements Serializable, Dumpable {

    public static final File FOLDER = Repository.COMMITS_DIR;

    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** sha-1 id */
    private String commitId;

    /** The message of this Commit. */
    private String message;

    /** parent commit represented by sha1-id */
    private String parent;

    /** for merge commit */
    private String secondParent;



    /** date when created */
    private String timeStamp;

    private Map<String, String> fileName2blobId;

    /**
     * initial commit
     */
    public Commit() {
        this("null", "null", "Wed Dec 31 16:00:00 1969 -0800", "initial commit");
    }

    public Commit(String parent, String message) {
        this(parent, "null", dateToTimeStamp(new Date()), message);
    }

    public Commit(String parent, String secondParent, String message) {
        this(parent, secondParent, dateToTimeStamp(new Date()), message);
    }


    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    public Commit(String parent, String secondParent, String timeStamp, String message) {
        this.parent = parent;
        this.timeStamp = timeStamp;
        this.message = message;
        this.secondParent = secondParent;
        fileName2blobId = new HashMap<>();
        setId();
    }

    /**
     * persist, file name is id
     */
    public void save() {
        File file = Utils.join(FOLDER, commitId);
        Utils.writeObject(file, this);
    }

    /**
     * read from file by given commitId
     * @param commitId
     * @return
     */
    public static Commit readFromFile(String commitId) {
        File file = Utils.join(FOLDER, commitId);
        if (!file.exists()) {
            return null;
        }
        Commit commit = Utils.readObject(file, Commit.class);
        return commit;
    }

    /**
     * return parentCommit
     * @return
     */
    public Commit getParentCommit() {
        if (parent.equals("null")) {
            return null;
        }
        return readFromFile(parent);
    }

    /**
     * whether this commit tracks given file
     * @param fileName
     * @return
     */
    public boolean isTrackedFile(String fileName) {
        return fileName2blobId.containsKey(fileName);
    }

    /**
     * return the blobId to which specified fileName is mapped in this commit,
     * or null if this commit not tracks the fileName
     * @param fileName:
     * @return :
     */
    public String getTrackedFileBlobId(String fileName) {
        return fileName2blobId.get(fileName);
    }

    /**
     * return all tracked files
     * @return
     */
    public Set<String> listAllTrackedFiles() {
        return fileName2blobId.keySet();
    }

    private void setId() {
        commitId = Utils.sha1(parent, secondParent, timeStamp, message);
    }

    public String getCommitId() {
        return commitId;
    }


    public Map<String, String> getFileName2blobId() {
        return fileName2blobId;
    }

    public void setFileName2blobId(Map<String, String> fileName2blobId) {
        this.fileName2blobId = fileName2blobId;
    }


    /**
     * whether blobId of file tracked by commit1 and commit2 is equal
     * if both not tracked, return also true
     * if one  tracks, another not tracks, return false
     * @param commit1
     * @param commit2
     * @param fileName
     * @return
     */
    public static boolean isHaveSameFileBlobId(Commit commit1, Commit commit2, String fileName) {
        String blobId1 = commit1.getTrackedFileBlobId(fileName);
        String blobId2 = commit2.getTrackedFileBlobId(fileName);
        return Objects.equals(blobId1, blobId2);
    }


    /**
     * get this commit tracking some file whose version is blobId
     * @param fileName
     * @param blobId
     */
    public void trackFile(String fileName, String blobId) {
        fileName2blobId.put(fileName, blobId);
    }


    /**
     * child commit tracks some file which is tracked by parent commit, unless parent commit does not track fileName
     * @param parent
     * @param child
     * @param fileName
     */
    public static void otherCommitExtends(Commit parent, Commit child, String fileName) {
        String parentFileBlobId = parent.getTrackedFileBlobId(fileName);
        if (parentFileBlobId == null) {
            return;
        }
        child.trackFile(fileName, parentFileBlobId);
    }


    @Override
    public String toString() {
        if (!secondParent.equals("null")) {
            return "==\n" +
                    "commit " + commitId + "\n" +
                    "Merge: " + parent.substring(0, 7) + " " + secondParent.substring(0, 7) + "\n" +
                    "Date: " + timeStamp + "\n" +
                    "Merged development into master.\n";
        }
        return "==\n" +
                "commit " + commitId + "\n" +
                "Date: " + timeStamp + "\n" +
                message + "\n";
    }

    @Override
    public void dump() {
        System.out.println("msg: " + message + "\n"
        + "date: " + timeStamp + "\n"
        + "parent: " + parent + "\n");

        System.out.println("tracked files: ");
        for (String s : fileName2blobId.keySet()) {
            System.out.println(s);
        }
        System.out.println();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return Objects.equals(commitId, commit.commitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commitId);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(Date date) {
        this.timeStamp = dateToTimeStamp(date);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


}
