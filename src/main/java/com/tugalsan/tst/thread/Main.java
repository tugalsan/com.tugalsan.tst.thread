package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.thread.server.TS_ThreadFetchAll;
import com.tugalsan.api.thread.server.TS_ThreadFetchFirst;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import java.util.List;
import java.util.concurrent.Callable;

public class Main {

    private static final TS_Log d = TS_Log.of(Main.class);

    //java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.tst.thread-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... s) {
        List<Callable<String>> callables = List.of(
                () -> {
                    d.cr("fetcing...", "1");
                    TS_ThreadWait.seconds(null, 1);
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
                    return "3";
                }
        );
        TS_ThreadFetchAll<String> fetchAll = TS_ThreadFetchAll.of(
                /*Instant.now().plusSeconds(5)*/null,
                () -> {
                    d.cr("fetchAll.fetcing...", "1");
                    TS_ThreadWait.seconds(null, 1);
                    d.cr("fetchAll.completed", "1");
                    return "1";
                },
                () -> {
                    d.cr("fetchAll.fetcing...", "2");
                    TS_ThreadWait.seconds(null, 2);
                    d.cr("fetchAll.completed", "2");
                    return "2";
                },
                () -> {
                    d.cr("fetchAll.fetcing...", "3");
                    TS_ThreadWait.seconds(null, 3);
                    d.cr("fetchAll.completed", "3");
                    return "3";
                }
        );
        fetchAll.resultLst().forEach(result -> d.cr("result", result));
        TS_ThreadFetchFirst<String> fetchFirst = TS_ThreadFetchFirst.of(
                /*Instant.now().plusSeconds(5)*/null,
                () -> {
                    d.cr("fetchFirst.fetcing...", "1");
                    TS_ThreadWait.seconds(null, 1);
                    d.cr("fetchFirst.completed", "1");
                    return "1";
                },
                () -> {
                    d.cr("fetchFirst.fetcing...", "2");
                    TS_ThreadWait.seconds(null, 2);
                    d.cr("fetchFirst.completed", "2");
                    return "2";
                },
                () -> {
                    d.cr("fetchFirst.fetcing...", "3");
                    TS_ThreadWait.seconds(null, 3);
                    d.cr("fetchFirst.completed", "3");
                    return "3";
                }
        );
        d.cr("fetchFirst.result()", fetchFirst.result());
        TS_ThreadWait.seconds(null, 10);
    }
}
