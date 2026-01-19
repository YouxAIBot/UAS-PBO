package projekPBO.projek;

public abstract class DigitalPayment implements PaymentMethod {
    protected String methodName;

    public DigitalPayment(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean pay(int total, int uang) {
        return uang == total;
    }
}

class GopayPayment extends DigitalPayment {
    public GopayPayment() {
        super("GOPAY");
    }
}

class DanaPayment extends DigitalPayment {
    public DanaPayment() {
        super("DANA");
    }
}

class BankPayment extends DigitalPayment {
    public BankPayment() {
        super("BANK");
    }
}
