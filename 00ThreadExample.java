package Multithread;

public class ThreadExample extends Thread {



        @Override
        public void run() {

            for (int i = 0; i <=5; i++) {
                
               System.out.println("Thread Running: "+ Thread.currentThread().getName());

               try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            }
        }
    


    public static void main(String[] args) {

        ThreadExample tc = new ThreadExample();
        ThreadExample tc1 = new ThreadExample();


        tc.start();
        tc1.start();
        
    }
    
}
