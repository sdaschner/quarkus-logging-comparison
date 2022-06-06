import io.quarkus.arc.Lock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class Logger {

    private BufferedWriter writer;

    @PostConstruct
    void init() throws IOException {
        Path path = Files.createTempFile("quarkus-log-", ".log");
        writer = new BufferedWriter(new FileWriter(path.toFile()));
    }

    @PreDestroy
    void close() throws IOException {
        writer.close();
    }

    @Lock
    public void log(String message) {
        try {
            writer.append(message);
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Lock
    public void log(String... parts) {
        try {
            for (String part : parts) {
                writer.append(part);
            }
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
