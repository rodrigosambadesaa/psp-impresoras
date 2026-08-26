package dev.rodrigosambade.printers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PrinterDemo {

    private static final long PRINT_TIME_MS = 100;

    private PrinterDemo() {
    }

    public static void main(String[] args) {
        PrinterPool pool = new PrinterPool(3, 2);
        List<PrinterPool.Type> requests = List.of(
                PrinterPool.Type.A,
                PrinterPool.Type.B,
                PrinterPool.Type.ANY,
                PrinterPool.Type.ANY,
                PrinterPool.Type.A,
                PrinterPool.Type.B);

        try (ExecutorService executor = Executors.newFixedThreadPool(8)) {
            for (PrinterPool.Type requestedType : requests) {
                executor.submit(() -> printDocument(pool, requestedType));
            }
        }
    }

    private static void printDocument(
            PrinterPool pool,
            PrinterPool.Type requestedType) {
        try (PrinterPool.Lease lease = pool.acquire(requestedType)) {
            System.out.printf(
                    "%s usa impresora %s%n",
                    Thread.currentThread().getName(),
                    lease.type());
            Thread.sleep(PRINT_TIME_MS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
