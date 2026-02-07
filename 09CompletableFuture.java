/*
 ============================================================================
  FILE: FutureAndCompletableFutureNotes.java

  TOPIC: Future vs CompletableFuture – Java Concurrency (Interview Ready)

  WHY THIS TOPIC IS IMPORTANT:
  ----------------------------
  Almost every SDE-2 / Backend interview asks:
   - Why Future is not enough?
   - Why CompletableFuture was introduced?
   - Blocking vs Non-blocking
   - Async pipeline design

 ============================================================================
*/

import java.util.concurrent.*;

public class FutureAndCompletableFutureNotes {

    /*
     ============================================================================
     1️⃣ FUTURE (Java 5)
     ----------------------------------------------------------------------------
     - Represents result of an async computation
     - Returned by ExecutorService.submit()
     - LIMITATIONS:
         ❌ Blocking get()
         ❌ No chaining
         ❌ No callbacks
         ❌ Hard to combine multiple async tasks
     ============================================================================
    */

    static void futureExample() throws Exception {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Callable task (returns value)
        Callable<Integer> task = () -> {
            Thread.sleep(1000);
            return 10;
        };

        // Submit task -> returns Future
        Future<Integer> future = executor.submit(task);

        /*
         ------------------------------------------------------------------------
         future.get()
         ------------------------------------------------------------------------
         - BLOCKING call
         - Thread waits until result is available
         - This defeats async benefits in many cases
         ------------------------------------------------------------------------
        */
        Integer result = future.get();
        System.out.println("Future result: " + result);

        executor.shutdown();
    }

    /*
     ============================================================================
     2️⃣ COMPLETABLE FUTURE (Java 8)
     ----------------------------------------------------------------------------
     - Advanced async API
     - Supports:
         ✔ Non-blocking execution
         ✔ Callbacks
         ✔ Chaining
         ✔ Combining tasks
         ✔ Exception handling
     ============================================================================
    */

    static void completableFutureBasic() {

        /*
         ------------------------------------------------------------------------
         supplyAsync()
         ------------------------------------------------------------------------
         - Runs task asynchronously
         - Returns CompletableFuture<T>
         - Uses ForkJoinPool.commonPool() by default
         ------------------------------------------------------------------------
        */
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            return 20;
        });

        /*
         ------------------------------------------------------------------------
         thenAccept()
         ------------------------------------------------------------------------
         - Consumes result
         - Does NOT return anything
         - Non-blocking callback
         ------------------------------------------------------------------------
        */
        cf.thenAccept(result -> {
            System.out.println("CompletableFuture result: " + result);
        });

        // Main thread continues without blocking
    }

    /*
     ============================================================================
     3️⃣ thenApply vs thenAccept vs thenRun (VERY IMPORTANT)
     ============================================================================
    */

    static void thenApplyAcceptRun() {

        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> 5);

        // thenApply -> transforms result and returns new CompletableFuture
        CompletableFuture<Integer> transformed =
                cf.thenApply(value -> value * 2);

        // thenAccept -> consumes result, returns CompletableFuture<Void>
        transformed.thenAccept(value ->
                System.out.println("Value after thenApply: " + value)
        );

        // thenRun -> no access to result
        cf.thenRun(() ->
                System.out.println("Task completed")
        );
    }

    /*
     ============================================================================
     4️⃣ EXCEPTION HANDLING (KEY INTERVIEW TOPIC)
     ============================================================================
    */

    static void completableFutureException() {

        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            if (true) {
                throw new RuntimeException("Something failed");
            }
            return 10;
        });

        /*
         ------------------------------------------------------------------------
         exceptionally()
         ------------------------------------------------------------------------
         - Handles exception
         - Provides fallback value
         ------------------------------------------------------------------------
        */
        cf.exceptionally(ex -> {
            System.out.println("Exception occurred: " + ex.getMessage());
            return 0; // fallback
        }).thenAccept(result ->
                System.out.println("Final result: " + result)
        );
    }

    /*
     ============================================================================
     5️⃣ thenCompose (FLATMAP) – DEPENDENT TASKS
     ----------------------------------------------------------------------------
     Use when:
       Task-2 depends on result of Task-1
     ============================================================================
    */

    static void thenComposeExample() {

        CompletableFuture<Integer> cf =
                CompletableFuture.supplyAsync(() -> 10)
                        .thenCompose(result ->
                                CompletableFuture.supplyAsync(() -> result * 3)
                        );

        cf.thenAccept(finalResult ->
                System.out.println("thenCompose result: " + finalResult)
        );
    }

    /*
     ============================================================================
     6️⃣ thenCombine – PARALLEL TASKS
     ----------------------------------------------------------------------------
     Use when:
       Two independent async tasks need to be combined
     ============================================================================
    */

    static void thenCombineExample() {

        CompletableFuture<Integer> cf1 =
                CompletableFuture.supplyAsync(() -> 10);

        CompletableFuture<Integer> cf2 =
                CompletableFuture.supplyAsync(() -> 20);

        CompletableFuture<Integer> combined =
                cf1.thenCombine(cf2, (a, b) -> a + b);

        combined.thenAccept(sum ->
                System.out.println("Combined result: " + sum)
        );
    }

    /*
     ============================================================================
     7️⃣ BLOCKING vs NON-BLOCKING (INTERVIEW GOLD)
     ============================================================================
    */

    static void blockingVsNonBlocking() throws Exception {

        CompletableFuture<Integer> cf =
                CompletableFuture.supplyAsync(() -> 50);

        // ❌ BLOCKING (avoid in reactive systems)
        Integer value = cf.get();

        // ✅ NON-BLOCKING (preferred)
        cf.thenAccept(result ->
                System.out.println("Non-blocking result: " + result)
        );
    }

    /*
     ============================================================================
     MAIN METHOD
     ============================================================================
    */

    public static void main(String[] args) throws Exception {

        futureExample();

        completableFutureBasic();

        thenApplyAcceptRun();

        completableFutureException();

        thenComposeExample();

        thenCombineExample();

        blockingVsNonBlocking();

        // Sleep added only to allow async tasks to finish in demo
        Thread.sleep(2000);
    }
}

/*
 ============================================================================
  INTERVIEW ONE-LINERS (MUST MEMORIZE)
  ----------------------------------

  ✔ Why Future is limited?
    "Future is blocking and does not support chaining or callbacks."

  ✔ Why CompletableFuture?
    "To enable non-blocking async pipelines."

  ✔ thenApply vs thenCompose?
    "thenApply transforms value, thenCompose flattens dependent async tasks."

  ✔ thenCompose vs thenCombine?
    "thenCompose is for dependent tasks, thenCombine is for independent tasks."

  ✔ Is CompletableFuture blocking?
    "Only if you call get() or join()."

 ============================================================================
*/