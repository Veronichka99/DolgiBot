import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAbilityExtension implements AbilityExtension {
    private SilentSender silent;
    DBContext db;
    private List<Person> usersList = new ArrayList<Person>();

    public MyAbilityExtension(SilentSender silent, DBContext db) {
        this.silent = silent;
        this.db = db;
    }

    private void addingTransaction(Long chatId, Double sum, String name, String comment) {
        Transaction transaction = new Transaction(sum, comment);
        for (Person person : usersList) {
            if (person.getChatId().equals(chatId)) {
                person.addNewTransaction(transaction, name);
            }
        }
    }

    private String formList (Map<String,Double> map) {
        String message = "";
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            message += String.format("\n%s: %s", entry.getKey(), entry.getValue());
        }
        return message;
    }

    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .input(0)
                .action(ctx -> {
                    Person person = new Person(ctx.chatId());
                    usersList.add(person);
                    silent.send("Hello! This bot helps you to control your debt relations." +
                            "\n\nIf you borrow some money from someone and want to add this transaction send /take+sum of transaction+name of person+short comment" +
                            "\nWhen you pay your date add it with /pay+sum+name+comment" +
                            "\nIf your friend asks for a loan there is command /give+sum+name+comment" +
                            "\nAnd when he/she backs your money - command /back+sum+name+comment", ctx.chatId());
                    silent.send("If you want to see all of your debt relations send /relations" +
                            "\nSend /debts to see list of your debts. To see list of people who borrowed money from you send /borrowed" +
                            "\nTo see your debt relations with certain person send /for+name of this person", ctx.chatId());
                    silent.send("Good luck!", ctx.chatId());
                })
                .build();
    }

    public Ability borrowFrom() {
        return Ability
                .builder()
                .name("take")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    addingTransaction(ctx.chatId(), Double.parseDouble(ctx.firstArg()), ctx.secondArg(), ctx.thirdArg());
                    silent.send(String.format("You borrow %s from %s", ctx.firstArg() , ctx.secondArg()), ctx.chatId());
                })
                .build();
    }

    public Ability borrowTo() {
        return Ability
                .builder()
                .name("give")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    addingTransaction(ctx.chatId(), - Double.parseDouble(ctx.firstArg()), ctx.secondArg(), ctx.thirdArg());
                    silent.send(String.format("You borrow %s to %s", ctx.firstArg() , ctx.secondArg()), ctx.chatId());
                })
                .build();
    }

    public Ability backTo() {
        return Ability
                .builder()
                .name("pay")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    addingTransaction(ctx.chatId(), - Double.parseDouble(ctx.firstArg()), ctx.secondArg(), ctx.thirdArg());
                    silent.send(String.format("You paid %s to %s", ctx.firstArg() , ctx.secondArg()), ctx.chatId());
                })
                .build();
    }

    public Ability backFrom() {
        return Ability
                .builder()
                .name("back")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    addingTransaction(ctx.chatId(), Double.parseDouble(ctx.firstArg()), ctx.secondArg(), ctx.thirdArg());
                    silent.send(String.format("You back %s from %s", ctx.firstArg() , ctx.secondArg()), ctx.chatId());
                })
                .build();
    }

    public Ability showRelationships() {
        return Ability
                .builder()
                .name("relations")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .input(0)
                .action(ctx -> {
                    String message = "All relations:";
                    for (Person person : usersList) {
                        if (person.getChatId().equals(ctx.chatId())) {
                            message += formList(person.splitBalance());
                            message += String.format("\nYour debt load is %s", person.getBalance());
                        }
                    }
                    silent.send(message, ctx.chatId());
                })
                .build();
    }

    public Ability showDebts() {
        return Ability
                .builder()
                .name("debts")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .input(0)
                .action(ctx -> {
                    String message = "Borrowed from:";
                    for (Person person : usersList) {
                        if (person.getChatId().equals(ctx.chatId())) {
                            message += formList(person.debtsOnly());
                            message += String.format("\nYour whole debt is %s", Math.abs(person.getDebt()));
                        }
                    }
                    silent.send(message, ctx.chatId());
                })
                .build();
    }

    public Ability showBorrowed() {
        return Ability
                .builder()
                .name("borrowed")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .input(0)
                .action(ctx -> {
                    String message = "Borrowed to:";
                    for (Person person : usersList) {
                        if (person.getChatId().equals(ctx.chatId())) {
                            message += formList(person.borrowedOnly());
                            message += String.format("\nYour borrowed to people %s", Math.abs(person.getBorrowed()));
                        }
                    }
                    silent.send(message, ctx.chatId());
                })
                .build();
    }

    public Ability showForPerson() {
        return Ability
                .builder()
                .name("for")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .input(1)
                .action(ctx -> {
                    String message = ctx.firstArg();
                    for (Person person : usersList) {
                        if (person.getChatId().equals(ctx.chatId())) {
                            for (Transaction transaction : person.getRelationships().get(ctx.firstArg())) {
                                message += String.format("\n%s - %s", transaction.getSum(), transaction.getComment());
                            }
                            if (person.splitBalance().get(ctx.firstArg()) > 0) {
                                message += String.format("\nYou owe %s %s", ctx.firstArg(),
                                        Math.abs(person.splitBalance().get(ctx.firstArg())));
                            } else if (person.splitBalance().get(ctx.firstArg()) < 0) {
                                message += String.format("\n%s owe you %s", ctx.firstArg(),
                                        Math.abs(person.splitBalance().get(ctx.firstArg())));
                            }
                        }
                    }
                    silent.send(message, ctx.chatId());
                })
                .build();
    }

}
