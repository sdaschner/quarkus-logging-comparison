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
        for (; counter < 1_000; counter++) {
            logger.log(Logger.Event.hello(counter));
        }
        return String.valueOf(counter);
    }

    @GET
    @Path("dump")
    public String dump() {
        return logger.dump();
    }

}
