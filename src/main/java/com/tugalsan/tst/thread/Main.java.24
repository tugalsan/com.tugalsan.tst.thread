package com.tugalsan.tst.thread;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_OutTyped_In1;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwait;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.random.client.*;
import com.tugalsan.api.random.server.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncWait;
import com.tugalsan.api.thread.server.async.scheduled.TS_ThreadAsyncScheduled;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwaitSingle;
import com.tugalsan.api.time.client.TGS_Time;
import static java.lang.System.out;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.*;

public class Main {

    private static final TS_Log d = TS_Log.of(true, Main.class);

    //cd C:\me\codes\com.tugalsan\tst\com.tugalsan.tst.thread
    //java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.tst.thread-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... s) {
        TS_ThreadSyncTrigger killTrigger = TS_ThreadSyncTrigger.of("main");
//        scopeTest_ShutdownOnFailure_waitWithException(killTrigger);
        scopeTest_ShutdownOnFailure(killTrigger);
//        scopeTest(killTrigger);
//        threadLocalRandomTest(killTrigger);
//        untilTest(killTrigger);
//        nestedTest_pureJava(
//                Duration.ofSeconds(80000),
//                Duration.ofSeconds(0),
//                1_000_000
//        );
//        nestedTest_onRequestReceivedFromAServlet();
//        {
//            var t = nestedTest_legacyCode(
//                    killTrigger,
//                    Duration.ofSeconds(20),
//                    Duration.ofSeconds(1),
//                    3,//TRY 0 or 3
//                    4_000
//            );
//            if (t.hasError()) {
//                d.ce("main.nestedTest_legacyCode", t.elapsed.getSeconds(), "timeout?", t.timeout(), t.exceptionIfFailed.get());
//            } else {
//                d.cr("main.nestedTest_legacyCode", t.elapsed.getSeconds(), t.resultIfSuccessful.isPresent() ? t.resultIfSuccessful.get() : "result is void");
//            }
//        }
//        if (!scheduledTest_everyMinutes_whenSecondShow(killTrigger)) {
//            return;
//        }
//        if (!scheduledTest_everyHours_whenMinuteShow(killTrigger)) {
//            return;
//        }

//        untilTest(killTrigger);
        d.cr("main", "done..");
        d.cr("main", "waiting..");
        TS_ThreadSyncWait.hours("", killTrigger, 5);
    }

    private static void scopeTest_ShutdownOnFailure(TS_ThreadSyncTrigger killTrigger) {
        Callable<String> callableBlocking = () -> {
            TS_ThreadSyncWait.seconds(d.className, killTrigger, 4);
            if (true) {
                throw new RuntimeException("hah");
            }
            while (true) {
                d.cr("scopeTest_ShutdownOnFailure", "tick", System.currentTimeMillis());
                TS_ThreadSyncWait.seconds(d.className, killTrigger, 1);
            }
        };
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(callableBlocking);
//            scope.joinUntil(Instant.now().plusSeconds(1));
            scope.join();
            scope.throwIfFailed();
            d.cr("scopeTest_ShutdownOnFailure", "result", future.get());
        } catch (InterruptedException | ExecutionException/*| TimeoutException*/ e) {
//            if (e instanceof TimeoutException) {
//                d.cr("scopeTest_ShutdownOnFailure", "timeout", "shutdown triggered");
//                return;
//            }
            d.ct("scopeTest_ShutdownOnFailure", e);
        }

    }

    private static boolean scheduledTest_everyMinutes_whenSecondShow(TS_ThreadSyncTrigger killTrigger) {
        TS_ThreadAsyncScheduled.everyMinutes_whenSecondShow(killTrigger.newChild("scheduledTest_everyMinutes_whenSecondShow"), Duration.ofSeconds(10), true, 1, 30, kt -> {
            d.cr("scheduledTest_everyMinutes_whenSecondShow", "periodic tick", TGS_Time.of().toString());
        });
        return true;
    }

    private static boolean scheduledTest_everyHours_whenMinuteShow(TS_ThreadSyncTrigger killTrigger) {
        TS_ThreadAsyncScheduled.everyHours_whenMinuteShow(killTrigger.newChild("scheduledTest_everyHours_whenMinuteShow"), Duration.ofSeconds(10), true, 1, 30, kt -> {
            d.cr("scheduledTest_everyHours_whenMinuteShow", "periodic tick", TGS_Time.of().toString());
        });
        return true;
    }

    private record Union<T>(boolean timeout, Throwable error, T result) {

    }

    private static void nestedTest_onRequestReceivedFromAServlet() {
        var durTimeoutServlet = Duration.ofSeconds(8);
        var scope = new StructuredTaskScope.ShutdownOnFailure();
        try {
            var subTask = scope.fork(() -> {
                return nestedTest_onRequestReceivedFromAServlet_fetchFromUrl();
            });
            scope.joinUntil(Instant.now().plusSeconds(durTimeoutServlet.getSeconds()));
            scope.throwIfFailed();
            if (subTask.state() == StructuredTaskScope.Subtask.State.FAILED) {
                System.out.println("subTask.exception(): " + subTask.exception().getMessage());
                return;
            }
            System.out.println("subTask.get(): " + subTask.get());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            if (e instanceof TimeoutException) {
                scope.shutdown();
                System.out.println("Error: timeout");
            } else {
                e.printStackTrace();
            }
        } finally {
            scope.close();
        }
    }

    private static Union<String> nestedTest_onRequestReceivedFromAServlet_fetchFromUrl() {
        var durTimeoutFetchFromUrl = Duration.ofSeconds(5);
        var durWorkloadFetchFromUrl = Duration.ofSeconds(1);
        var scope = new StructuredTaskScope.ShutdownOnFailure();
        try {
            var subTask = scope.fork(() -> {
                var dbData1 = nestedTest_onRequestReceivedFromAServlet_fetchFromUrl_fetchDataFromDB().toString();
                var dbData2 = nestedTest_onRequestReceivedFromAServlet_fetchFromUrl_fetchDataFromDB().toString();
                String downloadedTextAccroding2DbData;
                {//downloading file...
                    Thread.sleep(durWorkloadFetchFromUrl);
                    downloadedTextAccroding2DbData = dbData1 + dbData2;
                }
                return downloadedTextAccroding2DbData;
            });
            scope.joinUntil(Instant.now().plusSeconds(durTimeoutFetchFromUrl.getSeconds()));
            scope.throwIfFailed();
            if (subTask.state() == StructuredTaskScope.Subtask.State.FAILED) {
                return new Union(false, subTask.exception(), null);
            }
            return new Union(false, null, subTask.get());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            if (e instanceof TimeoutException) {
                scope.shutdown();
                return new Union(true, e, null);
            }
            return TGS_FuncMTUUtils.thrw(e);
        } finally {
            scope.close();
        }
    }

    private static Union<String> nestedTest_onRequestReceivedFromAServlet_fetchFromUrl_fetchDataFromDB() {
        var durTimeoutfetchFromDb = Duration.ofSeconds(5);
        var durWorkloadfetchFromDb = Duration.ofSeconds(10);
        var scope = new StructuredTaskScope.ShutdownOnFailure();
        try {
            var subTask = scope.fork(() -> {
                Thread.sleep(durWorkloadfetchFromDb);
                return String.valueOf(System.currentTimeMillis());
            });
            scope.joinUntil(Instant.now().plusSeconds(durTimeoutfetchFromDb.getSeconds()));
            scope.throwIfFailed();
            if (subTask.state() == StructuredTaskScope.Subtask.State.FAILED) {
                return new Union(false, subTask.exception(), null);
            }
            return new Union(false, null, subTask.get());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            if (e instanceof TimeoutException) {
                scope.shutdown();
                return new Union(true, e, null);
            }
            return TGS_FuncMTUUtils.thrw(e);
        } finally {
            scope.close();
        }
    }

    private static void nestedTest_pureJava(Duration untilTimeout, Duration workLoad, int nestedId) {
        if (nestedId < 0) {
            out.println("nestedTest_pureJava -> skip -> " + nestedId);
            return;
        }
//        out.println("nestedTest_pureJava -> begin -> " + nestedId);
        var scope = new StructuredTaskScope.ShutdownOnFailure();
        try {
            scope.fork(() -> {
                Thread.sleep(workLoad);
                nestedTest_pureJava(untilTimeout, workLoad, nestedId - 1);
                return null;
            });
            scope.joinUntil(Instant.now().plusSeconds(untilTimeout.getSeconds()));
            scope.throwIfFailed();

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            if (e instanceof TimeoutException) {
                scope.shutdown();
            }
            TGS_FuncMTUUtils.thrw(e);
        } finally {
            scope.close();
        }
        out.println("nestedTest_pureJava -> end -> " + nestedId);
    }

    private static TS_ThreadAsyncAwaitSingle<Void> nestedTest_legacyCode(TS_ThreadSyncTrigger killTrigger, Duration untilTimeout, Duration workLoad, int stepSeconds, int nestedId) {
        d.cr("nestedTest_legacyCode", nestedId, "begin");
        if (nestedId < 0) {
            return null;
        }
        var t = TS_ThreadAsyncAwait.runUntil(killTrigger.newChild("nestedTest_legacyCode"), untilTimeout, kt -> {
            TS_ThreadSyncWait.of("nestedTest_legacyCode_" + nestedId, killTrigger, workLoad);
            var f_untilTimeout = untilTimeout.minusSeconds(stepSeconds);
            if (f_untilTimeout.isZero() || f_untilTimeout.isNegative()) {
                f_untilTimeout = Duration.ofSeconds(1);
            }
            d.ce("nestedTest_legacyCode", nestedId, "until", f_untilTimeout.getSeconds());
            var _t = nestedTest_legacyCode(killTrigger, f_untilTimeout, workLoad, stepSeconds, nestedId - 1);
            if (_t == null) {
                d.cr("nestedTest_legacyCode", nestedId, "_t==null", nestedId - 1);
                return;
            } else {
                if (_t.hasError()) {
                    d.ce("main.nestedTest_legacyCode", nestedId, _t.elapsed.getSeconds(), "timeout?", _t.timeout(), _t.exceptionIfFailed.get());
//                    Thread.currentThread().interrupt();
                } else {
                    d.cr("main.nestedTest_legacyCode", nestedId, _t.elapsed.getSeconds(), _t.resultIfSuccessful.isPresent() ? _t.resultIfSuccessful.get() : "result is void");
                }
            }
        });
        d.cr("nestedTest", nestedId, "end");
        return t;
    }

    private static void untilTest(TS_ThreadSyncTrigger killTrigger) {
        d.cr("untilTest", "step0");
        TS_ThreadAsyncAwait.runUntil(killTrigger.newChild("untilTest"), Duration.ofSeconds(3), kt -> {
            while (true) {
                TS_ThreadSyncWait.seconds("runUntil", killTrigger, 1);
                d.cr("untilTest", "runUntil", System.currentTimeMillis());
            }
        });
        d.cr("untilTest", "step1");
        TS_ThreadAsyncAwait.callSingle(killTrigger.newChild("untilTest"), Duration.ofSeconds(3), kt -> {
            while (true) {
                TS_ThreadSyncWait.seconds("callSingle", killTrigger, 1);
                d.cr("untilTest", "callSingle", System.currentTimeMillis());
            }
        });
        d.cr("untilTest", "step2");
    }

    /*
    TODO: PLAY THINGS...
    To generate a random int in the range [0, 1_000]:
    int n = new SplittableRandom().nextInt(0, 1_001);

    To generate a random int[100] array of values in the range [0, 1_000]:
    int[] a = new SplittableRandom().ints(100, 0, 1_001).parallel().toArray();

    To return a Stream of random values:
    IntStream stream = new SplittableRandom().ints(100, 0, 1_001);
    
    SecureRandom rand = new SecureRandom();
     */
    private static void threadLocalRandomTest(TS_ThreadSyncTrigger killTrigger) {
        enum TestType {
            useNewThreadLocalRandom, useNewRandom,
            ReUseThreadLocal, ReUseRandom,
            ReUseGlobalRandom,
            customThreadLocalRandom, customRandom
        }
        var testType = TestType.customRandom;
        var rg = new Random();
        IntStream.range(0, 1_000_000).forEach(i -> {
            if (testType == TestType.useNewThreadLocalRandom) {//63mb->60mb
                IntStream.range(0, 100).forEach(j -> {
                    ThreadLocalRandom.current().nextFloat(1);
                });
            }
            if (testType == TestType.useNewRandom) {//326mb->323mb
                IntStream.range(0, 100).forEach(j -> {
                    new Random().nextFloat(1);
                });
            }
            if (testType == TestType.ReUseThreadLocal) {//65mb->61mb
                var r = ThreadLocalRandom.current();
                IntStream.range(0, 100).forEach(j -> {
                    r.nextFloat(1);
                });
            }
            if (testType == TestType.ReUseRandom) {//111mb->108mb
                var r = new Random();
                IntStream.range(0, 100).forEach(j -> {
                    r.nextFloat(1);
                });
            }
            if (testType == TestType.ReUseGlobalRandom) {//64mb->61mb
                IntStream.range(0, 100).forEach(j -> {
                    rg.nextFloat(1);
                });
            }
            if (testType == TestType.customThreadLocalRandom) {//63mb->60mb
                IntStream.range(0, 100).forEach(j -> {
                    TS_RandomUtils.nextFloat(0, 1);
                });
            }
            if (testType == TestType.customRandom) {//63mb->60mb
                IntStream.range(0, 100).forEach(j -> {
                    TGS_RandomUtils.nextFloat(0, 1);
                });
            }
        });
        TS_ThreadSyncWait.seconds(d.className, killTrigger, 10);
    }

    private static void scopeTest_ShutdownOnFailure_waitWithException(TS_ThreadSyncTrigger killTrigger) {
        Consumer<String> log = msg -> System.out.println("scopeTest_ShutdownOnFailure_waitWithException -> " + msg);
        Consumer<Duration> wait = duration -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                TGS_FuncMTUUtils.thrw(e);
            }
        };
        Callable<String> callableBlocking = () -> {
            log.accept("callableBlocking.begin");
            while (true) {
                log.accept("callableBlocking.while");
                wait.accept(Duration.ofSeconds(1));
            }
//  log.accept("fetchFail.callableBlocking.neverEnds");
//  return "never returns";
        };
        var scope = new StructuredTaskScope.ShutdownOnFailure();
        try {
            var future = scope.fork(callableBlocking);
            scope.joinUntil(Instant.now().plusSeconds(1));
            scope.throwIfFailed();
            log.accept("result: " + future.get());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            log.accept(e.getClass().getName() + ":" + e.getMessage());
            if (e instanceof TimeoutException) {
                log.accept("INFO: shutdown triggered");
                scope.shutdown();
            }
        } finally {
            scope.close();
        }

    }

    private static void scopeTest(TS_ThreadSyncTrigger killTrigger) {

        List<TGS_FuncMTU_OutTyped_In1<String, TS_ThreadSyncTrigger>> callables = List.of(kt -> {
            d.cr("fetcing...", "1");
            IntStream.range(0, 15).forEachOrdered(i -> {
                d.cr("fetcing...", "1", "tick");
                TS_ThreadSyncWait.seconds(d.className, killTrigger, 1);
            });
            d.cr("completed", "1");
            return "1";
        },
                kt -> {
                    d.cr("fetcing...", "2");
                    IntStream.range(0, 2).forEachOrdered(i -> {
                        d.cr("fetcing...", "2", "tick");
                        TS_ThreadSyncWait.seconds(d.className, killTrigger, 1);
                    });
                    d.cr("completed", "2");
                    return "2";
                },
                kt -> {
                    d.cr("fetcing...", "3");
                    IntStream.range(0, 3).forEachOrdered(i -> {
                        d.cr("fetcing...", "3", "tick");
                        TS_ThreadSyncWait.seconds(d.className, killTrigger, 1);
                    });
                    d.cr("completed", "3");
                    throw new RuntimeException("Callable 3");
                }
        );

        if (true) {
            d.cr("------- parallel.FOREVER -------");
            var fetchAll = TS_ThreadAsyncAwait.callParallel(killTrigger.newChild("scopeTest"), null, callables);
            fetchAll.resultsForSuccessfulOnes.forEach(result -> d.cr("fetchAll.resultsForSuccessfulOnes", result));
            d.cr("fetchAll.timeout()", fetchAll.timeout());
            fetchAll.exceptions.forEach(e -> d.cr("fetchAll.e", e.getMessage()));
        }

        if (true) {
            d.cr("------- parallel.TIMED -------");
            var fetchAll = TS_ThreadAsyncAwait.callParallel(killTrigger.newChild("scopeTest"), Duration.ofSeconds(1), callables);
            fetchAll.resultsForSuccessfulOnes.forEach(result -> d.cr("fetchAll.resultsForSuccessfulOnes", result));
            d.cr("fetchAll.timeout()", fetchAll.timeout());
            fetchAll.exceptions.forEach(e -> d.cr("fetchAll.e", e.getMessage()));
        }

        if (true) {
            d.cr("------- parallelUntilFirstSuccess.FOREVER -------");
            var fetchFirst = TS_ThreadAsyncAwait.callParallelUntilFirstSuccess(killTrigger.newChild("scopeTest"), null, callables);
            d.cr("fetchFirst.resultIfAnySuccessful()", fetchFirst.resultIfAnySuccessful);
            d.cr("fetchFirst.timeout()", fetchFirst.timeout());
            fetchFirst.exceptions.forEach(e -> d.cr("fetchFirst.e", e.getMessage()));
            d.cr("fetchFirst.states()", fetchFirst.states);
        }

        if (true) {
            d.cr("------- parallelUntilFirstSuccess.TIMED -------");
            var fetchFirst = TS_ThreadAsyncAwait.callParallelUntilFirstSuccess(killTrigger.newChild("scopeTest"), Duration.ofSeconds(1), callables);
            d.cr("fetchFirst.resultIfAnySuccessful()", fetchFirst.resultIfAnySuccessful);
            d.cr("fetchFirst.timeout()", fetchFirst.timeout());
            fetchFirst.exceptions.forEach(e -> d.cr("fetchFirst.e", e.getMessage()));
            d.cr("fetchFirst.states()", fetchFirst.states);
        }

        if (true) {
            d.cr("------- parallelUntilFirstFail.FOREVER -------");
            var fetchFail = TS_ThreadAsyncAwait.callParallelUntilFirstFail(killTrigger.newChild("scopeTest"), null, callables);
            d.cr("fetchFail.resultsForSuccessfulOnes()", fetchFail.resultsForSuccessfulOnes);
            d.cr("fetchFail.timeout()", fetchFail.timeout());
            fetchFail.exceptions.forEach(e -> d.cr("fetchFail.e", e.getMessage()));
            d.cr("fetchFail.states()", fetchFail.states);
        }

        if (true) {//HATA
            d.cr("------- parallelUntilFirstFail.TIMED -------");
            var fetchFail = TS_ThreadAsyncAwait.callParallelUntilFirstFail(killTrigger.newChild("scopeTest"), Duration.ofSeconds(1), callables);
            d.cr("fetchFail.resultsForSuccessfulOnes()", fetchFail.resultsForSuccessfulOnes);
            d.cr("fetchFail.timeout()", fetchFail.timeout());
            fetchFail.exceptions.forEach(e -> d.cr("fetchFail.e", e.getMessage()));
            d.cr("fetchFail.states()", fetchFail.states);
        }
        if (true) {
            d.cr("------- callSingle.TIMED -------");
            var fetchFail = TS_ThreadAsyncAwait.callSingle(killTrigger.newChild("scopeTest"), Duration.ofSeconds(5), callables.get(0));
            d.cr("fetchFail.resultIfSuccessful", fetchFail.resultIfSuccessful);
            d.cr("fetchFail.timeout()", fetchFail.timeout());
            fetchFail.exceptionIfFailed.stream().forEach(e -> d.cr("fetchFail.e", e.getMessage()));
        }
        if (true) {
            d.cr("------- everySeconds.killTriggered -------");
            TS_ThreadAsyncScheduled.everySeconds(killTrigger.newChild("scopeTest"), Duration.ofHours(1), true, 5, kt -> d.ce("everySeconds", "tick"));
            TS_ThreadSyncWait.seconds("everySeconds", killTrigger, 5);
            d.cr("scopeTest", "killTrigger.trigger();");
            killTrigger.trigger("testme");
        }
    }
}
