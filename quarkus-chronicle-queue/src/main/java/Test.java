import java.io.IOException;

/*
    creating queue at /tmp/chronicle-queue16448526988663616166
    Wrote 10,000,000 in 1.049 seconds.
    Wrote 10,000,000 in 0.902 seconds.
    Wrote 10,000,000 in 0.889 seconds.
    Wrote 10,000,000 in 0.890 seconds.
    Wrote 10,000,000 in 0.889 seconds.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        Logger logger = new Logger();
        logger.init();
        HelloResource hr = new HelloResource();
        hr.logger = logger;

        for (int t = 0; t < 5; t++) {
            long start = System.currentTimeMillis();
            int count = 10_000_000;
            for (int i = 0; i < count; i += 1000) {
                String wrote = hr.hello();
                System.out.println("Wrote: " + wrote);
            }
            long time = System.currentTimeMillis() - start;
            System.out.printf("Wrote %,d in %.3f seconds.%n", count, time / 1e3);
        }

        logger.close();
    }

}
