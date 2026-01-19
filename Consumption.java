package projekPBO.projek;

public class Consumption extends Item {
    public Consumption(String name, int price) {
        super(name, price);
    }

    @Override
    public int getPrice() {
        return price;
    }
}
