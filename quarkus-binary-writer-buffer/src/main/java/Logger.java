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
        path = Files.createTempFile("quarkus-binary-writer-buffer-log-", ".log");
        channel = FileChannel.open(path, StandardOpenOption.APPEND);
        bb = ByteBuffer.allocate(14 * 4096);
        bb.clear();
    }

    @Lock
    public void log(Context context, int count) {
        try {
            bb.putLong(System.currentTimeMillis());
            bb.putShort((short) context.ordinal());
            bb.putInt(count);

            if (!bb.hasRemaining()) {
                bb.flip();
                channel.write(bb);
                bb.clear();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @PreDestroy
    void close() throws IOException {
        channel.close();
    }

    public enum Context {
        MAIN, TEST, HELLO_METHOD, GOODBYE_METHOD
    }
}
