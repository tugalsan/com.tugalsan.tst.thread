package com.tugalsan.tst.thread;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_OutTyped_In1;
import com.tugalsan.api.thread.server.async.await.core.TS_ThreadAsyncAwaitCore;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncWait;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

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
