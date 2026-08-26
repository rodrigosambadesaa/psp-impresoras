# PSP · Gestión concurrente de impresoras

Reimplementación del ejercicio `Impresoras`. Modela impresoras de tipo A y B, trabajos que exigen un tipo concreto y trabajos flexibles. La sincronización se concentra en `PrinterPool` mediante `ReentrantLock` y `Condition`; la cesión se representa con un `Lease` `AutoCloseable`, de modo que una impresora siempre vuelve al pool incluso ante excepciones.

```bash
mvn verify
java -cp target/classes dev.rodrigosambade.printers.PrinterDemo
```
