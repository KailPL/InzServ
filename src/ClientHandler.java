import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();//lista wszystkioch polaczanych kliejtnów przez cient handler
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String clientId;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Pakiet pakiet = (Pakiet) objectInputStream.readObject();
            this.clientId = pakiet.getName();
            clientHandlers.add(this);
            broadcastMessage("client o id dolaczycl:" + clientId);
        } catch (IOException | ClassNotFoundException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    @Override
    public void run() {
        Pakiet pakiet;
        String messageFromCLient;
        while (socket.isConnected()) {
            try {
                pakiet = (Pakiet) objectInputStream.readObject();
                messageFromCLient = pakiet.getName();
                System.out.println("odebrano:"+messageFromCLient);
                broadcastMessage(messageFromCLient);
            } catch (IOException | ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        Pakiet pakiet = new Pakiet(messageToSend);
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientId.equals(clientId)) {
                    clientHandler.objectOutputStream.writeObject(pakiet);
                    clientHandler.objectOutputStream.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("serv: client id disconencted;" + clientId);
    }

    public void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        removeClientHandler();
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
}
