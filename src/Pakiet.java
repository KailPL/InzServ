import java.io.Serializable;

public class Pakiet implements Serializable {

    private String name;
    private int type;
    // 0 - logowanie
    // 1 - msg
    // 2 - pos
    // 3 - clientmap
    private int posx;
    private int posy;
    private int[][] world;



    public Pakiet(int type, String name) {
        this.type = type;
        this.name = name;
    }
    public Pakiet(int type, int posx, int posy) {
        this.type = type;
        this.posx = posx;
        this.posy = posy;
    }
    public Pakiet(int type, int[][] world) {
        this.type = type;
        this.world = world;

    }

    public int[][] getWorld() {
        return world;
    }

    public int getPosx() {
        return posx;
    }

    public int getPosy() {
        return posy;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
