import java.io.Serializable;

public class Transaction implements Serializable {
    double sum;
    String comment;

    public Transaction(double sum, String comment) {
        this.sum = sum;
        this.comment = comment;
    }

    public double getSum() {
        return this.sum;
    }

    public String getComment() {
        return this.comment;
    }
}
