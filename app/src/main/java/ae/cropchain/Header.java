package ae.cropchain;

public class Header {
    long Id;
    String Name;

    public Header() {
    }

    public Header(long id, String name) {
        Id = id;
        Name = name;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
