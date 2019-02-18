import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * Created by Parham on 28-Dec-18.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, SocketTimeoutException {
        String inputString, fileName, path;
        Scanner scanner = new Scanner(System.in);
        String[] splitString;
        boolean stay = true;
        while (stay) {
            inputString = scanner.nextLine();
            splitString = inputString.split(" ");
            switch (splitString[0]) {
                case ("p2p"):
                    switch (splitString[1]) {
                        case "-receive":
                            fileName = splitString[2];
                            UDPReceiver.Receiver(fileName);
                            break;
                        case "-serve":
                            switch (splitString[2]) {
                                case "-name":
                                    switch (splitString[4]) {
                                        case "-path":
                                            fileName = splitString[3];
                                            path = splitString[5];
                                            UDPSender.input(fileName, path);
                                            break;
                                        default:
                                            System.out.println("Wrong input!");
                                            break;
                                    }
                                    break;
                                case "START":
                                    UDPSender.Sender();
                                    break;
                                default:
                                    System.out.println("Wrong input!");
                                    break;
                            }
                            break;
                        default:
                            System.out.println("Wrong input!");
                            break;
                    }
                    break;
                case "END":
                    stay = false;
                    break;
                default:
                    System.out.println("Wrong input!");
                    break;
            }
        }
    }
}
