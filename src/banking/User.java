package banking;

public class User {
    private String cardNumber;
    private String pinCode;
    private long balance;
    public User(String pinCode, String cardNumber) {
        this.pinCode = pinCode;
        this.balance = 0;
        this.cardNumber = "400000" + cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPinCode() {
        return pinCode;
    }
}