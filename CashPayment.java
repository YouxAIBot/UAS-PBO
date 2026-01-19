package projekPBO.projek;

public class CashPayment implements PaymentMethod {
    @Override
    public boolean pay(int total, int uang) {
        return uang >= total;
    }
}
