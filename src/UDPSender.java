import java.util.*;
import java.io.*;
import java.net.*;

public class UDPSender {
    private static String FILE_PATH; //= "D:\\Documents\\Polytechnic\\Term 5\\Computer Networks\\Projects\\project1b\\tmp\\RedFish.jpg";
    private static String FILE_NAME; //"D:\\Documents\\Polytechnic\\Term 5\\Computer Networks\\Projects\\project1b\\RedFish.jpg"
    private static int PART_SIZE = 10240;
    private static HashMap<String, String> availableFiles = new HashMap<>();

    public static void input(String fileName, String filePath) {
        if (!availableFiles.containsKey(fileName))
            availableFiles.put(fileName, filePath);
    }

    public static void Sender() throws IOException, InterruptedException {
        while (true) {
            DatagramSocket datagramSocket = new DatagramSocket(5001);
            byte[] requestedFile = new byte[128];
            DatagramPacket datagramPacket = new DatagramPacket(requestedFile, requestedFile.length);
            datagramSocket.receive(datagramPacket);
            InetAddress inetAddress = datagramPacket.getAddress();
            int port = datagramPacket.getPort();
            String requestedFileName = new String(requestedFile, 0, requestedFile.length).trim();
            System.out.println("Requested file name: " + requestedFileName);
            datagramSocket.close();


            if (availableFiles.containsKey(requestedFileName)) {
                System.out.println("Requested file is Available");
                datagramSocket = new DatagramSocket();
                byte[] message = "Available!".getBytes();
                datagramPacket = new DatagramPacket(message, message.length, inetAddress, port);
                System.out.println("inetAddress.getHostName()= " + inetAddress.getHostName() +
                        "\ninetAddress.getHostAddress()= " + inetAddress.getHostAddress() + "\nPort= " + port);
                Thread.sleep(1000);
                datagramSocket.send(datagramPacket);
                datagramSocket.close();


                String command = "";
                InetAddress inetAddress2 = null;
                try {
                    datagramSocket = new DatagramSocket(5001);
                    datagramSocket.setSoTimeout(10000);
                    byte[] input = new byte[128];
                    datagramPacket = new DatagramPacket(input, input.length);
                    datagramSocket.receive(datagramPacket);
                    inetAddress2 = datagramPacket.getAddress();
                    command = new String(input, 0, input.length).trim();
                    System.out.println("Command: " + command);
                    datagramSocket.close();
                } catch (SocketException e) {
                    System.out.println("Sender Socket Closed!");
                } catch (SocketTimeoutException e) {
                    System.out.println("Sender time out!");
                }

                if ("SEND!".equals(command)) {
                    Thread.sleep(1000);
                    FILE_NAME = requestedFileName;
                    FILE_PATH = availableFiles.get(requestedFileName);
                    DatagramSocket sendDatagramSocket = new DatagramSocket();
                    new File(FILE_PATH.substring(0, FILE_PATH.length() - FILE_NAME.length()) + "\\tmp").mkdirs();
                    File file = new File(FILE_PATH);
                    SplitFile splitFile = new SplitFile(file);
                    Vector<File> fileChunks = splitFile.split(FILE_PATH.substring(0, FILE_PATH.length() - FILE_NAME.length()) + "\\tmp\\" + FILE_NAME, PART_SIZE);
                    System.out.println("Size is = " + fileChunks.size());
                    int i = 0;
                    FileInputStream inputStream = null;
                    while (i < fileChunks.size()) {
                        File inputFile = new File(FILE_PATH.substring(0, FILE_PATH.length() - FILE_NAME.length()) + "\\tmp\\" + FILE_NAME + ".part" + i);
                        inputStream = new FileInputStream(inputFile);
                        byte[] byteChunkPart = new byte[PART_SIZE];
                        datagramPacket = new DatagramPacket(byteChunkPart, byteChunkPart.length, inetAddress2, 5000);
                        while ((inputStream.read(byteChunkPart)) != -1) {
                            sendDatagramSocket.send(datagramPacket);
                            Thread.sleep(100);
                        }
                        i++;
                    }
                    byte[] eof = new byte[PART_SIZE];
                    for (i = 0; i < PART_SIZE; i++)
                        eof[i] = '!';
                    datagramPacket = new DatagramPacket(eof, eof.length, inetAddress2, 5000);
                    sendDatagramSocket.send(datagramPacket);
                    System.out.println("File sent successfully!");
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    sendDatagramSocket.close();
                }
            }
        }
    }
}