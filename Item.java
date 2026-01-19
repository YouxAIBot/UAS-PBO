package projekPBO.projek;

public abstract class Item {
    protected String name;
    protected int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public abstract int getPrice();

    public String getName() {
        return name;
    }
}
