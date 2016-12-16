import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by zstan on 01.12.16.
 */
public class Bank {
    private final Random random = new Random();
    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return random.nextBoolean();
    }
}

class Account implements Comparable<Account> {
    private long money;
    private String accNumber;

    @Override
    public int compareTo(Account o) {
        return this.accNumber.compareTo(o.accNumber);
    }

    public void transfer(Account to, long amount) {
        this.money -= amount;
        to.money += amount;
    }

    public String getAccNumber() {
        return accNumber;
    }
}

class AccountManager {

    private Bank bank = new Bank();

    // можно организовать сторадж на ConcurrentSkipListSet, но он внутри содержит ту же самую ConcurrentHashMap, что оверхед
    // ConcurrentSkipListSet accountStorage = ...

    // возьмем по простому:
    Set<Account> accountStorage = Collections.synchronizedSet(new HashSet<>());
    // т.к. я не стал делать вначале мапу. будем хранить информацию о фрод блокировках (их же меньше чем аккаунтов) в отдельном сете
    Set<Account> fraudAccountStorage = Collections.synchronizedSet(new HashSet<>());
    // сберегая место, я усложняю работу разработчику, нужно держать эти две структуры консистентными.

    // 1. тут нужно синхронизироваться на обьектах Account и не словить dead lock , можно на последовательных синхронайзах или на tryLock
    // 2. isFraud - долгая операция, нужно как то красиво ее обработать
    public void transfer(Account from, Account to, long amount) {
        // проверка на фрод
        if (fraudAccountStorage.contains(from) && fraudAccountStorage.contains(to))
            return; // ну как то пишем \ сообщаем.

        Account acc1 = from.compareTo(to) > 0 ? from : to;
        Account acc2 = from.compareTo(to) > 0 ? to : from;
        synchronized (acc1) {
            synchronized (acc2) {
                if (amount > 50000) {
                    final CompletableFuture<Boolean> f1 =
                            CompletableFuture.supplyAsync(() -> {return bank.isFraud(from.getAccNumber(), to.getAccNumber(), amount);});
                    f1.thenAccept(result -> {addFraud(from, to, amount, result);});
                } else
                    from.transfer(to, amount);
            }
        }
    }

    public void addFraud(Account from, Account to, long amount, Boolean fraud) {
        if (fraud) {
            fraudAccountStorage.add(from);
            fraudAccountStorage.add(to);
        } else {
            from.transfer(to, amount);
        }
    }
}

// но это очень простая реализация, с деньгами так обращаться нельзя, тут несколько проблем
// 1. персистентность - ее скорее нужно решать подключением транзакционного стораджа ))
// 2. вся эта асинхронная конструкция может плохо себя чуствовать при большой нагрузке (если пойдет много > 50000 операций)
// тут тоже можно будет как то подумать на предмет очереди какой то.