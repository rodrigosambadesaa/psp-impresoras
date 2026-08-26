package dev.rodrigosambade.printers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrinterPoolTest {

    @Test
    void closingLeaseReturnsPrinterToPool() throws InterruptedException {
        PrinterPool pool = new PrinterPool(1, 1);

        try (PrinterPool.Lease ignored = pool.acquire(PrinterPool.Type.A)) {
            assertEquals(0, pool.free(PrinterPool.Type.A));
        }

        assertEquals(1, pool.free(PrinterPool.Type.A));
    }

    @Test
    void flexibleRequestUsesAnAvailablePrinterType() throws InterruptedException {
        PrinterPool pool = new PrinterPool(0, 1);

        try (PrinterPool.Lease lease = pool.acquire(PrinterPool.Type.ANY)) {
            assertEquals(PrinterPool.Type.B, lease.type());
        }
    }
}
