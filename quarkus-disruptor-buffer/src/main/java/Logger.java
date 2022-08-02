import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Logger {

    private Disruptor<Event> disruptor;
    private RingBuffer<Event> ringBuffer;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    JournalConsumer journalConsumer;

    @PostConstruct
    void init() {
        int bufferSize = 8192;

        disruptor = new Disruptor<>(Event::new, bufferSize, managedExecutor);
        disruptor.handleEventsWith(journalConsumer);
        disruptor.start();

        ringBuffer = disruptor.getRingBuffer();
    }

    public void log(Context context, int count) {
        long sequence = ringBuffer.next();

        Event event = ringBuffer.get(sequence);
        event.context = context;
        event.count = count;

        ringBuffer.publish(sequence);
    }

    @PreDestroy
    void close() {
        disruptor.shutdown();
    }

    public static class Event {

        public Context context;
        public int count;

    }

    public enum Context {
        MAIN, TEST, HELLO_METHOD, GOODBYE_METHOD
    }
}
