/*
 =============================================================================
 FILE: ForkJoinAndParallelismNotes.java

 PHASE 7: FORK-JOIN & PARALLELISM (SDE-2+)

 WHY THIS MATTERS IN INTERVIEWS:
 -------------------------------
 - Parallelism != free performance
 - Misuse can make systems slower
 - Interviewers test understanding of CPU vs IO workloads
 - Deep JVM + concurrency knowledge

 =============================================================================
*/

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class ForkJoinAndParallelismNotes {

    /*
     ============================================================================
     1️⃣9️⃣ FORK JOIN POOL
     ----------------------------------------------------------------------------
     - Introduced in Java 7
     - Designed for CPU-bound parallel tasks
     - Uses Divide & Conquer strategy
     - Uses WORK-STEALING algorithm
     ============================================================================
    */

    /*
     ============================================================================
     DIVIDE & CONQUER CONCEPT
     ----------------------------------------------------------------------------
     - Break big task into smaller sub-tasks
     - Solve sub-tasks in parallel
     - Combine results
     ============================================================================
    */

    static class SumTask extends RecursiveTask<Integer> {

        private static final int THRESHOLD = 5;
        private final int[] arr;
        private final int start;
        private final int end;

        SumTask(int[] arr, int start, int end) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {

            // Base condition (small enough task)
            if (end - start <= THRESHOLD) {
                int sum = 0;
                for (int i = start; i < end; i++) {
                    sum += arr[i];
                }
                return sum;
            }

            /*
             --------------------------------------------------------------------
             Fork step:
             --------------------------------------------------------------------
             - Split task into two subtasks
             --------------------------------------------------------------------
            */
            int mid = (start + end) / 2;
            SumTask leftTask = new SumTask(arr, start, mid);
            SumTask rightTask = new SumTask(arr, mid, end);

            leftTask.fork();                 // async execution
            int rightResult = rightTask.compute(); // compute one directly
            int leftResult = leftTask.join();       // wait for forked task

            /*
             --------------------------------------------------------------------
             Join step:
             --------------------------------------------------------------------
             - Combine results
             --------------------------------------------------------------------
            */
            return leftResult + rightResult;
        }
    }

    /*
     ============================================================================
     WORK-STEALING (VERY IMPORTANT)
     ----------------------------------------------------------------------------
     - Each worker thread has its own deque
     - Idle threads STEAL tasks from busy threads
     - Improves CPU utilization
     - Reduces thread starvation
     ============================================================================
    */

    static void forkJoinExample() {

        ForkJoinPool pool = ForkJoinPool.commonPool();

        int[] numbers = IntStream.rangeClosed(1, 20).toArray();

        SumTask task = new SumTask(numbers, 0, numbers.length);

        int result = pool.invoke(task);

        System.out.println("ForkJoin result: " + result);
    }

    /*
     ============================================================================
     USED INTERNALLY BY (INTERVIEW GOLD)
     ----------------------------------------------------------------------------
     ✔ Parallel Streams
     ✔ CompletableFuture (default async)
     ============================================================================
    */

    /*
     ============================================================================
     2️⃣0️⃣ PARALLEL STREAMS
     ----------------------------------------------------------------------------
     - Introduced in Java 8
     - Uses ForkJoinPool.commonPool()
     - Splits data and processes in parallel
     ============================================================================
    */

    static void parallelStreamExample() {

        int sum =
                IntStream.rangeClosed(1, 100)
                        .parallel() // switches to parallel execution
                        .sum();

        System.out.println("Parallel stream sum: " + sum);
    }

    /*
     ============================================================================
     WHEN NOT TO USE PARALLEL STREAMS ⭐⭐⭐
     ----------------------------------------------------------------------------
     ❌ I/O bound tasks
        - Threads block waiting for network / DB
        - ForkJoinPool threads get wasted

     ❌ Shared mutable state
        - Race conditions
        - Synchronization overhead kills performance

     ❌ Small datasets
        - Thread creation + splitting cost > benefit
     ============================================================================
    */

    static void whenNotToUseParallelStreams() {

        /*
         ------------------------------------------------------------------------
         BAD EXAMPLE: Shared mutable state
         ------------------------------------------------------------------------
        */
        int[] counter = new int[1];

        IntStream.range(0, 1000)
                .parallel()
                .forEach(i -> {
                    // ❌ NOT THREAD-SAFE
                    counter[0]++;
                });

        // Result is unpredictable
        System.out.println("Wrong counter value: " + counter[0]);
    }

    /*
     ============================================================================
     PERFORMANCE REALITY CHECK (INTERVIEW MUST SAY)
     ----------------------------------------------------------------------------
     - Parallel streams are NOT free performance
     - Best for:
         ✔ CPU-bound
         ✔ Large datasets
         ✔ Stateless operations
     ============================================================================
    */

    /*
     ============================================================================
     MAIN METHOD
     ============================================================================
    */

    public static void main(String[] args) {

        forkJoinExample();

        parallelStreamExample();

        whenNotToUseParallelStreams();
    }
}

/*
 =============================================================================
 INTERVIEW ONE-LINERS (MUST MEMORIZE)
 ----------------------------------

 ✔ ForkJoinPool:
   "Uses divide-and-conquer with work-stealing."

 ✔ Work-stealing:
   "Idle threads steal tasks from busy threads."

 ✔ Parallel Streams:
   "Built on top of ForkJoinPool."

 ✔ When NOT to use parallel streams:
   "IO tasks, shared mutable state, small datasets."

 ✔ Killer interview line ⭐⭐⭐:
   "Parallel streams are not free performance."

 =============================================================================
*/