package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import jdk.incubator.concurrent.StructuredTaskScope;

//https://www.youtube.com/watch?v=_fRN7tpLyPk
//1:45
public record Weather1(String agency, String weather) {

    final private static TS_Log d = TS_Log.of(Weather1.class);

    public static Weather1 readWeather() throws InterruptedException {
        try ( var scope = new StructuredTaskScope.ShutdownOnSuccess<Weather1>()) {
            Future<Weather1> weatherA = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(30, 110);
                return new Weather1("WA-A", "Sunny");
            });
            Future<Weather1> weatherB = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(40, 90);
                return new Weather1("WA-B", "Rainy");
            });
            Future<Weather1> weatherC = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(20, 100);
                return new Weather1("WA-C", "Snowy");
            });

//            scope.join();
            try {
                scope.joinUntil(Instant.now().plusMillis(100));
            } catch (TimeoutException ex) {
                return new Weather1("", "Donno");
            }

            //RETURNS EX: SUCCESS FAILED FAILED
            d.cr("weatherX.states", weatherA.state(), weatherB.state(), weatherC.state());

            return scope.result();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
