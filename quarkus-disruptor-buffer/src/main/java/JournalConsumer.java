import com.lmax.disruptor.EventHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@ApplicationScoped
public class JournalConsumer implements EventHandler<Logger.Event> {

    private FileChannel channel;
    private ByteBuffer bb;

    @PostConstruct
    void init() throws IOException {
        channel = FileChannel.open(Files.createTempFile("quarkus-disruptor-buffer-log-", ".log"), StandardOpenOption.APPEND);
        bb = ByteBuffer.allocate(6 * 4096);
    }

    @Override
    public void onEvent(Logger.Event event, long sequence, boolean endOfBatch) throws IOException {
        bb.putShort((short) event.context.ordinal());
        bb.putInt(event.count);

        if (!bb.hasRemaining()) {
            bb.flip();
            channel.write(bb);
            bb.clear();
        }
    }

    @PreDestroy
    void close() throws IOException {
        channel.close();
    }

}
