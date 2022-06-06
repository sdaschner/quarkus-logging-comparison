package com.sebastian_daschner.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("hello")
public class HelloResource {

    private static Logger logger = LogManager.getLogger(HelloResource.class);

    @GET
    public String hello() {
        for (int i = 0; i < 1_000; i++) {
            logger.info("invoked /hello: {}", i);
        }
        return "Hello world";
    }

}
