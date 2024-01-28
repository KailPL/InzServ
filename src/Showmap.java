import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
public class Showmap extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Generator gen = new Generator();
        int[][] intArray = gen.map();

        int width = intArray[0].length;
        int height = intArray.length;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        PixelWriter pw = gc.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int colorValue = intArray[y][x];
                pw.setArgb(x, y, generateColor(colorValue));
            }
        }

        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle("IntArray to Image");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int[][] generateIntArray() {
        // Przykładowa implementacja generowania tablicy 2D intów
        int width = 800;
        int height = 600;
        int[][] intArray = new int[height][width];

        // Wypełnianie tablicy intów (przykład losowych wartości)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                intArray[y][x] = (int) (Math.random() * 256); // Losowe wartości od 0 do 255
            }
        }

        return intArray;
    }

    private int generateColor(int value) {
        // Przykładowa implementacja generowania koloru na podstawie wartości int
        // Tutaj możesz dostosować generowanie koloru w zależności od wartości w tablicy intów
        value=value+500;
        value=value/5;

        return 0xFF000000 | (value << 16) | (value << 8) | value;



    }

}
