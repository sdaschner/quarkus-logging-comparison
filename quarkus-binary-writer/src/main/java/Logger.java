import io.quarkus.arc.Lock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@ApplicationScoped
public class Logger {

    private FileChannel channel;
    private ByteBuffer bb;
    private Path path;

    @PostConstruct
    void init() throws IOException {
        path = Files.createTempFile("quarkus-binary-writer-", ".log");
        channel = FileChannel.open(path, StandardOpenOption.APPEND);
        bb = ByteBuffer.allocate(6);
    }

    @Lock
    public void log(Context context, int count) {
        try {
            bb.clear();
            bb.putShort((short) context.ordinal());
            bb.putInt(count);
            bb.flip();
            channel.write(bb);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @PreDestroy
    void close() throws IOException {
        channel.close();
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();

        try {
            byte[] bytes = Files.readAllBytes(path);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            while (buffer.hasRemaining()) {
                builder
                        .append(Context.values()[buffer.getShort()].name())
                        .append(':')
                        .append(buffer.getInt())
                        .append('\n');
            }

            return builder.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public enum Context {
        MAIN, TEST, HELLO_METHOD, GOODBYE_METHOD;
    }

}
