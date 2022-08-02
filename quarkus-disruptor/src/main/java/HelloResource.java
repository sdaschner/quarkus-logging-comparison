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
            logger.log(Logger.Context.HELLO_METHOD, counter);
        }
        return String.valueOf(counter);
    }

}
