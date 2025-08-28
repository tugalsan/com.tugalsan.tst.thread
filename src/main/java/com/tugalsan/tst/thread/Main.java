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

        Callable<String> tenSecsTask = () -> {
            Thread.sleep(Duration.ofSeconds(10));
            return "a";
        };
        var allAwait = allAwait("allAwait", Duration.ofSeconds(2), tenSecsTask);
        allAwait.resultsSuccessful().forEach(IO::println);
        IO.println("main.done..");
    }

    public static record AllAwait<R>(String name, Duration timeout, List<R> resultsSuccessful, List<StructuredTaskScope.Subtask<R>> resultsFailedOrUnavailable, Optional<StructuredTaskScope.TimeoutException> timeoutException) {

    }

    public static <R> AllAwait<R> allAwait(String name, Duration timeout, Callable<R>... callables) {
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
            var resultsSuccessful = subTasks.stream().filter(st -> st.state() == State.SUCCESS).map(StructuredTaskScope.Subtask::get).toList();
            var resultsFailedOrUnavailable = subTasks.stream().filter(st -> st.state() == State.FAILED || st.state() == State.UNAVAILABLE).toList();
            return new AllAwait(name, timeout, resultsSuccessful, resultsFailedOrUnavailable, Optional.empty());
        } catch (InterruptedException | StructuredTaskScope.TimeoutException e) {
            throwIfInterruptedException(e);
            if (e instanceof StructuredTaskScope.TimeoutException et) {
                return new AllAwait(name, timeout, List.of(), List.of(), Optional.of(et));
            }
            return null;
        }
    }

    public static record AllAwaitNoType(String name, Duration timeout, List resultsSuccessful, List<StructuredTaskScope.Subtask> resultsFailed, Optional<StructuredTaskScope.TimeoutException> timeoutException) {

    }

    public static AllAwaitNoType allAwaitNoType(String name, Duration timeout, Callable... callables) {
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
            return new AllAwaitNoType(name, timeout, resultsSuccessful, resultsFailed, Optional.empty());
        } catch (InterruptedException | StructuredTaskScope.TimeoutException e) {
            throwIfInterruptedException(e);
            if (e instanceof StructuredTaskScope.TimeoutException et) {
                return new AllAwaitNoType(name, timeout, List.of(), List.of(), Optional.of(et));
            }
            return null;
        }
    }

    //-------------------- INTERRUPTED EXCEPTION ----------------
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void _throwAsUncheckedException(Throwable exception) throws T {
        throw (T) exception;
    }

    @Deprecated //only internalUse
    private static void throwAsUncheckedException(Throwable exception) {
        Main.<RuntimeException>_throwAsUncheckedException(exception);
    }

    public static <R> R throwIfInterruptedException(Throwable t) {
        if (isInterruptedException(t)) {
            Thread.currentThread().interrupt();
            throwAsUncheckedException(t);
        }
        return null;
    }

    public static boolean isInterruptedException(Throwable t) {
        if (t instanceof InterruptedException) {
            return true;
        }
        if (t.getCause() != null) {
            return isInterruptedException(t.getCause());
        }
        return false;
    }

    public static record AnySuccessfulOrThrow<R>(String name, Duration timeout, Optional<R> result, Optional<StructuredTaskScope.FailedException> e) {

    }

    public static <R> AnySuccessfulOrThrow<R> anySuccessfulOrThrow(String name, Duration timeout, Callable<R>... callables) {
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
            throwIfInterruptedException(e);
            return new AnySuccessfulOrThrow(name, timeout, Optional.empty(), Optional.of((StructuredTaskScope.FailedException) e));
        }
    }

    public static record AllSuccessfulOrThrow<R>(String name, Duration timeout, Optional<List<R>> results, Optional<StructuredTaskScope.FailedException> e) {

    }

    public static <R> AllSuccessfulOrThrow<List<R>> allSuccessfulOrThrow(String name, Duration timeout, Callable<R>... callables) {
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
            return new AllSuccessfulOrThrow(name, timeout, Optional.of(scope.join().map(StructuredTaskScope.Subtask::get).toList()), Optional.empty());
        } catch (InterruptedException | StructuredTaskScope.FailedException e) {
            throwIfInterruptedException(e);
            return new AllSuccessfulOrThrow(name, timeout, Optional.empty(), Optional.of((StructuredTaskScope.FailedException) e));
        }
    }
}
