package projekPBO.projek;

public class Film extends Item {
    private int stock;

    public Film(String name, int price, int stock) {
        super(name, price);
        this.stock = stock;
    }

    public Film(String name, int price) {
        super(name, price);
    }

    @Override
    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return name + " (Rp" + price + ")";
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
