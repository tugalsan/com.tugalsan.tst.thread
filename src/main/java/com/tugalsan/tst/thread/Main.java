package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.thread.server.TS_ThreadFetchAll;
import com.tugalsan.api.thread.server.TS_ThreadFetchFirst;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Main {

    private static final TS_Log d = TS_Log.of(Main.class);

    //cd C:\me\codes\com.tugalsan\tst\com.tugalsan.tst.thread
    //java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.tst.thread-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... s) {
//        scopeTest();
        threadLocalRandomTest(false);
    }

    public static void threadLocalRandomTest(boolean useThreadLocal) {
//                    TS_ThreadRun.now(() -> TS_RandomUtils.nextFloat(0, 1));
//                    TS_ThreadRun.now(() -> TGS_RandomUtils.nextFloat(0, 1));
        IntStream.range(0, 1_000_000).forEach(i -> {
            if (useThreadLocal) {
                IntStream.range(0, 100).forEach(j -> {
                    ThreadLocalRandom.current().nextFloat(1);
                });
            } else {
                IntStream.range(0, 100).forEach(j -> {
                    new Random().nextFloat(1);
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
                    TGS_UnSafe.catchMeIfUCan(d.className, "Callable", "3");
                    return "3";
                }
        );
        if (true) {
            d.cr("------- TS_ThreadFetchAll.JOIN -------");
            var fetchAll = TS_ThreadFetchAll.of(null, callables);
            fetchAll.resultLst().forEach(result -> d.cr("fetchAll.result", result));
            d.cr("fetchAll.timeout()", fetchAll.timeout());
            fetchAll.exceptionLst().forEach(e -> d.cr("fetchAll.e", e.getMessage()));
            d.cr("fetchAll.exceptionPack()", fetchAll.exceptionPack());
        }
        if (true) {
            d.cr("------- TS_ThreadFetchAll.UNTIL -------");
            var fetchAll = TS_ThreadFetchAll.of(Instant.now().plusSeconds(1), callables);
            fetchAll.resultLst().forEach(result -> d.cr("fetchAll.result", result));
            d.cr("fetchAll.timeout()", fetchAll.timeout());
            fetchAll.exceptionLst().forEach(e -> d.cr("fetchAll.e", e.getMessage()));
            d.cr("fetchAll.exceptionPack()", fetchAll.exceptionPack());
        }
        if (true) {
            d.cr("------- TS_ThreadFetchFirst.JOIN -------");
            var fetchFirst = TS_ThreadFetchFirst.of(null, callables);
            d.cr("fetchFirst.result()", fetchFirst.result());
            d.cr("fetchFirst.timeout()", fetchFirst.timeout());
            d.cr("fetchFirst.exception()", fetchFirst.exception());
            d.cr("fetchFirst.states()", fetchFirst.states());
        }
        if (true) {
            d.cr("------- TS_ThreadFetchFirst.UNTIL -------");
            var fetchFirst = TS_ThreadFetchFirst.of(Instant.now().plusSeconds(1), callables);
            d.cr("fetchFirst.result()", fetchFirst.result());
            d.cr("fetchFirst.timeout()", fetchFirst.timeout());
            d.cr("fetchFirst.exception()", fetchFirst.exception());
            d.cr("fetchFirst.states()", fetchFirst.states());
        }
    }
}
