package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.thread.server.TS_ThreadFetchAll;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import java.time.Instant;

public class Main {

    private static TS_Log d = TS_Log.of(Main.class);

    //java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.tst.thread-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... s) {
        TS_ThreadFetchAll<String> f = TS_ThreadFetchAll.of(Instant.now().plusSeconds(5),
                () -> {
                    d.cr("fetcing...", "1");//WHY NOT WORKING?
                    TS_ThreadWait.seconds(null, 1);
                    d.cr("completed", "1");//WHY NOT WORKING?
                    return "1";
                },
                () -> {
                    d.cr("fetcing...", "2");//WHY NOT WORKING?
                    TS_ThreadWait.seconds(null, 2);
                    d.cr("completed", "2");//WHY NOT WORKING?
                    return "2";
                },
                () -> {
                    d.cr("fetcing...", "3");//WHY NOT WORKING?
                    TS_ThreadWait.seconds(null, 3);
                    d.cr("completed", "3");//WHY NOT WORKING?
                    return "3";
                }
        );
        f.resultLst().forEach(result -> d.cr("result", result));//WHY NOT WORKING?
    }
}
