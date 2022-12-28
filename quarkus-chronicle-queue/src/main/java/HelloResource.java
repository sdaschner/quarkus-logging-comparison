import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.wire.DocumentContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("hello")
public class HelloResource {

    @Inject
    Logger logger;

    @GET
    public String hello() {
        int counter = 0;

        try (Logger.Appender appender = logger.appender()) {
            for (; counter < 1_000; counter++) {
                appender.log(Logger.Context.HELLO_METHOD, counter);
            }
        }
        return String.valueOf(counter);
    }

    @GET
    @Path("dump")
    public String dump() {
        return logger.dump();
    }

}
