package dev.rodrigosambade.printers;
import java.util.concurrent.locks.*;
public final class PrinterPool{
 public enum Type{A,B,ANY} private final Lock lock=new ReentrantLock(true);private final Condition available=lock.newCondition();private final int capacityA,capacityB;private int freeA,freeB;
 public PrinterPool(int a,int b){if(a<0||b<0||a+b==0)throw new IllegalArgumentException();capacityA=freeA=a;capacityB=freeB=b;}
 public Lease acquire(Type requested)throws InterruptedException{lock.lockInterruptibly();try{while(!canAcquire(requested))available.await();Type actual=choose(requested);if(actual==Type.A)freeA--;else freeB--;return new Lease(actual);}finally{lock.unlock();}}
 private boolean canAcquire(Type t){return switch(t){case A->freeA>0;case B->freeB>0;case ANY->freeA+freeB>0;};}private Type choose(Type t){if(t!=Type.ANY)return t;return freeA>=freeB&&freeA>0?Type.A:Type.B;}
 public int free(Type t){lock.lock();try{return t==Type.A?freeA:t==Type.B?freeB:freeA+freeB;}finally{lock.unlock();}}
 public final class Lease implements AutoCloseable{private final Type type;private boolean closed;private Lease(Type type){this.type=type;}public Type type(){return type;}@Override public void close(){lock.lock();try{if(closed)return;closed=true;if(type==Type.A){if(++freeA>capacityA)throw new IllegalStateException();}else{if(++freeB>capacityB)throw new IllegalStateException();}available.signalAll();}finally{lock.unlock();}}}
}
