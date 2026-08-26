package dev.rodrigosambade.printers;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class PrinterPoolTest{@Test void leaseReturnsPrinter()throws Exception{var p=new PrinterPool(1,1);try(var l=p.acquire(PrinterPool.Type.A)){assertEquals(0,p.free(PrinterPool.Type.A));}assertEquals(1,p.free(PrinterPool.Type.A));}@Test void flexibleGetsAvailableType()throws Exception{var p=new PrinterPool(0,1);try(var l=p.acquire(PrinterPool.Type.ANY)){assertEquals(PrinterPool.Type.B,l.type());}}}
