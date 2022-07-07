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

//    public void log(Event event) {
    public void log(Context context, int count) {
        ExcerptAppender appender = queue.acquireAppender();
//        appender.writeDocument(w -> w
//                .getValueOut().uint16(event.context.ordinal())
//                .getValueOut().uint32(event.count)
//        );

        appender.writeBytes(w -> w.writeUnsignedShort(context.ordinal())
                .writeUnsignedInt(count));
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();
        ExcerptTailer tailer = queue.createTailer();
        while (tailer.readBytes(in -> builder
                .append(Context.values()[in.readUnsignedShort()].name())
                .append(':')
                .append(in.readUnsignedInt())
                .append('\n')));

        return builder.toString();
    }

    @PreDestroy
    void close() {
        queue.close();
    }

    public static class Event {

        public final Context context;
        public final int count;

        public Event(Context context, int count) {
            this.context = context;
            this.count = count;
        }

        public static Event hello(int count) {
            return new Event(Context.HELLO_METHOD, count);
        }
    }

    public enum Context {
        MAIN, TEST, HELLO_METHOD, GOODBYE_METHOD
    }
}
