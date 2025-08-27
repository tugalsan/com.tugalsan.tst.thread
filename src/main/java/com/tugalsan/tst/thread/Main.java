package com.tugalsan.tst.thread;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

public class Main {

//    private static final TS_Log d = TS_Log.of(true, Main.class);
    //cd C:\me\codes\com.tugalsan\tst\com.tugalsan.tst.thread
    //java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.tst.thread-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... s) {
//        TS_ThreadSyncTrigger killTrigger = TS_ThreadSyncTrigger.of("main");

//        allSuccessfulOrThrow("ali", Duration.ofSeconds(2));
        IO.println("main.done..");
    }

    public static record AnySuccessfulOrThrow<R>(String name, Duration timeout, Optional<R> result, Optional<StructuredTaskScope.FailedException> e) {

    }

    public static <R> AnySuccessfulOrThrow<R> anySuccessfulOrThrow(String name, Duration timeout, Callable<R>... callables) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(Joiner.<R>anySuccessfulResultOrThrow(),
                cf -> {
                    if (name != null) {
                        cf.withName(name);
                    }
                    if (timeout != null) {
                        cf.withTimeout(timeout);
                    }
                    return cf;
                }
        )) {
            Arrays.stream(callables).forEach(scope::fork);
            return new AnySuccessfulOrThrow(name, timeout, Optional.of(scope.join()), Optional.empty());
        } catch (InterruptedException | StructuredTaskScope.FailedException e) {
            if (e instanceof InterruptedException eie) {
                Thread.currentThread().interrupt();
                throw eie;
            }
            return new AnySuccessfulOrThrow(name, timeout, Optional.empty(), Optional.of((StructuredTaskScope.FailedException) e));
        }
    }

    public static record AllSuccessfulOrThrow<R>(String name, Duration timeout, Optional<List<R>> results, Optional<StructuredTaskScope.FailedException> e) {

    }

    public static <R> AllSuccessfulOrThrow<List<R>> allSuccessfulOrThrow(String name, Duration timeout, Callable<R>... callables) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(Joiner.<R>allSuccessfulOrThrow(),
                cf -> {
                    if (name != null) {
                        cf.withName(name);
                    }
                    if (timeout != null) {
                        cf.withTimeout(timeout);
                    }
                    return cf;
                }
        )) {
            Arrays.stream(callables).forEach(scope::fork);
            return new AllSuccessfulOrThrow(name, timeout, Optional.of(scope.join().map(c -> c.get()).toList()), Optional.empty());
        } catch (InterruptedException | StructuredTaskScope.FailedException e) {
            if (e instanceof InterruptedException eie) {
                Thread.currentThread().interrupt();
                throw eie;
            }
            return new AllSuccessfulOrThrow(name, timeout, Optional.empty(), Optional.of((StructuredTaskScope.FailedException) e));
        }
    }

    public static record AllAwait<R>(String name, Duration timeout, List<R> resultsSuccessful, List<R> resultsFailedOrUnavailable) {

    }

    public static <R> AllAwait<R> allAwait(String name, Duration timeout, Callable<R>... callables) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(Joiner.<R>awaitAll(),
                cf -> {
                    if (name != null) {
                        cf.withName(name);
                    }
                    if (timeout != null) {
                        cf.withTimeout(timeout);
                    }
                    return cf;
                }
        )) {
            var subTasks = Arrays.stream(callables).map(scope::fork).toList();
            scope.join();
            var resultsSuccessful = subTasks.stream().filter(st -> st.state() == State.SUCCESS).toList();
            var resultsFailedOrUnavailable = subTasks.stream().filter(st -> st.state() == State.FAILED || st.state() == State.UNAVAILABLE).toList();
            return new AllAwait(name, timeout, resultsSuccessful, resultsFailedOrUnavailable);
        } catch (InterruptedException e) {
//            if (e instanceof InterruptedException eie) {
            Thread.currentThread().interrupt();
            throw e;
//            }
        }
    }

    public static record AllAwaitNoType(String name, Duration timeout, List resultsSuccessful, List resultsFailed) {

    }

    public static AllAwaitNoType allAwaitNoType(String name, Duration timeout, Callable... callables) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(Joiner.awaitAll(),
                cf -> {
                    if (name != null) {
                        cf.withName(name);
                    }
                    if (timeout != null) {
                        cf.withTimeout(timeout);
                    }
                    return cf;
                }
        )) {
            var subTasks = Arrays.stream(callables).map(scope::fork).toList();
            scope.join();
            var resultsSuccessful = subTasks.stream().filter(st -> st.state() == State.SUCCESS).toList();
            var resultsFailed = subTasks.stream().filter(st -> st.state() == State.FAILED || st.state() == State.UNAVAILABLE).toList();
            return new AllAwaitNoType(name, timeout, resultsSuccessful, resultsFailed);
        } catch (InterruptedException e) {
//            if (e instanceof InterruptedException eie) {
            Thread.currentThread().interrupt();
            throw e;
//            }
        }
    }

}
