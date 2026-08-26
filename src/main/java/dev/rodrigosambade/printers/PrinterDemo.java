package dev.rodrigosambade.printers;
import java.util.List;import java.util.concurrent.*;
public final class PrinterDemo{public static void main(String[]args)throws Exception{var pool=new PrinterPool(3,2);try(var ex=Executors.newFixedThreadPool(8)){for(var type:List.of(PrinterPool.Type.A,PrinterPool.Type.B,PrinterPool.Type.ANY,PrinterPool.Type.ANY,PrinterPool.Type.A,PrinterPool.Type.B))ex.submit(()->{try(var lease=pool.acquire(type)){System.out.println(Thread.currentThread()+" usa "+lease.type());Thread.sleep(100);}catch(InterruptedException e){Thread.currentThread().interrupt();}});}}}
