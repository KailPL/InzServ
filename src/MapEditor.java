import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import javax.swing.ImageIcon;

public class MapEditor extends JFrame {

    private static final int SIZE = 256;
    private static final int TILE_SIZE = 32;
    private int[][] map;
    private int selectedTile = 1; // Domyślny obrazek z palety
    private int drawingMode = 0; // 0 - Tryb zmiany, 1 - Tryb resetowania

    public MapEditor() {
        map = new int[SIZE][SIZE];
        try (BufferedReader reader = new BufferedReader(new FileReader("map.txt"))) {
            String line;
            int i = 0;
            // Wczytywanie danych z pliku do tablicy
            while ((line = reader.readLine()) != null) {
                String[] elementy = line.split(" ");
                for (int j = 0; j < elementy.length; j++) {
                    map[i][j] = Integer.parseInt(elementy[j]);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Map Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Ustawienie maksymalizowanego stanu
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        int tileValue = map[i][j];
                        ImageIcon icon = getIconForTile(tileValue);
                        if (icon != null) {
                            icon.paintIcon(this, g, j * TILE_SIZE, i * TILE_SIZE);
                        }
                    }
                }
            }
        };

        JScrollPane scrollPane = new JScrollPane(mapPanel);
        mapPanel.setPreferredSize(new Dimension(SIZE * TILE_SIZE, SIZE * TILE_SIZE));

        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = e.getY() / TILE_SIZE;
                int col = e.getX() / TILE_SIZE;

                if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
                    // tryb zmieniania
                    if (drawingMode == 0) {
                        // Przykładowe zmiany wartości w tablicy po kliknięciu
                        if (map[row][col] < 16) {
                            map[row][col]++;
                            System.out.println("Zmieniono mapę[" + row + "][" + col + "] na " + map[row][col]);
                        } else {
                            map[row][col] = 0;
                            System.out.println("Zmieniono mapę[" + row + "][" + col + "] na " + map[row][col]);
                        }
                    } else if (drawingMode == 1) {
                        // Tryb rysowania
                        map[row][col] = selectedTile;
                        System.out.println("Zresetowano mapę[" + row + "][" + col + "]");
                    }

                    // Odświeżenie panelu z mapą
                    mapPanel.repaint();
                    mapPanel.revalidate();
                }
            }
        });

        JPanel palettePanel = new JPanel();
        palettePanel.setLayout(new BoxLayout(palettePanel, BoxLayout.Y_AXIS));

        // Dodanie ikon z palety
        for (int i = 1; i <= 16; i++) {
            final int tileNumber = i;
            ImageIcon paletteIcon = getIconForTile(i);
            JLabel paletteLabel = new JLabel(paletteIcon);
            paletteLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedTile = tileNumber;
                    System.out.println("Wybrano obrazek z palety: " + selectedTile);
                    drawingMode = 1; // Zmiana na tryb zmiany
                }
            });
            palettePanel.add(paletteLabel);
        }

        // Dodanie przycisku resetowania
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawingMode = 0; // Zmiana na tryb resetowania
                System.out.println("Zmieniono tryb na resetowanie");
            }
        });

        // Dodanie przycisku Save
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("map.txt"))) {
                    for (int i = 0; i < map.length; i++) {
                        for (int j = 0; j < map[i].length; j++) {
                            writer.write(map[i][j] + " ");
                        }
                        writer.newLine();
                    }
                } catch (IOException er) {
                    er.printStackTrace();
                }
            }
        });

        palettePanel.add(resetButton);
        palettePanel.add(saveButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(palettePanel, BorderLayout.EAST);
        add(mainPanel);
    }

    private ImageIcon getIconForTile(int tileValue) {
        String imageName = "img/" + tileValue + ".png";
        ImageIcon icon = new ImageIcon(imageName);
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            return icon;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MapEditor editor = new MapEditor();
            editor.setVisible(true);
        });
    }
}
