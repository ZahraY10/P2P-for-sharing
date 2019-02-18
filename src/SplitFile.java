import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SplitFile {
    private File inputFile = null;
    private static int PART_SIZE = 10240;

    public SplitFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public Vector<File> split(String fileName, int partSize) {
        FileInputStream inputStream;
        String newFileName;
        FileOutputStream filePart;
        int fileSize = (int) inputFile.length();
        int nChunks = 0, read = 0, readLength = partSize;
        byte[] byteChunkPart;
        Vector<File> fileChunks = new Vector<File>(5, 5);
        try {
            inputStream = new FileInputStream(inputFile);
            while (fileSize > 0) {
                if (fileSize <= PART_SIZE) {
                    readLength = fileSize;
                }
                byteChunkPart = new byte[readLength];
                read = inputStream.read(byteChunkPart, 0, readLength);
                fileSize -= read;
                assert (read == byteChunkPart.length);
                nChunks++;
                newFileName = fileName + ".part"
                        + Integer.toString(nChunks - 1);
                filePart = new FileOutputStream(new File(newFileName));
                filePart.write(byteChunkPart);
                File newChunk = new File(fileName + ".part"
                        + Integer.toString(nChunks - 1));
                fileChunks.addElement(newChunk);
                filePart.flush();
                filePart.close();
                byteChunkPart = null;
                filePart = null;
            }
            inputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return fileChunks;
    }
}