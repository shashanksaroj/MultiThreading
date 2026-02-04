public class NotifyVsNotifyAll {

    private static final Object lock = new Object();
    private static boolean itemAvailable = false;

    // Consumer thread
    static class Consumer extends Thread {

        Consumer(String name) {
            super(name);
        }

        @Override
        public void run() {
            synchronized (lock) {
                while (!itemAvailable) {
                    try {
                        System.out.println(getName() + " waiting for item");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // consume item
                itemAvailable = false;
                System.out.println(getName() + " consumed item");
            }
        }
    }

    // Producer thread
    static class Producer extends Thread {

        @Override
        public void run() {
            synchronized (lock) {
                itemAvailable = true;
                System.out.println("Producer produced item");

                // 🔴 TRY notify() VS notifyAll()
                // lock.notify();      // may cause deadlock
                lock.notifyAll();     // correct
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // Start multiple consumers
        new Consumer("Consumer-1").start();
        new Consumer("Consumer-2").start();
        new Consumer("Consumer-3").start();

        Thread.sleep(1000);

        // Start producer
        new Producer().start();
    }
}
