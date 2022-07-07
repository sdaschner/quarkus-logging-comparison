package com.sebastian_daschner.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("hello")
public class HelloResource {

    private static final Logger logger = LoggerFactory.getLogger(HelloResource.class);

    @GET
    public String hello() {
        int counter = 0;
        for (; counter < 1_000; counter++) {
            logger.info("invoked /hello: {}", counter);
        }
        return String.valueOf(counter);
    }

}
