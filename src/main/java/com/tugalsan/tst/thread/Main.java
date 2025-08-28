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
        Callable<String> threeSecsStringThrowingTask = () -> {
            Thread.sleep(Duration.ofSeconds(3));
            throw new RuntimeException("task 3 secs string throwing");
        };
        Callable<Long> fourSecsLongTask = () -> {
            Thread.sleep(Duration.ofSeconds(4));
            return 4L;
        };
        Callable<String> fiveSecsStringTask = () -> {
            Thread.sleep(Duration.ofSeconds(5));
            return "task 5 secs string finished";
        };
        Callable<String> sixSecsStringTask = () -> {
            Thread.sleep(Duration.ofSeconds(6));
            return "task 6 secs string finished";
        };
        IO.println("main.begin..");

        IO.println(allAwait("allAwait_success", Duration.ofSeconds(10), fiveSecsStringTask, sixSecsStringTask));
        IO.println(allAwait("allAwait_timeout1", Duration.ofSeconds(2), fiveSecsStringTask, sixSecsStringTask));
        IO.println(allAwait("allAwait_throw", Duration.ofSeconds(10), threeSecsStringThrowingTask, fiveSecsStringTask));
        IO.println(allAwait("allAwait_timeout2", Duration.ofSeconds(2), threeSecsStringThrowingTask, fiveSecsStringTask));
        IO.println(allAwaitNoType("allAwaitNoType_success", Duration.ofSeconds(10), fourSecsLongTask, sixSecsStringTask));
        IO.println(allAwaitNoType("allAwaitNoType_timeout1", Duration.ofSeconds(2), fourSecsLongTask, sixSecsStringTask));
        IO.println(allAwaitNoType("allAwaitNoType_throw", Duration.ofSeconds(10), threeSecsStringThrowingTask, fourSecsLongTask));
        IO.println(allAwaitNoType("allAwaitNoType_timeout2", Duration.ofSeconds(2), threeSecsStringThrowingTask, fourSecsLongTask));

//        anySuccessfulOrThrow_tst_success(fiveSecsTask);//TODO
//        anySuccessfulOrThrow_tst_throw(throwingTask);//TODO
//        anySuccessfulOrThrow_tst_timeout(fiveSecsTask);//TODO
//        allSuccessfulOrThrow_tst_success(fiveSecsTask);//TODO
//        allSuccessfulOrThrow_tst_throw(throwingTask);//TODO
//        allSuccessfulOrThrow_tst_timeout(fiveSecsTask);//TODO
        IO.println("main.done..");
    }

    //--------------------------- AllAwait ----------------------------
    public static record AllAwait<R>(String name, Duration timeout, List<R> resultsSuccessful, List<StructuredTaskScope.Subtask<R>> resultsFailedOrUnavailable, Optional<StructuredTaskScope.TimeoutException> timeoutException) {

    }

    public static <R> AllAwait<R> allAwait(String name, Duration timeout, Callable<R>... callables) {
        try (var scope = StructuredTaskScope.open(Joiner.<R>awaitAll(),
                cf -> {
                    if (name != null && timeout != null) {
                        return cf.withName(name).withTimeout(timeout);
                    }
                    if (timeout != null) {
                        return cf.withTimeout(timeout);
                    }
                    if (name != null) {
                        return cf.withName(name);
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

    //--------------------------- AllAwaitNoType ----------------------------
    public static record AllAwaitNoType(String name, Duration timeout, List resultsSuccessful, List<StructuredTaskScope.Subtask> resultsFailed, Optional<StructuredTaskScope.TimeoutException> timeoutException) {

    }

    public static AllAwaitNoType allAwaitNoType(String name, Duration timeout, Callable... callables) {
        try (var scope = StructuredTaskScope.open(Joiner.awaitAll(),
                cf -> {
                    if (name != null && timeout != null) {
                        return cf.withName(name).withTimeout(timeout);
                    }
                    if (timeout != null) {
                        return cf.withTimeout(timeout);
                    }
                    if (name != null) {
                        return cf.withName(name);
                    }
                    return cf;
                }
        )) {
            var subTasks = Arrays.stream(callables).map(scope::fork).toList();
            scope.join();
            var resultsSuccessful = subTasks.stream().filter(st -> st.state() == State.SUCCESS).map(StructuredTaskScope.Subtask::get).toList();
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

    //--------------------------- AnySuccessfulOrThrow ----------------------------
    public static record AnySuccessfulOrThrow<R>(String name, Duration timeout, Optional<R> result, Optional<StructuredTaskScope.FailedException> failedException) {

    }

    public static <R> AnySuccessfulOrThrow<R> anySuccessfulOrThrow(String name, Duration timeout, Callable<R>... callables) {
        try (var scope = StructuredTaskScope.open(Joiner.<R>anySuccessfulResultOrThrow(),
                cf -> {
                    if (name != null && timeout != null) {
                        return cf.withName(name).withTimeout(timeout);
                    }
                    if (timeout != null) {
                        return cf.withTimeout(timeout);
                    }
                    if (name != null) {
                        return cf.withName(name);
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

    //--------------------------- AllSuccessfulOrThrow ----------------------------
    public static record AllSuccessfulOrThrow<R>(String name, Duration timeout, Optional<List<R>> results, Optional<StructuredTaskScope.FailedException> failedException) {

    }

    public static <R> AllSuccessfulOrThrow<List<R>> allSuccessfulOrThrow(String name, Duration timeout, Callable<R>... callables) {
        try (var scope = StructuredTaskScope.open(Joiner.<R>allSuccessfulOrThrow(),
                cf -> {
                    if (name != null && timeout != null) {
                        return cf.withName(name).withTimeout(timeout);
                    }
                    if (timeout != null) {
                        return cf.withTimeout(timeout);
                    }
                    if (name != null) {
                        return cf.withName(name);
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

    //-------------------- throwIfInterruptedException ----------------
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
}
