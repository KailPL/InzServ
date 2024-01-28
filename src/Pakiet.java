import java.io.Serializable;

public class Pakiet implements Serializable {

    private String name;
    private int type;
    // 0 - logowanie
    // 1 - msg


    public Pakiet(int type, String name) {
        this.type = type;
        this.name = name;
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
