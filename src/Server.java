import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private static int[][] map;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Server: new thread started");
                ClientHandler clientHandler = new ClientHandler(map, socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("generuje mape");
        Generator gen = new Generator();
        map = gen.map();
        // Wypełnianie tablicy intów (przykład losowych wartości)
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                map[y][x] = (map[y][x]+600)/100;
            }
        }
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
               // map[y][x] = (map[y][x]+600)/100;
            }
        }
        int min,max;
        min=max=map[0][0];
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                System.out.print(map[y][x] + " ");
                if(map[y][x]<min)min=map[y][x];
                if(map[y][x]>max)max=map[y][x];
            }
            System.out.println();
        }
        System.out.println("MIN="+min+"MAX="+max);
        System.out.println("wstaje server");
        ServerSocket serverSocket = new ServerSocket(23456);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
