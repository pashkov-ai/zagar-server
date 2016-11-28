package replication;

import main.ApplicationContext;
import network.ClientConnections;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by xakep666 on 26.11.16.
 * <p>
 * Replicates a file content to client (NOT FOR PRODUCTION!!! ONLY FOR TESTS)
 */
public class SimpleJsonReplicator implements Replicator {
    private static URL jsonFileUrl = SimpleJsonReplicator.class
            .getClassLoader()
            .getResource("testreplic.json");
    private static String json;

    static {
        try {
            json = new String(Files.readAllBytes(Paths.get(jsonFileUrl.getFile())), Charset.defaultCharset());
        } catch (IOException e) {
            json = null;
        }
    }

    @Override
    public void replicate() {
        if (json == null) return;
        try {
            ApplicationContext.instance().get(ClientConnections.class)
                    .getConnections()
                    .forEach((entry) -> {
                        try {
                            entry.getValue().getRemote().sendString(json);
                        } catch (IOException ignored) {

                        }
                    });
        } catch (Exception ignored) {

        }
    }
}