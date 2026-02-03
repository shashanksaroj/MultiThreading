package Multithread;

class BankAccount {
    int balance = 1000;

    synchronized void withdraw(int amount) {
        balance = balance - amount;
        System.out.println(
            Thread.currentThread().getName() +
            " withdrew " + amount +
            ", Balance: " + balance
        );
    }
}

public class SyncExample {
    public static void main(String[] args) {

        BankAccount account = new BankAccount();

        Runnable task = () -> {
            account.withdraw(500);
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();
    }
}
