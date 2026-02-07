/*
 ============================================================================
  FILE: RunnableCallableNotes.java

  TOPIC: Runnable vs Callable (Java Concurrency) – Interview Notes

  IMPORTANT INTERVIEW CONTEXT:
  ----------------------------
  Java provides two main ways to define a task for multithreading:
    1. Runnable  (Java 1.0)
    2. Callable  (Java 5 - java.util.concurrent)

  Runnable limitations:
    - Cannot return a value
    - Cannot throw checked exceptions

  Callable solves both problems:
    - Can return a value
    - Can throw checked exceptions
    - Works with Future to fetch result

 ============================================================================
*/

import java.util.concurrent.*;

public class RunnableCallableNotes {

    /*
     ============================================================================
     1️⃣ RUNNABLE
     ----------------------------------------------------------------------------
     - Functional Interface
     - Single abstract method: run()
     - Return type: void
     - No checked exceptions allowed
     - Used for "fire-and-forget" background tasks
     ============================================================================
    */

    static class MyRunnable implements Runnable {

        @Override
        public void run() {
            // This code runs in a separate thread
            System.out.println("Runnable task executed by: "
                    + Thread.currentThread().getName());

            // Runnable CANNOT return result
            // Runnable CANNOT throw checked exception
        }
    }

    /*
     ============================================================================
     2️⃣ CALLABLE
     ----------------------------------------------------------------------------
     - Functional Interface
     - Single abstract method: call()
     - Returns a value (Generic type)
     - Can throw checked exceptions
     - Used when result is required
     ============================================================================
    */

    static class MyCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            // Simulating some computation
            Thread.sleep(1000);

            // Callable CAN return value
            return 10 + 20;
        }
    }

    /*
     ============================================================================
     3️⃣ MAIN METHOD
     ----------------------------------------------------------------------------
     Demonstrates:
       - Runnable execution
       - Callable execution
       - Future usage
       - Blocking behavior
       - Exception handling
     ============================================================================
    */

    public static void main(String[] args) throws Exception {

        /*
         ------------------------------------------------------------------------
         ExecutorService
         ------------------------------------------------------------------------
         - Manages thread pool
         - Preferred over creating threads manually
         - Improves performance & resource management
         ------------------------------------------------------------------------
        */
        ExecutorService executor = Executors.newFixedThreadPool(2);

        /*
         =========================================================================
         RUNNABLE EXECUTION
         =========================================================================
         */
        Runnable runnableTask = new MyRunnable();

        // execute() is used for Runnable
        // No return value
        executor.execute(runnableTask);

        /*
         =========================================================================
         CALLABLE EXECUTION
         =========================================================================
         */
        Callable<Integer> callableTask = new MyCallable();

        /*
         submit() returns Future<T>
         Future acts as:
           - Result holder
           - Status checker
           - Cancellation handle
        */
        Future<Integer> future = executor.submit(callableTask);

        /*
         =========================================================================
         FUTURE.get()
         =========================================================================
         - Blocking call
         - Waits until task completes
         - Throws:
             InterruptedException
             ExecutionException (wraps original exception)
         =========================================================================
        */
        Integer result = future.get();
        System.out.println("Callable result: " + result);

        /*
         =========================================================================
         CALLABLE EXCEPTION HANDLING
         =========================================================================
        */
        Callable<Integer> exceptionTask = () -> {
            // Checked exception allowed in Callable
            throw new Exception("Business logic failed");
        };

        Future<Integer> errorFuture = executor.submit(exceptionTask);

        try {
            errorFuture.get(); // Exception occurs here
        } catch (ExecutionException e) {
            // Original exception is wrapped inside ExecutionException
            System.out.println("Exception from Callable: "
                    + e.getCause().getMessage());
        }

        /*
         =========================================================================
         SHUTDOWN
         =========================================================================
         - Always shutdown ExecutorService
         - Prevents memory leaks
         =========================================================================
        */
        executor.shutdown();
    }
}

/*
 ============================================================================
  INTERVIEW ONE-LINERS (VERY IMPORTANT)
  ------------------------------------

  ✔ Runnable:
    "Runnable is used for tasks that do not return a result."

  ✔ Callable:
    "Callable is used for tasks that return a value and can throw exceptions."

  ✔ Why Callable was introduced?
    "Runnable lacked return value and exception handling."

  ✔ Future.get():
    "It is a blocking call."

  ✔ Can Callable be used with Thread?
    "No, only with ExecutorService."

 ============================================================================
*/