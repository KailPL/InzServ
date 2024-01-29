import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;
import java.awt.event.ActionListener;

public class Client {
    private ObjectOutputStream  objectOutputStream;
    private ObjectInputStream  objectInputStream;
    private Socket socket;
    private String clientId;
    public static JTextArea obszarTekstowy;
    public static JLabel miejsceNaGrafike;
    public static int[][] mapa= new int[25][25];
    public Client(Socket socket, String clientId) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.clientId = clientId;
            Pakiet pakiet = new Pakiet(0, clientId);
            objectOutputStream.writeObject(pakiet);
            System.out.println("WYSYLAM ID");
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    public void sendMessage(String mess) {
        try {
                Pakiet pakiet = new Pakiet(1, mess);
                objectOutputStream.writeObject(pakiet);


        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }
    public void move(int x, int y) {
        try {
            Pakiet pakiet = new Pakiet(2, x, y);
            objectOutputStream.writeObject(pakiet);
            System.out.println("wyslano ruch:"+x+":"+y);
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    Pakiet pakiet = (Pakiet) objectInputStream.readObject();
                    if (pakiet.getType()==1) {
                        System.out.println(pakiet.getName());
                        // Wywołanie metody obsługującej otrzymane dane w wątku zdarzeń AWT/Swing
                        SwingUtilities.invokeLater(() -> {
                            obszarTekstowy.append(pakiet.getName() + "\n");
                        });
                    }
                    if (pakiet.getType()==2){

                        System.out.println("Dostalem kordy x:"+pakiet.getPosx()+"oraz y:"+pakiet.getPosy());
                    }
                    if (pakiet.getType()==3){

                        System.out.println("Dostalem MAPE!");
                        mapa = pakiet.getWorld();
                        SwingUtilities.invokeLater(() -> {
                            miejsceNaGrafike.removeAll();
                            for (int i = 0; i < mapa.length; i++) {
                                for (int j = 0; j < mapa[i].length; j++) {
                                    int numerObrazka = mapa[i][j];  // Pobranie wartości z mapy
                                    String nazwaObrazka = "img/" + numerObrazka + ".png";
                                    ImageIcon obrazek = new ImageIcon(nazwaObrazka);
                                    JLabel label = new JLabel(obrazek);
                                    if (i == 12 && j == 12) {
                                        // Jeśli to pozycja (12, 12), dodaj drugi obrazek
                                        ImageIcon drugiObrazek = new ImageIcon("img/mid.png");
                                        JLabel drugiLabel = new JLabel(drugiObrazek);
                                        drugiLabel.setBounds(j * 32, i * 32, 32, 32);
                                        miejsceNaGrafike.add(drugiLabel);
                                    }
                                    label.setBounds(j * 32, i * 32, 32, 32);  // Zakładam, że obrazki mają wymiary 25x25
                                    miejsceNaGrafike.add(label);
                                    miejsceNaGrafike.revalidate();
                                    miejsceNaGrafike.repaint();

                                }
                            }
                        });
                    }
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


        // Tworzenie okna
        JFrame ramka = new JFrame("Client");

        JTextField poleTekstowe = new JTextField(63); // 20 to szerokość pola w znakach

        // Tworzenie przycisku
        JButton przycisk = new JButton("Wyslij");

        obszarTekstowy = new JTextArea(4, 1);

        // Ustawienie obszaru tekstowego jako niemodyfikowalnego
        obszarTekstowy.setEditable(false);

        // Dodanie miejsca na wyświetlanie grafiki
        miejsceNaGrafike = new JLabel();
        miejsceNaGrafike.setPreferredSize(new Dimension(800, 800));

//        for (int i = 0; i < mapa.length; i++) {
//            for (int j = 0; j < mapa[i].length; j++) {
//                mapa[i][j]= ((j+j)%3)+1;  // Pobranie wartości z mapy
//            }
//        }

        ImageIcon obrazek = new ImageIcon("img/mid.png");
        JLabel label = new JLabel(obrazek);
        label.setBounds(12 * 32, 12 * 32, 32, 32);  // Zakładam, że obrazki mają wymiary 25x25
        miejsceNaGrafike.add(label);
        // Wypełnianie paneluMapy obrazkami na podstawie wartości w mapie
        for (int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                int numerObrazka = mapa[i][j];  // Pobranie wartości z mapy
                String nazwaObrazka = "img/" + numerObrazka + ".png";
                 obrazek = new ImageIcon(nazwaObrazka);
                 label = new JLabel(obrazek);
                label.setBounds(i * 32, j * 32, 32, 32);  // Zakładam, że obrazki mają wymiary 25x25
                miejsceNaGrafike.add(label);
            }
        }

        // Dodanie obszaru tekstowego do JScrollPane, aby umożliwić przewijanie
        JScrollPane scrollPane = new JScrollPane(obszarTekstowy);
        ActionListener akcjaPrzycisk = e -> {
            String wpisanyTekst = poleTekstowe.getText();
            // Dodanie wpisanego tekstu do obszaru tekstowego
            obszarTekstowy.append(userId+":" + wpisanyTekst + "\n");
            client.sendMessage(userId+":"+wpisanyTekst);
            // Czyszczenie pola tekstowego
            poleTekstowe.setText("");
            ramka.setFocusable(true);
            ramka.requestFocusInWindow();
        };
        // Dodanie ActionListenera do przycisku
        przycisk.addActionListener(akcjaPrzycisk);

        // Dodanie ActionListenera do pola tekstowego (reaguje na Enter)
        poleTekstowe.addActionListener(akcjaPrzycisk);

        JPanel panelObszaruTekstowego = new JPanel(new BorderLayout());
        panelObszaruTekstowego.add(scrollPane, BorderLayout.SOUTH);

        JPanel panelPolaIPrzycisku = new JPanel();
        panelPolaIPrzycisku.add(poleTekstowe);
        panelPolaIPrzycisku.add(przycisk);

        JPanel panelGlowny = new JPanel(new BorderLayout());
        panelGlowny.add(miejsceNaGrafike, BorderLayout.NORTH);
        panelGlowny.add(panelPolaIPrzycisku, BorderLayout.SOUTH);
        panelGlowny.add(panelObszaruTekstowego, BorderLayout.CENTER);
        ramka.getContentPane().add(panelGlowny);



        ramka.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                int key = e.getKeyCode();
                System.out.println("RUSZ");
                switch (key) {
                    case KeyEvent.VK_UP:
                        client.move(-1, 0);  // Przesunięcie w górę
                        break;
                    case KeyEvent.VK_DOWN:
                        client.move(1, 0);  // Przesunięcie w dół
                        break;
                    case KeyEvent.VK_LEFT:
                        client.move(0, -1);  // Przesunięcie w lewo
                        break;
                    case KeyEvent.VK_RIGHT:
                        client.move(0, 1);  // Przesunięcie w prawo
                        break;
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                System.out.println("RUSZ");
                switch (key) {
                    case KeyEvent.VK_UP:
                        client.move(-1, 0);  // Przesunięcie w górę
                        break;
                    case KeyEvent.VK_DOWN:
                        client.move(1, 0);  // Przesunięcie w dół
                        break;
                    case KeyEvent.VK_LEFT:
                        client.move(0, -1);  // Przesunięcie w lewo
                        break;
                    case KeyEvent.VK_RIGHT:
                        client.move(0, 1);  // Przesunięcie w prawo
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
//                int key = e.getKeyCode();
//                System.out.println("RUSZ");
//                switch (key) {
//                    case KeyEvent.VK_UP:
//                        client.move(0, -1);  // Przesunięcie w górę
//                        break;
//                    case KeyEvent.VK_DOWN:
//                        client.move(0, 1);  // Przesunięcie w dół
//                        break;
//                    case KeyEvent.VK_LEFT:
//                        client.move(-1, 0);  // Przesunięcie w lewo
//                        break;
//                    case KeyEvent.VK_RIGHT:
//                        client.move(1, 0);  // Przesunięcie w prawo
//                        break;
//                }
            }
        });

        // Konfiguracja ramki
        ramka.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ramka.setSize( 	820, 945); // Ustawienie rozmiaru
        ramka.setLocationRelativeTo(null); // Wyśrodkowanie ramki
        ramka.setVisible(true); // Ustawienie widoczności

         ramka.setFocusable(true);
        ramka.requestFocusInWindow();

        client.listenForMessage();
        scanner.nextLine();
    }
}

