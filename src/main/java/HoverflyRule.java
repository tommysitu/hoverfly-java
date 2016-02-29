import com.google.common.base.MoreObjects;
import com.google.common.io.Resources;
import org.junit.rules.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;

public class HoverflyRule extends ExternalResource {

    private static final String HOVERFLY_BINARY_PATH = "hoverfly";
    private static final String HOVERFLY_DB_PATH = "requests.db";

    private final URL serviceDataUrl;
    private final URL hoverflyUrl;

    private Process hoverflyProcess;
    private Optional<URL> databaseUrl = Optional.empty();

    public HoverflyRule(final String serviceDataResourceName) {
        serviceDataUrl = getResource(serviceDataResourceName)
                .orElseThrow(() -> new IllegalArgumentException("Service data not found at " + serviceDataResourceName));
        hoverflyUrl = getResource(HOVERFLY_BINARY_PATH).get();
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "8500");
    }

    @Override
    protected void before() throws Throwable {

        tearDownDatabaseIfExists();

        final Path pathToHoverfly = Paths.get(hoverflyUrl.toURI());
        final Path directoryOfBinary = pathToHoverfly.getParent();
        final Path binaryName = pathToHoverfly.getFileName();

        final ProcessBuilder builder = new ProcessBuilder()
                .inheritIO()
                .directory(directoryOfBinary.toFile())
                .command("./" + binaryName.toString(), "-import", serviceDataUrl.getPath());

        hoverflyProcess = builder.start();
    }

    @Override
    protected void after() {
        hoverflyProcess.destroy();
        if(databaseUrl.isPresent()) {
            try {
                tearDownDatabaseIfExists();
            } catch (IOException e) {
                throw new RuntimeException("Unable to delete hoveryfly database", e);
            }
        }
    }

    private void tearDownDatabaseIfExists() throws IOException {
        databaseUrl = getResource(HOVERFLY_DB_PATH);

        if(databaseUrl.isPresent()) {
            Files.delete(Paths.get(databaseUrl.get().getPath()));
        }
    }

    private static Optional<URL> getResource(String resourceName) {
        ClassLoader loader = MoreObjects.firstNonNull(
                Thread.currentThread().getContextClassLoader(),
                Resources.class.getClassLoader());
        return Optional.ofNullable(loader.getResource(resourceName));
    }
}
