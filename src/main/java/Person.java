import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Person {
    Long chatId;
    Map<String, ArrayList<Transaction>> relationships = new HashMap<>();

    Person(Long chatId) {
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

    public Map<String, Float> splitBalance() {
        Map<String, Float> listToFloat = new HashMap<>();
        for (String key : relationships.keySet()) {
            Float balance = 0.0F;
            for (Transaction transaction : relationships.get(key)) {
                balance += transaction.getSum();
            }
            if (balance != 0) {
                listToFloat.put(key, balance);
            }
        }
        return listToFloat;
    }

    public Float getBalance() {
        Float balance = 0.0F;
        for (String key : splitBalance().keySet()) {
            balance += splitBalance().get(key);
        }
        return balance;
    }

    public Map<String, Float> debtsOnly() {
        Map<String, Float> listToFloat = new HashMap<>();
        for (String key : relationships.keySet()) {
            Float balance = 0.0F;
            for (Transaction transaction : relationships.get(key)) {
                balance += transaction.getSum();
            }
            if (balance > 0) {
                listToFloat.put(key, balance);
            }
        }
        return listToFloat;
    }

    public Map<String, Float> borrowedOnly() {
        Map<String, Float> listToFloat = new HashMap<>();
        for (String key : relationships.keySet()) {
            Float balance = 0.0F;
            for (Transaction transaction : relationships.get(key)) {
                balance += transaction.getSum();
            }
            if (balance < 0) {
                listToFloat.put(key, balance);
            }
        }
        return listToFloat;
    }

    public float getDebt() {
        Float balance = 0.0F;
        for (String key : debtsOnly().keySet()) {
            balance += debtsOnly().get(key);
        }
        return balance;
    }

    public float getBorrowed() {
        Float balance = 0.0F;
        for (String key : borrowedOnly().keySet()) {
            balance += borrowedOnly().get(key);
        }
        return balance;
    }
}
