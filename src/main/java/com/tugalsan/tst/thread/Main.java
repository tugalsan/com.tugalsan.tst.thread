package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.random.client.*;
import com.tugalsan.api.random.server.*;
import com.tugalsan.api.thread.server.async.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import com.tugalsan.api.unsafe.client.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Main {

    private static final TS_Log d = TS_Log.of(true, Main.class);

    //cd C:\me\codes\com.tugalsan\tst\com.tugalsan.tst.thread
    //java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.tst.thread-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... s) {
        scopeTest();
//        threadLocalRandomTest();
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
    public static void threadLocalRandomTest() {
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
        TS_ThreadWait.seconds(null, 10);
    }

    public static void scopeTest() {
        List<Callable<String>> callables = List.of(
                () -> {
                    d.cr("fetcing...", "1");
                    TS_ThreadWait.seconds(null, 10);
                    d.cr("completed", "1");
                    return "1";
                },
                () -> {
                    d.cr("fetcing...", "2");
                    TS_ThreadWait.seconds(null, 2);
                    d.cr("completed", "2");
                    return "2";
                },
                () -> {
                    d.cr("fetcing...", "3");
                    TS_ThreadWait.seconds(null, 3);
                    d.cr("completed", "3");
                    TGS_UnSafe.thrw(d.className, "Callable", "3");
                    return "3";
                }
        );

        if (false) {
            d.cr("------- parallel.FOREVER -------");
            var fetchAll = TS_ThreadAsyncAwait.callParallel(null, callables);
            fetchAll.resultsForSuccessfulOnes.forEach(result -> d.cr("fetchAll.resultsForSuccessfulOnes", result));
            d.cr("fetchAll.timeout()", fetchAll.timeout());
            fetchAll.exceptions.forEach(e -> d.cr("fetchAll.e", e.getMessage()));
        }

        if (false) {
            d.cr("------- parallel.TIMED -------");
            var fetchAll = TS_ThreadAsyncAwait.callParallel(Duration.ofSeconds(1), callables);
            fetchAll.resultsForSuccessfulOnes.forEach(result -> d.cr("fetchAll.resultsForSuccessfulOnes", result));
            d.cr("fetchAll.timeout()", fetchAll.timeout());
            fetchAll.exceptions.forEach(e -> d.cr("fetchAll.e", e.getMessage()));
        }

        if (false) {
            d.cr("------- parallelUntilFirstSuccess.FOREVER -------");
            var fetchFirst = TS_ThreadAsyncAwait.callParallelUntilFirstSuccess(null, callables);
            d.cr("fetchFirst.resultIfAnySuccessful()", fetchFirst.resultIfAnySuccessful);
            d.cr("fetchFirst.timeout()", fetchFirst.timeout());
            fetchFirst.exceptions.forEach(e -> d.cr("fetchFirst.e", e.getMessage()));
            d.cr("fetchFirst.states()", fetchFirst.states);
        }

        if (false) {
            d.cr("------- parallelUntilFirstSuccess.TIMED -------");
            var fetchFirst = TS_ThreadAsyncAwait.callParallelUntilFirstSuccess(Duration.ofSeconds(1), callables);
            d.cr("fetchFirst.resultIfAnySuccessful()", fetchFirst.resultIfAnySuccessful);
            d.cr("fetchFirst.timeout()", fetchFirst.timeout());
            fetchFirst.exceptions.forEach(e -> d.cr("fetchFirst.e", e.getMessage()));
            d.cr("fetchFirst.states()", fetchFirst.states);
        }

        if (false) {
            d.cr("------- parallelUntilFirstFail.FOREVER -------");
            var fetchFail = TS_ThreadAsyncAwait.callParallelUntilFirstFail(null, callables);
            d.cr("fetchFail.resultsForSuccessfulOnes()", fetchFail.resultsForSuccessfulOnes);
            d.cr("fetchFail.timeout()", fetchFail.timeout());
            fetchFail.exceptions.forEach(e -> d.cr("fetchFail.e", e.getMessage()));
            d.cr("fetchFail.states()", fetchFail.states);
        }

        if (false) {
            d.cr("------- parallelUntilFirstFail.TIMED -------");
            var fetchFail = TS_ThreadAsyncAwait.callParallelUntilFirstFail(Duration.ofSeconds(1), callables);
            d.cr("fetchFail.resultsForSuccessfulOnes()", fetchFail.resultsForSuccessfulOnes);
            d.cr("fetchFail.timeout()", fetchFail.timeout());
            fetchFail.exceptions.forEach(e -> d.cr("fetchFail.e", e.getMessage()));
            d.cr("fetchFail.states()", fetchFail.states);
        }

        if (true) {
            Callable<String> callableBlocking = () -> {
                d.ci("fetchFail.callableBlocking", "begin");
                while (true) {
                    d.ci("fetchFail.callableBlocking", "while");
                    TS_ThreadWait.of(Duration.ofSeconds(1));
                }
//                d.ci("fetchFail.callableBlocking", "never ends");
//                return "4";
            };
            d.cr("------- single.TIMED.BLOCKING -------");
            var fetchFail = TS_ThreadAsyncAwait.callSingle(Duration.ofSeconds(1), callableBlocking);
            d.cr("fetchFail.resultsForSuccessfulOnes()", fetchFail.resultsForSuccessfulOnes);
            d.cr("fetchFail.timeout()", fetchFail.timeout());
            fetchFail.exceptions.forEach(e -> d.cr("fetchFail.e", e.getMessage()));
            d.cr("fetchFail.states()", fetchFail.states);
        }

    }
}
