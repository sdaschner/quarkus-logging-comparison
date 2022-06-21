package com.sebastian_daschner.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("hello")
public class HelloResource {

    private static final Logger logger = LogManager.getLogger(HelloResource.class);

    @GET
    public String hello() {
        int counter = 0;
        for (; counter < 1_000; counter++) {
            logger.info("invoked /hello: {}", counter);
        }
        return String.valueOf(counter);
    }

}
