import java.io.Serializable;

public class Pakiet implements Serializable {

    private String name;
    public Pakiet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
