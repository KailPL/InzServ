import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();//lista wszystkioch polaczanych kliejtnów przez cient handler
    public static int[][] map;
    private static boolean Starting = true;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String clientId;
    private int clientX =100;
    private int clientY =100;

    public ClientHandler(int[][] map, Socket socket) {

        try {
            firstHandler(map);
            this.socket = socket;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Pakiet pakiet = (Pakiet) objectInputStream.readObject();
            this.clientId = pakiet.getName();
            clientHandlers.add(this);
            broadcastMessage("client o id dolaczycl:" + clientId);
            replyMessage();

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
                if(pakiet.getType()==0){
//                messageFromCLient = pakiet.getName();
                System.out.println("ERROR? logowanie od aktywnego kliejnta:"+clientId);
//                broadcastMessage(messageFromCLient);
                }
                if(pakiet.getType()==1){
                    messageFromCLient = pakiet.getName();
                    System.out.println("O1:"+messageFromCLient);
                    broadcastMessage(messageFromCLient);
                    replyMessage();
                }
                if(pakiet.getType()==2){

                    System.out.println("O2:"+clientId+" ruch:"+(clientX+pakiet.getPosx())+":"+(clientY+pakiet.getPosy()));


                    if (((clientX+pakiet.getPosx())>15)&&((clientX+pakiet.getPosx())<240)) clientX+= pakiet.getPosx();
                    if (((clientY+pakiet.getPosy())>15)&&((clientY+pakiet.getPosy())<240)) clientY+= pakiet.getPosy();
                    replyMessage();
                }
            } catch (IOException | ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
    }

    public void firstHandler(int[][] map){
        if (Starting){
            ClientHandler.map = map;
            Starting = false;
        }
    }
    public void broadcastMessage(String messageToSend) {
        Pakiet pakiet = new Pakiet(1, messageToSend);
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

    public void replyMessage() {
        Pakiet pakiet = new Pakiet(2, clientX, clientY);

            try {
                int[][] clientMap = new int[25][25];
                for (int x = 0; x < clientMap[0].length; x++) {
                    System.arraycopy(map[clientX + x - 12], clientY - 12, clientMap[x], 0, clientMap.length);
                }
                    objectOutputStream.writeObject(pakiet);
                    objectOutputStream.flush();
                pakiet = new Pakiet(3, clientMap);
                objectOutputStream.writeObject(pakiet);
                objectOutputStream.flush();

            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            }
        }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: client "+clientId +" disconencted" );
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
