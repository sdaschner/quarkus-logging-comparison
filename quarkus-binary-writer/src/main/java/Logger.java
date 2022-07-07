import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class Logger {

    private BufferedOutputStream outputStream;
    private Path path;

    @PostConstruct
    void init() throws IOException {
        path = Files.createTempFile("quarkus-log-", ".log");
        outputStream = new BufferedOutputStream(new FileOutputStream(path.toFile()));
    }

    public void log(Event event) {
        try {
            outputStream.write(toArray(event));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static byte[] toArray(Event event) {
        int ordinal = event.context.ordinal();
        return new byte[]{
                (byte) (ordinal >> 8),
                (byte) ordinal,
                (byte) (event.count >> 24),
                (byte) (event.count >> 16),
                (byte) (event.count >> 8),
                (byte) event.count};
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();

        try {
            byte[] bytes = Files.readAllBytes(path);
            ByteBuffer buffer = ByteBuffer.allocate(6);

            for (int i = 0; i < bytes.length; i += 6) {
                buffer.put(bytes, i, 6).rewind();
                builder
                        .append(Context.values()[buffer.getShort()].name())
                        .append(':')
                        .append(buffer.getInt())
                        .append('\n');
                buffer.rewind();
            }

            return builder.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @PreDestroy
    void close() throws IOException {
        outputStream.close();
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
