package Multithread;

public class MultiThraedingLambda {

    public static void main(String[] args) {
        
      new Thread( ()->{
            System.out.println("Thread running :" + Thread.currentThread().getName());
        }).start();


         new Thread( ()->{
            System.out.println("Thread running :" + Thread.currentThread().getName());
        }).start();


          new Thread( ()->{
            System.out.println("Thread running :" + Thread.currentThread().getName());
        }).start();




    }
    
}
