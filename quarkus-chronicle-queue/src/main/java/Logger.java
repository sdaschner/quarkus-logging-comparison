import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@ApplicationScoped
public class Logger {

    private ChronicleQueue queue;

    @PostConstruct
    void init() throws IOException {
        File basePath = Files.createTempDirectory("chronicle-queue").toFile();
        System.out.println("creating queue at " + basePath);
        queue = SingleChronicleQueueBuilder.binary(basePath).build();
    }

    public Appender appender() {
        return new Appender(queue.acquireAppender());
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();
        ExcerptTailer tailer = queue.createTailer();
        while (true) {
            try (DocumentContext dc = tailer.readingDocument()) {
                if (!dc.isPresent())
                    break;
                final Bytes<?> bytes = dc.wire().bytes();
                while (bytes.readRemaining() > 0) {
                    builder
                            .append(Context.values()[bytes.readUnsignedShort()])
                            .append(':')
                            .append(bytes.readInt())
                            .append('\n');
                }
            }
        }
        return builder.toString();
    }

    @PreDestroy
    void close() {
        queue.close();
    }

    public static class Appender implements Closeable {

        private final DocumentContext documentContext;

        private Appender(ExcerptAppender appender) {
            documentContext = appender.writingDocument();
        }

        public void log(Context context, int count) {
            documentContext.wire().bytes()
                    .writeUnsignedShort(context.ordinal())
                    .writeInt(count);
        }

        @Override
        public void close() {
            documentContext.close();
        }
    }

    public enum Context {
        MAIN, TEST, HELLO_METHOD, GOODBYE_METHOD
    }

}
