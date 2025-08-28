package com.tugalsan.tst.thread;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

/*

main.begin..
AllAwait[name=allAwait_success, timeout=PT10S, timeoutException=Optional.empty, resultsSuccessful=[task 5 secs string finished, task 6 secs string finished], resultsFailedOrUnavailable=[]]
AllAwait[name=allAwait_timeout1, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], resultsSuccessful=[], resultsFailedOrUnavailable=[]]
AllAwait[name=allAwait_throw, timeout=PT10S, timeoutException=Optional.empty, resultsSuccessful=[task 5 secs string finished], resultsFailedOrUnavailable=[java.util.concurrent.StructuredTaskScopeImpl$SubtaskImpl@378bf509[Failed: java.lang.RuntimeException: task 3 secs string throwing]]]
AllAwait[name=allAwait_timeout2, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], resultsSuccessful=[], resultsFailedOrUnavailable=[]]
AllAwaitNoType[name=allAwaitNoType_success, timeout=PT10S, timeoutException=Optional.empty, resultsFailed=[], resultsSuccessful=[4, task 6 secs string finished]]
AllAwaitNoType[name=allAwaitNoType_timeout1, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], resultsFailed=[], resultsSuccessful=[]]
AllAwaitNoType[name=allAwaitNoType_throw, timeout=PT10S, timeoutException=Optional.empty, resultsFailed=[java.util.concurrent.StructuredTaskScopeImpl$SubtaskImpl@15aeb7ab[Failed: java.lang.RuntimeException: task 3 secs string throwing]], resultsSuccessful=[4]]
AllAwaitNoType[name=allAwaitNoType_timeout2, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], resultsFailed=[], resultsSuccessful=[]]
AnySuccessfulOrThrow[name=anySuccessfulOrThrow_success, timeout=PT10S, timeoutException=Optional.empty, failedException=Optional.empty, result=Optional[task 5 secs string finished]]
AnySuccessfulOrThrow[name=anySuccessfulOrThrow_timeout1, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], failedException=Optional.empty, result=Optional.empty]
AnySuccessfulOrThrow[name=anySuccessfulOrThrow_throw, timeout=PT10S, timeoutException=Optional.empty, failedException=Optional.empty, result=Optional[task 5 secs string finished]]
AnySuccessfulOrThrow[name=anySuccessfulOrThrow_timeout2, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], failedException=Optional.empty, result=Optional.empty]
AllSuccessfulOrThrow[name=allSuccessfulOrThrow_success, timeout=PT10S, timeoutException=Optional.empty, failedException=Optional.empty, results=[task 5 secs string finished, task 6 secs string finished]]
AllSuccessfulOrThrow[name=allSuccessfulOrThrow_timeout1, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], failedException=Optional.empty, results=[]]
AllSuccessfulOrThrow[name=allSuccessfulOrThrow_throw, timeout=PT10S, timeoutException=Optional.empty, failedException=Optional[java.util.concurrent.StructuredTaskScope$FailedException: java.lang.RuntimeException: task 3 secs string throwing], results=[]]
AllSuccessfulOrThrow[name=allSuccessfulOrThrow_timeout2, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], failedException=Optional.empty, results=[]]
main.done..

*/

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
        IO.println(anySuccessfulOrThrow("anySuccessfulOrThrow_success", Duration.ofSeconds(10), fiveSecsStringTask, sixSecsStringTask));
        IO.println(anySuccessfulOrThrow("anySuccessfulOrThrow_timeout1", Duration.ofSeconds(2), fiveSecsStringTask, sixSecsStringTask));
        IO.println(anySuccessfulOrThrow("anySuccessfulOrThrow_throw", Duration.ofSeconds(10), threeSecsStringThrowingTask, fiveSecsStringTask));
        IO.println(anySuccessfulOrThrow("anySuccessfulOrThrow_timeout2", Duration.ofSeconds(2), threeSecsStringThrowingTask, fiveSecsStringTask));
        IO.println(allSuccessfulOrThrow("allSuccessfulOrThrow_success", Duration.ofSeconds(10), fiveSecsStringTask, sixSecsStringTask));
        IO.println(allSuccessfulOrThrow("allSuccessfulOrThrow_timeout1", Duration.ofSeconds(2), fiveSecsStringTask, sixSecsStringTask));
        IO.println(allSuccessfulOrThrow("allSuccessfulOrThrow_throw", Duration.ofSeconds(10), threeSecsStringThrowingTask, fiveSecsStringTask));
        IO.println(allSuccessfulOrThrow("allSuccessfulOrThrow_timeout2", Duration.ofSeconds(2), threeSecsStringThrowingTask, fiveSecsStringTask));
        IO.println("main.done..");
    }

    //--------------------------- AllAwait ----------------------------
    public static record AllAwait<R>(String name, Duration timeout, Optional<StructuredTaskScope.TimeoutException> timeoutException, List<R> resultsSuccessful, List<StructuredTaskScope.Subtask<R>> resultsFailedOrUnavailable) {

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
            return new AllAwait(name, timeout, Optional.empty(), resultsSuccessful, resultsFailedOrUnavailable);
        } catch (InterruptedException | StructuredTaskScope.TimeoutException e) {
            if (e instanceof StructuredTaskScope.TimeoutException et) {
                return new AllAwait(name, timeout, Optional.of(et), List.of(), List.of());
            }
            return throwIfInterruptedException(e);
        }
    }

    //--------------------------- AllAwaitNoType ----------------------------
    public static record AllAwaitNoType(String name, Duration timeout, Optional<StructuredTaskScope.TimeoutException> timeoutException, List<StructuredTaskScope.Subtask> resultsFailed, List resultsSuccessful) {

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
            return new AllAwaitNoType(name, timeout, Optional.empty(), resultsFailed, resultsSuccessful);
        } catch (InterruptedException | StructuredTaskScope.TimeoutException e) {
            if (e instanceof StructuredTaskScope.TimeoutException et) {
                return new AllAwaitNoType(name, timeout, Optional.of(et), List.of(), List.of());
            }
            return throwIfInterruptedException(e);
        }
    }

    //--------------------------- AnySuccessfulOrThrow ----------------------------
    public static record AnySuccessfulOrThrow<R>(String name, Duration timeout, Optional<StructuredTaskScope.TimeoutException> timeoutException, Optional<StructuredTaskScope.FailedException> failedException, Optional<R> result) {

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
            return new AnySuccessfulOrThrow(name, timeout, Optional.empty(), Optional.empty(), Optional.of(scope.join()));
        } catch (InterruptedException | StructuredTaskScope.TimeoutException | StructuredTaskScope.FailedException e) {
            if (e instanceof StructuredTaskScope.TimeoutException et) {
                return new AnySuccessfulOrThrow(name, timeout, Optional.of(et), Optional.empty(), Optional.empty());
            }
            if (e instanceof StructuredTaskScope.FailedException ef) {
                return new AnySuccessfulOrThrow(name, timeout, Optional.empty(), Optional.of(ef), Optional.empty());
            }
            return throwIfInterruptedException(e);
        }
    }

    //--------------------------- AllSuccessfulOrThrow ----------------------------
    public static record AllSuccessfulOrThrow<R>(String name, Duration timeout, Optional<StructuredTaskScope.TimeoutException> timeoutException, Optional<StructuredTaskScope.FailedException> failedException, List<R> results) {

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
            return new AllSuccessfulOrThrow(name, timeout, Optional.empty(), Optional.empty(), scope.join().map(StructuredTaskScope.Subtask::get).toList());
        } catch (InterruptedException | StructuredTaskScope.TimeoutException | StructuredTaskScope.FailedException e) {
            throwIfInterruptedException(e);
            if (e instanceof StructuredTaskScope.TimeoutException et) {
                return new AllSuccessfulOrThrow(name, timeout, Optional.of(et), Optional.empty(), List.of());
            }
            if (e instanceof StructuredTaskScope.FailedException ef) {
                return new AllSuccessfulOrThrow(name, timeout, Optional.empty(), Optional.of(ef), List.of());
            }
            return throwIfInterruptedException(e);
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
