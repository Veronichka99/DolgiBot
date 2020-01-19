import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Person {
    Long chatId;
    Map<String, ArrayList<Transaction>> relationships = new HashMap<>();

    public Person(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public Map<String, ArrayList<Transaction>> getRelationships() {
        return relationships;
    }

    public void addNewTransaction(Transaction transaction, String name) {
        if (relationships.containsKey(name)) {
            relationships.get(name).add(transaction);
        } else {
            ArrayList<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);
            relationships.put(name, transactions);
        }
    }

    public Map<String, Double> splitBalance() {
        Map<String, Double> listToDouble = new HashMap<>();
        for (Map.Entry<String,ArrayList<Transaction>> entry : relationships.entrySet()) {
            double balance = 0.0;
            for (Transaction transaction : entry.getValue()) {
                balance += transaction.getSum();
            }
            if (balance != 0) {
                listToDouble.put(entry.getKey(), balance);
            }
        }
        return listToDouble;
    }

    public Double getBalance() {
        double balance = 0.0;
        for (Map.Entry<String, Double> entry : splitBalance().entrySet()) {
            balance += entry.getValue();
        }
        return balance;
    }

    public Map<String, Double> debtsOnly() {
        Map<String, Double> listToDouble = new HashMap<>();
        for (Map.Entry<String,ArrayList<Transaction>> entry : relationships.entrySet()) {
            double balance = 0.0;
            for (Transaction transaction : entry.getValue()) {
                balance += transaction.getSum();
            }
            if (balance > 0) {
                listToDouble.put(entry.getKey(), balance);
            }
        }
        return listToDouble;
    }

    public Map<String, Double> borrowedOnly() {
        Map<String, Double> listToDouble = new HashMap<>();
        for (Map.Entry<String,ArrayList<Transaction>> entry : relationships.entrySet()) {
            double balance = 0.0;
            for (Transaction transaction : entry.getValue()) {
                balance += transaction.getSum();
            }
            if (balance < 0) {
                listToDouble.put(entry.getKey(), balance);
            }
        }
        return listToDouble;
    }

    public double getDebt() {
        double balance = 0.0;
        for (Map.Entry<String, Double> entry : debtsOnly().entrySet()) {
            balance += entry.getValue();
        }
        return balance;
    }

    public double getBorrowed() {
        double balance = 0.0;
        for (Map.Entry<String, Double> entry : borrowedOnly().entrySet()) {
            balance += entry.getValue();
        }
        return balance;
    }
}
