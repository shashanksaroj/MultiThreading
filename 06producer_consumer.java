/*
PRODUCER–CONSUMER PROBLEM (INTERVIEW THEORY)

1. Definition:
   - Producer produces data and puts it into a shared buffer.
   - Consumer consumes data from the same buffer.
   - Buffer has limited capacity (here: only 1 item).

2. Problems to solve:
   - Producer should WAIT if buffer is full.
   - Consumer should WAIT if buffer is empty.
   - Access to buffer must be THREAD-SAFE.

3. Concepts used:
   - synchronized → ensures mutual exclusion (only one thread at a time).
   - wait() → causes current thread to wait and RELEASES the lock.
   - notify() → wakes up a waiting thread.
   - while loop → handles spurious wakeups (important interview point).

4. Key Interview Points:
   - wait() and notify() must be called inside synchronized methods/blocks.
   - wait() releases the lock, sleep() does NOT.
   - This is inter-thread COMMUNICATION, not just locking.
*/

class Buffer {
    private int data;
    private boolean hasData = false;

    // Producer calls this method
    public synchronized void produce(int value) throws InterruptedException {

        // If buffer is full, producer waits
        while (hasData) {
            wait();
        }

        // Produce data
        data = value;
        hasData = true;
        System.out.println("Produced: " + value);

        // Notify consumer that data is available
        notify();
    }

    // Consumer calls this method
    public synchronized int consume() throws InterruptedException {

        // If buffer is empty, consumer waits
        while (!hasData) {
            wait();
        }

        // Consume data
        hasData = false;
        System.out.println("Consumed: " + data);

        // Notify producer that buffer is free
        notify();
        return data;
    }
}

// Producer Thread
class Producer extends Thread {
    private Buffer buffer;

    Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        try {
            for (int i = 1; i <= 5; i++) {
                buffer.produce(i);
                Thread.sleep(500); // simulate work
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Consumer Thread
class Consumer extends Thread {
    private Buffer buffer;

    Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        try {
            for (int i = 1; i <= 5; i++) {
                buffer.consume();
                Thread.sleep(500); // simulate work
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Main class
public class ProducerConsumerSingleFile {
    public static void main(String[] args) {
        Buffer buffer = new Buffer();
        new Producer(buffer).start();
        new Consumer(buffer).start();
    }
}
