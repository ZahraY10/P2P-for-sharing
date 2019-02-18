import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class MergeFile {
    private Vector<File> fileChunks;

    public MergeFile(Vector<File> fileChunks) {
        this.fileChunks = fileChunks;
    }

    public File merge(String fileName) {
        File oFile = new File(fileName);
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        byte[] fileBytes;
        int bytesRead = 0;
        try {
            fileOutputStream = new FileOutputStream(oFile, true);
            for (File file : fileChunks) {
                fileInputStream = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fileInputStream.read(fileBytes, 0, (int) file.length());
                assert (bytesRead == fileBytes.length);
                assert (bytesRead == (int) file.length());
                fileOutputStream.write(fileBytes);
                fileOutputStream.flush();
                fileBytes = null;
                fileInputStream.close();
                fileInputStream = null;
            }
            fileOutputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return oFile;
    }
}