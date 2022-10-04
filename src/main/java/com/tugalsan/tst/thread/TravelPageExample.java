package com.tugalsan.tst.thread;

import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.thread.server.TS_ThreadExceptionPck;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.time.Instant;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import jdk.incubator.concurrent.StructuredTaskScope;

//https://www.youtube.com/watch?v=_fRN7tpLyPk
//2:28
public class TravelPageExample {

    final private static TS_Log d = TS_Log.of(TravelPageExample.class);

    private static record TravelPage(Weather2 weather, Quotation1 quotation) {

    }

    private static class TravelPageScope extends StructuredTaskScope<PageComponent> {

        private volatile Quotation1 quotation;
        private volatile TS_ThreadExceptionPck exception;
        private volatile Weather2 weather = Weather2.UNKNOWN;

        @Override
        protected void handleComplete(Future<PageComponent> future) {
            switch (future.state()) {
                case RUNNING ->
                    throw new IllegalStateException("State should not be running!");
                case SUCCESS -> {
                    switch (future.resultNow()) {
                        case Quotation1 quotation ->
                            this.quotation = quotation;
                        case Weather2 weather ->
                            this.weather = weather;
                    }
                }
                case FAILED -> {
                    switch (future.exceptionNow()) {
                        case TS_ThreadExceptionPck exception ->
                            this.exception = exception;
                        case Throwable t ->
                            throw new RuntimeException(t);
                    }
                }

            }
        }

        public TravelPage travelPage() {
            if (quotation != null) {
                return new TravelPage(weather, quotation);
            } else {
                throw this.exception;
            }
        }

        public TravelPageScope joinUntil(Instant deadline) throws InterruptedException {
            try {
                super.joinUntil(deadline);
            } catch (TimeoutException e) {
                super.shutdown();
            }
            return this;
        }
    }

    public static void main(String... args) {
        TGS_UnSafe.execute(() -> {
            try ( var scope = new TravelPageScope()) {
                scope.fork(() -> Weather2.readWeather());
                scope.fork(() -> Quotation1.readQuotation());
                scope.joinUntil(Instant.now().plusMillis(100));
                d.cr("travelPage", scope.travelPage());
            }
        });
    }
}
