package dev.rodrigosambade.printers;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class PrinterPool {

    public enum Type {
        A,
        B,
        ANY
    }

    private final Lock lock = new ReentrantLock(true);
    private final Condition available = lock.newCondition();

    private final int capacityA;
    private final int capacityB;

    private int freeA;
    private int freeB;

    public PrinterPool(int capacityA, int capacityB) {
        if (capacityA < 0 || capacityB < 0 || capacityA + capacityB == 0) {
            throw new IllegalArgumentException("At least one printer is required");
        }

        this.capacityA = capacityA;
        this.capacityB = capacityB;
        this.freeA = capacityA;
        this.freeB = capacityB;
    }

    public Lease acquire(Type requestedType) throws InterruptedException {
        Objects.requireNonNull(requestedType, "requestedType");

        lock.lockInterruptibly();
        try {
            while (!canAcquire(requestedType)) {
                available.await();
            }

            Type actualType = chooseType(requestedType);
            reserve(actualType);
            return new Lease(actualType);
        } finally {
            lock.unlock();
        }
    }

    public int free(Type type) {
        Objects.requireNonNull(type, "type");

        lock.lock();
        try {
            return switch (type) {
                case A -> freeA;
                case B -> freeB;
                case ANY -> freeA + freeB;
            };
        } finally {
            lock.unlock();
        }
    }

    private boolean canAcquire(Type type) {
        return switch (type) {
            case A -> freeA > 0;
            case B -> freeB > 0;
            case ANY -> freeA + freeB > 0;
        };
    }

    private Type chooseType(Type requestedType) {
        if (requestedType != Type.ANY) {
            return requestedType;
        }

        if (freeA >= freeB && freeA > 0) {
            return Type.A;
        }
        return Type.B;
    }

    private void reserve(Type type) {
        if (type == Type.A) {
            freeA--;
        } else {
            freeB--;
        }
    }

    private void release(Type type) {
        if (type == Type.A) {
            freeA++;
            if (freeA > capacityA) {
                throw new IllegalStateException("Too many type A printers released");
            }
        } else {
            freeB++;
            if (freeB > capacityB) {
                throw new IllegalStateException("Too many type B printers released");
            }
        }
    }

    public final class Lease implements AutoCloseable {

        private final Type type;
        private boolean closed;

        private Lease(Type type) {
            this.type = type;
        }

        public Type type() {
            return type;
        }

        @Override
        public void close() {
            lock.lock();
            try {
                if (closed) {
                    return;
                }

                closed = true;
                release(type);
                available.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
