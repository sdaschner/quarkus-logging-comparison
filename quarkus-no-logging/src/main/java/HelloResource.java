import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("hello")
public class HelloResource {

    @GET
    public String hello() {
        int counter = 0;
        for (; counter < 1_000; counter++) {
            // ... I know this doesn't make much sense :)
        }
        return String.valueOf(counter);
    }

}
