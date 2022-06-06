import org.jboss.logging.Logger;

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
        for (int i = 0; i < 1_000; i++) {
            logger.infov("invoked /hello: {0}", i);
        }
        return "Hello world";
    }

}
