package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import jdk.incubator.concurrent.StructuredTaskScope;

//https://www.youtube.com/watch?v=_fRN7tpLyPk
//1:53
public record Weather2(String agency, String weather) implements PageComponent {

    public static final Weather2 UNKNOWN = new Weather2("Unknown", "Unknown");
    final private static TS_Log d = TS_Log.of(Weather2.class);

    private static class WeatherScope implements AutoCloseable {

        private StructuredTaskScope.ShutdownOnSuccess<Weather2> scope = new StructuredTaskScope.ShutdownOnSuccess();
        private volatile boolean timeout = false;

        public WeatherScope joinUntil(Instant deadline) throws InterruptedException {
            try {
                scope.joinUntil(deadline);
            } catch (TimeoutException e) {
                scope.shutdown();
                timeout = true;
            }
            return this;
        }

        public Future<Weather2> fork(Callable<? extends Weather2> task) {
            return scope.fork(task);
        }

        public void shutdown() {
            scope.shutdown();
        }

        @Override
        public void close() {
            scope.close();
        }

        public Weather2 weather() throws ExecutionException {
            if (!timeout) {
                return scope.result();
            }
            return UNKNOWN;
        }
    }

    public static Weather2 readWeather() throws InterruptedException {
        try ( var scope = new WeatherScope()) {
            Future<Weather2> weatherA = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(30, 110);
                return new Weather2("WA-A", "Sunny");
            });
            Future<Weather2> weatherB = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(40, 90);
                return new Weather2("WA-B", "Rainy");
            });
            Future<Weather2> weatherC = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(20, 100);
                return new Weather2("WA-C", "Snowy");
            });

            scope.joinUntil(Instant.now().plusMillis(100));

            //RETURNS EX: SUCCESS FAILED FAILED
            d.cr("weatherX.states", weatherA.state(), weatherB.state(), weatherC.state());

            return scope.weather();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
