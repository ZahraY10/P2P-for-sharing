import java.util.*;
import java.io.*;
import java.net.*;

public class UDPReceiver {
    private static String FILE_NAME; //= "C:\\Users\\Parham\\Desktop\\ReceivedFile.jpg";
    private static int PART_SIZE = 10240;

    public static void Receiver(String fileName) throws IOException, InterruptedException {
        FILE_NAME = fileName;


        System.out.println("Finding peers that have file...");
        DatagramSocket datagramSocket = new DatagramSocket(4001);
        datagramSocket.setBroadcast(true);
        byte[] message = fileName.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(message, message.length, InetAddress.getByName("255.255.255.255"), 5001);
        datagramSocket.send(datagramPacket);
        int port = 4001;
        System.out.println("Receiver port= " + port);
        datagramSocket.close();
        InetAddress address = null;
        Thread.sleep(1000);
        try {
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setSoTimeout(10000);
            byte[] input = new byte[128];
            datagramPacket = new DatagramPacket(input, input.length);
            datagramSocket.receive(datagramPacket);
            String command = new String(input, 0, input.length).trim();
            address = datagramPacket.getAddress();
            System.out.println("Peer that have it: " + address);
            datagramSocket.close();
        } catch (SocketException e) {
            System.out.println("Receiver Socket Closed!");
        } catch (SocketTimeoutException e) {
            System.out.println("Receiver time out!");
        }

        System.out.println("Requesting file...");
        datagramSocket = new DatagramSocket();
        byte[] message2 = "SEND!".getBytes();
        Thread.sleep(1000);
        datagramPacket = new DatagramPacket(message2, message2.length, address, 5001);
        datagramSocket.send(datagramPacket);
        datagramSocket.close();


        DatagramSocket receiveDatagramSocket = new DatagramSocket(5000);
        Vector<File> fileChunks = new Vector<File>(5, 5);
        byte[] buf;
        FileOutputStream fileOutputStream = null;
        int i = 0;
        while (true) {
            buf = new byte[10240];
            new File("C:\\Users\\Parham\\Desktop\\receiveTemp").mkdirs();
            File file = new File("C:\\Users\\Parham\\Desktop\\receiveTemp\\" + FILE_NAME + ".part" + i);
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            datagramPacket = new DatagramPacket(buf, buf.length);
            receiveDatagramSocket.receive(datagramPacket);
            boolean bad = true;
            {
                byte b;
                for(int j = 0; j < PART_SIZE; j++){
                    if('!' != datagramPacket.getData()[j]) {
                        bad = false;
                        break;
                    }
                }
            }
            if (bad) {
                System.out.println("File received successfully!");
                fileOutputStream.close();
                receiveDatagramSocket.close();
                break;
            }
            System.out.println("Package size is = " + datagramPacket.getLength());
            fileOutputStream.write(datagramPacket.getData(), 0, datagramPacket.getLength());
            fileOutputStream.flush();
            fileChunks.addElement(file);
            System.out.println("Received Packages = " + fileChunks.size());
            i++;
        }
        fileOutputStream.close();
        receiveDatagramSocket.close();
        MergeFile mergeFile = new MergeFile(fileChunks);
        new File("C:\\Users\\Parham\\Desktop\\output").mkdirs();
        File finalFile = mergeFile.merge("C:\\Users\\Parham\\Desktop\\output\\" + FILE_NAME);
        finalFile.createNewFile();
    }
}