package Multithread;


public class RunnableExample implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i <= 5; i++) {

            System.out.println("Thread Running: " + Thread.currentThread().getName());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {

        RunnableExample runnable = new RunnableExample();
    
        Thread t1 = new Thread(runnable,"Thraed a");
        Thread t2 = new Thread(runnable,"Thraed b");
        t1.start();
        t2.start();

    }

}
