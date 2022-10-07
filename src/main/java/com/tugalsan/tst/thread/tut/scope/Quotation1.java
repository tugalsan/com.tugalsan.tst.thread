package com.tugalsan.tst.thread.tut.scope;

import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.random.server.TS_RandomUtils;
import com.tugalsan.api.thread.server.TS_ThreadSafeLst;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import com.tugalsan.api.thread.server.TS_ThreadExceptionPck;
import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import jdk.incubator.concurrent.StructuredTaskScope;

//https://www.youtube.com/watch?v=_fRN7tpLyPk
//1:59
public record Quotation1(String agency, int quotation) implements PageComponent {

    final private static TS_Log d = TS_Log.of(Quotation1.class);

    private static class QuotationScope extends StructuredTaskScope<Quotation1> {

        private final TS_ThreadSafeLst<Quotation1> quotations = new TS_ThreadSafeLst();
        private final TS_ThreadSafeLst<Throwable> exceptions = new TS_ThreadSafeLst();

        @Override
        protected void handleComplete(Future<Quotation1> future) {
            switch (future.state()) {
                case RUNNING ->
                    throw new IllegalStateException("State should not be running!");
                case SUCCESS ->
                    this.quotations.add(future.resultNow());
                case FAILED ->
                    this.exceptions.add(future.exceptionNow());
                case CANCELLED -> {
                }
            }
        }

        public TS_ThreadExceptionPck exceptions() {
            return new TS_ThreadExceptionPck(exceptions, null);
        }

        public Quotation1 quotation() {
            return quotations.stream()
                    .min(Comparator.comparing(Quotation1::quotation))
                    .orElseThrow(() -> exceptions());
        }

        public QuotationScope joinUntil(Instant deadline) throws InterruptedException {
            try {
                super.joinUntil(deadline);
            } catch (TimeoutException e) {
                super.shutdown();
            }
            return this;
        }
    }

    public static Quotation1 readQuotation() throws InterruptedException {
        try ( var scope = new QuotationScope()) {
            Future<Quotation1> quotationA = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(30, 120);
                return new Quotation1("QA-A", TS_RandomUtils.nextInt(80, 110));
            });
            Future<Quotation1> quotationB = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(20, 110);
                return new Quotation1("QA-B", TS_RandomUtils.nextInt(90, 120));
            });
            Future<Quotation1> quotationC = scope.fork(() -> {
                TS_ThreadWait.millisecondsBtw(10, 130);
                return new Quotation1("QA-C", TS_RandomUtils.nextInt(70, 130));
            });

//            scope.join();
            scope.joinUntil(Instant.now().plusMillis(10));

            //scope.exceptions();
            return scope.quotation();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
