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
        ExcerptAppender appender = logger.appender();
        try (DocumentContext dc = appender.writingDocument()) {
            final Bytes<?> bytes = dc.wire().bytes();

            for (; counter < 1_000; counter++) {

                bytes.writeUnsignedShort(Logger.Context.HELLO_METHOD.ordinal())
                        .writeInt(counter);
            }

//            logger.log(Logger.Context.HELLO_METHOD, counter);
        }
        return String.valueOf(counter);
    }

    @GET
    @Path("dump")
    public String dump() {
        return logger.dump();
    }

}
