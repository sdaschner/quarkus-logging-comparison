import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/*
creating queue at /tmp/chronicle-queue16448526988663616166
Wrote 10,000,000 in 1.049 seconds.
Wrote 10,000,000 in 0.902 seconds.
Wrote 10,000,000 in 0.889 seconds.
Wrote 10,000,000 in 0.890 seconds.
Wrote 10,000,000 in 0.889 seconds.
 */
@ApplicationScoped
public class Logger {

    private ChronicleQueue queue;

    public static void main(String[] args) throws IOException {
        Logger logger = new Logger();
        logger.init();
        HelloResource hr = new HelloResource();
        hr.logger = logger;
        for (int t = 0; t < 5; t++) {
            long start = System.currentTimeMillis();
            int count = 10_000_000;
            for (int i = 0; i < count; i += 1000) {
                String wrote = hr.hello();
//                System.out.println("Wrote: " + wrote);
            }
            long time = System.currentTimeMillis() - start;
            System.out.printf("Wrote %,d in %.3f seconds.%n", count, time / 1e3);
        }
        logger.close();
    }

    @PostConstruct
    void init() throws IOException {
        File basePath = Files.createTempDirectory("chronicle-queue").toFile();
        System.out.println("creating queue at " + basePath);
        queue = SingleChronicleQueueBuilder.binary(basePath).build();
    }

    public void log(Context context, int count) {
        ExcerptAppender appender = queue.acquireAppender();

        try (DocumentContext dc = appender.writingDocument()) {
            final Bytes<?> bytes = dc.wire().bytes();
            bytes.writeUnsignedShort(context.ordinal())
                    .writeInt(count);
        }
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();
        ExcerptTailer tailer = queue.createTailer();
        while (true) {
            try (DocumentContext dc = tailer.readingDocument()) {
                if (!dc.isPresent())
                    break;
                final Bytes<?> bytes = dc.wire().bytes();
                builder
                        .append(Context.values()[bytes.readUnsignedShort()])
                        .append(':')
                        .append(bytes.readInt())
                        .append('\n');

            }
        }
        return builder.toString();
    }

    @PreDestroy
    void close() {
        queue.close();
    }

    public enum Context {
        MAIN, TEST, HELLO_METHOD, GOODBYE_METHOD
    }
}
