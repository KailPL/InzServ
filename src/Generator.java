import java.util.Random;

public class Generator {
    private int permutation[] = new int[512];

    public Generator() {
        Random rand = new Random();

        for (int i = 0; i < 256; i++) {
            permutation[i] = i;
        }

        for (int i = 0; i < 256; i++) {
            int index = rand.nextInt(256 - i) + i;
            int temp = permutation[i];
            permutation[i] = permutation[index];
            permutation[index] = temp;
            permutation[i + 256] = permutation[i];
        }
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x) {
        int h = hash & 15;
        double grad = 1 + (h & 7);
        if ((h & 8) != 0) grad = -grad;
        return (grad * x);
    }

    public void generateNoise(int width, int height, int[][] noiseArray, double scale, int octaves, double persistence) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double amplitude = 1;
                double frequency = 1;
                double noiseValue = 0;

                for (int i = 0; i < octaves; i++) {
                    double sampleX = x / scale * frequency;
                    double sampleY = y / scale * frequency;

                    double interpolatedValue = noise(sampleX, sampleY);
                    noiseValue += interpolatedValue * amplitude;

                    amplitude *= persistence;
                    frequency *= 2;
                }

                // Normalize the result to the range [0, 255]
                noiseValue = (noiseValue + 1) / 2.0; // Map to [0, 1]
                noiseValue *= 255; // Scale to [0, 255]

                // Store the result in the array
                noiseArray[y][x] = (int) noiseValue;
            }
        }
    }

    private double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        x -= Math.floor(x);
        y -= Math.floor(y);
        double u = fade(x);
        double v = fade(y);
        int A = permutation[X] + Y;
        int AA = permutation[A];
        int AB = permutation[A + 1];
        int B = permutation[X + 1] + Y;
        int BA = permutation[B];
        int BB = permutation[B + 1];
        return lerp(v, lerp(u, grad(permutation[AA], x, y), grad(permutation[BA], x - 1, y)),
                lerp(u, grad(permutation[AB], x, y - 1), grad(permutation[BB], x - 1, y - 1))) * 2;
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 7;
        double grad = 1 + (h & 3);
        if ((h & 4) != 0) grad = -grad;
        return (grad * x + grad * y);
    }
    public int[][]map(){
        int width = 256;
        int height = 256;
        int[][] noiseArray = new int[height][width];
        Generator noiseGen = new Generator();
        noiseGen.generateNoise(width, height, noiseArray, 55, 200, 0.3);
        return noiseArray;
    }



    public static void main(String[] args) {
        int width = 256;
        int height = 256;
        int[][] noiseArray = new int[height][width];

        Generator noiseGen = new Generator();
        noiseGen.generateNoise(width, height, noiseArray, 55, 200, 0.3);

        int min =0;
        int max =0;
        // Print the generated noise array
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(noiseArray[y][x] + " ");
                if(noiseArray[y][x]<min)min=noiseArray[y][x];
                if(noiseArray[y][x]>max)max=noiseArray[y][x];
            }
            System.out.println();
        }
        System.out.println("MIN="+min+"MAX="+max);
    }
}
