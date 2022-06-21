import io.quarkus.arc.Lock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class Logger {

    private PrintWriter writer;

    @PostConstruct
    void init() throws IOException {
        Path path = Files.createTempFile("quarkus-log-", ".log");
        writer = new PrintWriter(new FileWriter(path.toFile()), true);
    }

    @PreDestroy
    void close() {
        writer.close();
    }

    @Lock
    public void log(String message) {
        writer.println(message);
    }

    @Lock
    public void log(String... parts) {
        for (String part : parts)
            writer.print(part);
        writer.println();
    }

}
