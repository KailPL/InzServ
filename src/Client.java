import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;

public class Client {
    private ObjectOutputStream  objectOutputStream;
    private ObjectInputStream  objectInputStream;
    private Socket socket;
    private String clientId;

    public Client(Socket socket, String clientId) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.clientId = clientId;
            Pakiet pakiet = new Pakiet(clientId);
            objectOutputStream.writeObject(pakiet);
            System.out.println("WYSYLAM ID");
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    public void sendMessage() {
        try {

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String mess = scanner.nextLine();
                Pakiet pakiet = new Pakiet(mess);
                objectOutputStream.writeObject(pakiet);

            }
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {

                    Pakiet pakiet = (Pakiet) objectInputStream.readObject();
                    System.out.println(pakiet.getName());
                } catch (IOException e) {
                    closeEverything(socket, objectInputStream, objectOutputStream);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("podaj id");
        Scanner scanner = new Scanner(System.in);
        String userId = scanner.nextLine();
        Socket socket = new Socket("localhost", 23456);
        Client client = new Client(socket, userId);
        client.listenForMessage();
        client.sendMessage();
    }
}
