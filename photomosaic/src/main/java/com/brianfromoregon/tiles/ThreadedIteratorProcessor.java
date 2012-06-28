package com.brianfromoregon.tiles;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A utility class to process each element in an iterator in an efficient manner.
 * Taken from my own stackoverflow answer to my own question here:
 * http://stackoverflow.com/questions/1932201/parallel-computation-for-an-iterator-of-elements-in-java/1949889#1949889
 */
public class ThreadedIteratorProcessor<T> {

    public static interface ElementProcessor<T> {

        /**
         * Process an element.
         * @param element The element to process.
         */
        public void processElement(T element);
    }
    private final int numThreads;

    /**
     * Create an instance which uses a specified number of threads.
     * @param numThreads The number of processing threads.
     */
    public ThreadedIteratorProcessor(int numThreads) {
        this.numThreads = numThreads;
    }

    /**
     * Create an instance which uses a number of threads equal to the number of system processors.
     */
    public ThreadedIteratorProcessor() {
        this(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Process each element in an iterator in parallel.  The number of worker threads depends on how this object was
     * constructed.  This method will re-throw any exception thrown in the supplied ElementProcessor.  An element will
     * not be requested from the iterator any earlier than is absolutely necessary.  In other words, the last element in
     * the iterator will not be consumed until all of the other elements are completely processed, excluding elements
     * currently being processed by the worker threads.
     * @param iterator The iterator from which to get elements.  This iterator need not be thread-safe.
     * @param elementProcessor The element processor.
     */
    public void processIterator(Iterator<T> iterator, ElementProcessor<T> elementProcessor) {
        // Use an ExecutorService for proper exception handling.
        ExecutorService e = Executors.newFixedThreadPool(numThreads, new ThreadFactoryBuilder().setDaemon(true).build());
        List<Future<?>> futures = Lists.newLinkedList();

        // Get a thread-safe iterator
        final SafeIterator<T> safeIterator = new SafeIterator<T>(iterator);

        // Submit numThreads new worker threads to pull work from the iterator.
        for (int i = 0; i < numThreads; i++) {
            futures.add(e.submit(new Consumer<T>(safeIterator, elementProcessor)));
        }

        e.shutdown();

        // Calling .get() on the futures accomplishes two things:
        // 1. awaiting completion of the work
        // 2. discovering an exception during calculation, and rethrowing to the client in this thread.
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException ex) {
                // swallowing is OK
            } catch (ExecutionException ex) {
                // Re-throw the underlying exception to the client.
                throw Throwables.propagate(ex.getCause());
            }
        }
    }

    // A runnable that sits in a loop consuming and processing elements from an iterator.
    private static class Consumer<T> implements Runnable {

        private final SafeIterator<T> it;
        private final ElementProcessor<T> elementProcessor;

        public Consumer(SafeIterator<T> it, ElementProcessor<T> elementProcessor) {
            this.it = it;
            this.elementProcessor = elementProcessor;
        }

        @Override
        public void run() {
            while (true) {
                T o = it.nextOrNull();
                if (o == null) {
                    return;
                }
                elementProcessor.processElement(o);
            }
        }
    }

    // a thread-safe iterator-like object.
    private static class SafeIterator<T> {

        private final Iterator<T> in;

        SafeIterator(Iterator<T> in) {
            this.in = in;
        }

        synchronized T nextOrNull() {
            if (in.hasNext()) {
                return in.next();
            }
            return null;
        }
    }
}
