/*
EXECUTORSERVICE (INTERVIEW THEORY)

1. What is ExecutorService?
   - ExecutorService is a framework in java.util.concurrent
   - It manages a POOL of threads for us.
   - We submit tasks, NOT threads.

2. Why ExecutorService?
   - Creating threads manually is expensive.
   - Hard to manage lifecycle (start, stop, reuse).
   - ExecutorService handles:
       • Thread creation
       • Thread reuse
       • Task scheduling
       • Graceful shutdown

3. Key Interview Advantages:
   - Better performance using thread pooling.
   - Prevents resource exhaustion.
   - Cleaner and more maintainable code.

4. Main Interfaces & Classes:
   - Executor → execute(Runnable)
   - ExecutorService → submit(), shutdown()
   - Executors → factory class to create thread pools

5. Common Thread Pools:
   - newSingleThreadExecutor() → one thread
   - newFixedThreadPool(n) → fixed number of threads
   - newCachedThreadPool() → dynamic threads

6. Task Types:
   - Runnable → no return value
   - Callable → returns value (Future)

7. Shutdown Methods (VERY IMPORTANT):
   - shutdown() → stops accepting new tasks, finishes old ones
   - shutdownNow() → attempts to stop immediately

8. Interview One-liner:
   - "ExecutorService separates task submission from task execution."
*/

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceExample {

    public static void main(String[] args) {

        // Create a fixed thread pool with 2 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Submit tasks (Runnable)
        for (int i = 1; i <= 5; i++) {
            int taskNumber = i;

            executor.execute(() -> {
                System.out.println(
                    "Task " + taskNumber +
                    " executed by " +
                    Thread.currentThread().getName()
                );
            });
        }

        // Proper shutdown of ExecutorService
        executor.shutdown();
    }
}
