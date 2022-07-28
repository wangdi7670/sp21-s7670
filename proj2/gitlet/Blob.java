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

    public void setId() {
        if (fileName == null || content == null) {
            return;
        }
        blobId = Utils.sha1(fileName, content);
    }

    /**
     * 根据文件名和文件内容计算文件的 sha-1 ID
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
     * 持久化，文件名就是自己的 blobId
     */
    public void save() {
        File f = Utils.join(Repository.BLOBS_DIR, blobId);
        Utils.writeObject(f, this);
    }

    @Override
    public void dump() {
        System.out.println("file: " + fileName);
        System.out.println(new String(content));
    }
}
