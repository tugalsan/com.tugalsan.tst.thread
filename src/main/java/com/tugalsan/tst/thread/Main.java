package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.thread.server.TS_ThreadFetchAll;
import com.tugalsan.api.thread.server.TS_ThreadFetchFirst;
import com.tugalsan.api.thread.server.TS_ThreadWait;
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
                    return "3";
                }
        );
        if (false) {
            TS_ThreadFetchAll.of(
                    /*Instant.now().plusSeconds(5)*/null,
                    callables
            ).resultLst().forEach(result -> d.cr("result", result));
        }
        if (true) {
            d.cr("fetchFirst.result()",
                    TS_ThreadFetchFirst.of(
                            /*Instant.now().plusSeconds(5)*/null,
                            callables
                    ).result()
            );
        }
    }
}
