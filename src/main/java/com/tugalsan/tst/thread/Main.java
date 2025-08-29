package com.tugalsan.tst.thread;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_OutTyped_In1;
import com.tugalsan.api.thread.server.async.await.core.TS_ThreadAsyncAwaitCore;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncWait;
import java.time.Duration;
import java.util.List;

/*

main.begin..
AllAwait[killTrigger=TS_ThreadSyncTrigger{name=main>allAwait_success, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@37bba400, value=false}, timeout=PT10S, timeoutException=Optional.empty, resultsFailedOrUnavailable=[], resultsSuccessful=[task 5 secs string finished, task 6 secs string finished]]
AllAwait[killTrigger=TS_ThreadSyncTrigger{name=main>allAwait_timeout1, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@37f8bb67, value=true}, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], resultsFailedOrUnavailable=[], resultsSuccessful=[]]
AllAwait[killTrigger=TS_ThreadSyncTrigger{name=main>allAwait_throw, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@49c2faae, value=false}, timeout=PT10S, timeoutException=Optional.empty, resultsFailedOrUnavailable=[java.util.concurrent.StructuredTaskScopeImpl$SubtaskImpl@31cefde0[Failed: java.lang.RuntimeException: task 3 secs string throwing]], resultsSuccessful=[task 5 secs string finished]]
AllAwait[killTrigger=TS_ThreadSyncTrigger{name=main>allAwait_timeout2, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@439f5b3d, value=true}, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], resultsFailedOrUnavailable=[], resultsSuccessful=[]]
AnySuccessfulOrThrow[killTrigger=TS_ThreadSyncTrigger{name=main>anySuccessfulOrThrow_success, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@26a1ab54, value=false}, timeout=PT10S, timeoutException=Optional.empty, failedException=Optional.empty, result=Optional[task 5 secs string finished]]
AnySuccessfulOrThrow[killTrigger=TS_ThreadSyncTrigger{name=main>anySuccessfulOrThrow_timeout1, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@3d646c37, value=true}, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], failedException=Optional.empty, result=Optional.empty]
AnySuccessfulOrThrow[killTrigger=TS_ThreadSyncTrigger{name=main>anySuccessfulOrThrow_throw, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@41cf53f9, value=false}, timeout=PT10S, timeoutException=Optional.empty, failedException=Optional.empty, result=Optional[task 5 secs string finished]]
AnySuccessfulOrThrow[killTrigger=TS_ThreadSyncTrigger{name=main>anySuccessfulOrThrow_timeout2, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@5a10411, value=true}, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], failedException=Optional.empty, result=Optional.empty]
AllSuccessfulOrThrow[killTrigger=TS_ThreadSyncTrigger{name=main>allSuccessfulOrThrow_success, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@5ce65a89, value=false}, timeout=PT10S, timeoutException=Optional.empty, failedException=Optional.empty, results=[task 5 secs string finished, task 6 secs string finished]]
AllSuccessfulOrThrow[killTrigger=TS_ThreadSyncTrigger{name=main>allSuccessfulOrThrow_timeout1, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@25f38edc, value=true}, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], failedException=Optional.empty, results=[]]
AllSuccessfulOrThrow[killTrigger=TS_ThreadSyncTrigger{name=main>allSuccessfulOrThrow_throw, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@3eb07fd3, value=true}, timeout=PT10S, timeoutException=Optional.empty, failedException=Optional[java.util.concurrent.StructuredTaskScope$FailedException: java.lang.RuntimeException: task 3 secs string throwing], results=[]]
AllSuccessfulOrThrow[killTrigger=TS_ThreadSyncTrigger{name=main>allSuccessfulOrThrow_timeout2, parents=com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst@506c589e, value=true}, timeout=PT2S, timeoutException=Optional[java.util.concurrent.StructuredTaskScope$TimeoutException], failedException=Optional.empty, results=[]]
main.done..

 */
public class Main {

//    private static final TS_Log d = TS_Log.of(true, Main.class);
    //cd C:\me\codes\com.tugalsan\tst\com.tugalsan.tst.thread
    //java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.tst.thread-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... s) {
        var killTrigger = TS_ThreadSyncTrigger.of("main");
        TGS_FuncMTU_OutTyped_In1<String, TS_ThreadSyncTrigger> threeSecsStringThrowingTask = kt -> {
            TS_ThreadSyncWait.seconds("wait", kt, 3);
            throw new RuntimeException("task 3 secs string throwing");
        };       
        TGS_FuncMTU_OutTyped_In1<String, TS_ThreadSyncTrigger> fiveSecsStringTask = kt -> {
            TS_ThreadSyncWait.seconds("wait", kt, 5);
            return "task 5 secs string finished";
        };
        TGS_FuncMTU_OutTyped_In1<String, TS_ThreadSyncTrigger> sixSecsStringTask = kt -> {
            TS_ThreadSyncWait.seconds("wait", kt, 6);
            return "task 6 secs string finished";
        };
        IO.println("main.begin..");

        IO.println(TS_ThreadAsyncAwaitCore.allAwait(killTrigger.newChild("allAwait_success"), Duration.ofSeconds(10), List.of(fiveSecsStringTask, sixSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.allAwait(killTrigger.newChild("allAwait_timeout1"), Duration.ofSeconds(2), List.of(fiveSecsStringTask, sixSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.allAwait(killTrigger.newChild("allAwait_throw"), Duration.ofSeconds(10), List.of(threeSecsStringThrowingTask, fiveSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.allAwait(killTrigger.newChild("allAwait_timeout2"), Duration.ofSeconds(2), List.of(threeSecsStringThrowingTask, fiveSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.anySuccessfulOrThrow(killTrigger.newChild("anySuccessfulOrThrow_success"), Duration.ofSeconds(10), List.of(fiveSecsStringTask, sixSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.anySuccessfulOrThrow(killTrigger.newChild("anySuccessfulOrThrow_timeout1"), Duration.ofSeconds(2), List.of(fiveSecsStringTask, sixSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.anySuccessfulOrThrow(killTrigger.newChild("anySuccessfulOrThrow_throw"), Duration.ofSeconds(10), List.of(threeSecsStringThrowingTask, fiveSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.anySuccessfulOrThrow(killTrigger.newChild("anySuccessfulOrThrow_timeout2"), Duration.ofSeconds(2), List.of(threeSecsStringThrowingTask, fiveSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.allSuccessfulOrThrow(killTrigger.newChild("allSuccessfulOrThrow_success"), Duration.ofSeconds(10), List.of(fiveSecsStringTask, sixSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.allSuccessfulOrThrow(killTrigger.newChild("allSuccessfulOrThrow_timeout1"), Duration.ofSeconds(2), List.of(fiveSecsStringTask, sixSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.allSuccessfulOrThrow(killTrigger.newChild("allSuccessfulOrThrow_throw"), Duration.ofSeconds(10), List.of(threeSecsStringThrowingTask, fiveSecsStringTask)));
        IO.println(TS_ThreadAsyncAwaitCore.allSuccessfulOrThrow(killTrigger.newChild("allSuccessfulOrThrow_timeout2"), Duration.ofSeconds(2), List.of(threeSecsStringThrowingTask, fiveSecsStringTask)));
        IO.println("main.done..");
    }
}
