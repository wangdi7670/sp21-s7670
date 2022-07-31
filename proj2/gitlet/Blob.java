package gitlet;

import java.io.File;
import java.io.Serializable;

/**
 * @author: Wingd
 * @date: 2022/7/27 17:41
 */
public class Blob implements Serializable, Dumpable {
    private String blobId;

    private String fileName;

    private byte[] content;

    public Blob(String fileName, byte[] content) {
        this.fileName = fileName;
        this.content = content;
        setId();
    }

    public byte[] getContent() {
        return content;
    }

    public void setId() {
        if (fileName == null || content == null) {
            return;
        }
        blobId = Utils.sha1(fileName, content);
    }

    /**
     * compute the shai-id of specified file by file name and its content
     * @param file
     * @return
     */
    public static String computeFileId(File file) {
        String fName = file.getName();
        byte[] fContent = Utils.readContents(file);
        return Utils.sha1(fName, fContent);
    }

    public String getBlobId() {
        return blobId;
    }

    /**
     * persist a file whose file name is its blobId
     */
    public void save() {
        File f = Utils.join(Repository.BLOBS_DIR, blobId);
        Utils.writeObject(f, this);
    }

    /**
     * return Blob object by the given blobId, or null if not exist
     * @param blobId
     * @return
     */
    public static Blob readFromFile(String blobId) {
        File file = Utils.join(Repository.BLOBS_DIR, blobId);
        if (!file.exists()) {
            return null;
        }

        Blob blob = Utils.readObject(file, Blob.class);
        return blob;
    }

    @Override
    public void dump() {
        System.out.println("file: " + fileName);
        System.out.println(new String(content));
    }
}
