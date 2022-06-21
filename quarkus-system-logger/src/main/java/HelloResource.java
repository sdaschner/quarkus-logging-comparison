import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static java.lang.System.Logger.Level.INFO;

@ApplicationScoped
@Path("hello")
public class HelloResource {

    private final System.Logger logger = System.getLogger("HelloResource");

    @GET
    public String hello() {
        int counter = 0;
        for (; counter < 1_000; counter++) {
            logger.log(INFO, "invoked /hello: {0}", counter);
        }
        return String.valueOf(counter);
    }

}
