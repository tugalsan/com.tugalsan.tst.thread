package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.thread.server.TS_ThreadFetchAll;
import com.tugalsan.api.thread.server.TS_ThreadFetchFirst;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;

public class Main {

    private static final TS_Log d = TS_Log.of(Main.class);

    //cd C:\me\codes\com.tugalsan\tst\com.tugalsan.tst.thread
    //java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.tst.thread-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... s) {
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
            var fetchAll = TS_ThreadFetchAll.of(null, callables);
            fetchAll.resultLst().forEach(result -> d.cr("fetchAll.result", result));
            d.cr("fetchAll.timeout()", fetchAll.timeout());
            fetchAll.exceptionLst().forEach(e -> d.cr("fetchAll.e", e.getMessage()));
        }
        if (true) {
            var fetchAll = TS_ThreadFetchAll.of(Instant.now().plusSeconds(1), callables);
            fetchAll.resultLst().forEach(result -> d.cr("fetchAll.result", result));
            d.cr("fetchAll.timeout()", fetchAll.timeout());
            fetchAll.exceptionLst().forEach(e -> d.cr("fetchAll.e", e.getMessage()));
        }
        if (true) {
            var fetchFirst = TS_ThreadFetchFirst.of(null, callables);
            d.cr("fetchFirst.result()", fetchFirst.result());
            d.cr("fetchFirst.timeout()", fetchFirst.timeout());
            d.cr("fetchFirst.exception()", fetchFirst.exception());
        }
        if (true) {
            var fetchFirst = TS_ThreadFetchFirst.of(Instant.now().plusSeconds(1), callables);
            d.cr("fetchFirst.result()", fetchFirst.result());
            d.cr("fetchFirst.timeout()", fetchFirst.timeout());
            d.cr("fetchFirst.exception()", fetchFirst.exception());
        }
    }
}
