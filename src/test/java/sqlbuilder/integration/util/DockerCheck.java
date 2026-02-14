package sqlbuilder.integration.util;

import org.testcontainers.DockerClientFactory;

public class DockerCheck {
    private static Boolean dockerAvailable = null;

    public static boolean isDockerAvailable() {
        if (dockerAvailable == null) {
            try {
                dockerAvailable = DockerClientFactory.instance().isDockerAvailable();
            } catch (Exception e) {
                dockerAvailable = false;
            }
        }
        return dockerAvailable;
    }
}
