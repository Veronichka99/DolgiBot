public class Transaction {
    private float sum;
    private String comment;

    Transaction(float sum, String comment) {
        this.sum = sum;
        this.comment = comment;
    }

    public float getSum() {
        return this.sum;
    }

    public String getComment() {
        return this.comment;
    }
}
