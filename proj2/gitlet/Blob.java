package gitlet;

/**
 * @author: Wingd
 * @date: 2022/7/27 17:41
 */
public class Blob {
    private String id;

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
        id = Utils.sha1(fileName, content);
    }
}
