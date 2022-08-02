import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
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
        queue = SingleChronicleQueueBuilder.fieldlessBinary(basePath).build();
    }

    public void log(Context context, int count) {
        ExcerptAppender appender = queue.acquireAppender();
        appender.writeDocument(w -> w
                .getValueOut().uint16(context.ordinal())
                .getValueOut().uint32(count)
        );

//        appender.writeBytes(w -> w.writeUnsignedShort(context.ordinal())
//                .writeUnsignedInt(count));
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();
        ExcerptTailer tailer = queue.createTailer();
        while (tailer.readDocument(r -> builder
                .append(Context.values()[r.read().uint16()].name())
                .append(':')
                .append(r.read().int32())
                .append('\n'))) ;
//        while (tailer.readBytes(in -> builder
//                .append(Context.values()[in.readUnsignedShort()].name())
//                .append(':')
//                .append(in.readUnsignedInt())
//                .append('\n')));

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
