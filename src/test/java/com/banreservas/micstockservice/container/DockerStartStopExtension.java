package com.banreservas.micstockservice.container;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.testcontainers.containers.wait.strategy.Wait.forListeningPort;

public class DockerStartStopExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    private DockerComposeContainer docker;
    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            started = true;
            docker = new DockerComposeContainer(new File("docker-compose.yml"))
                    .withLocalCompose(true)
                    .withOptions("--compatibility")
                    .withExposedService("zookeeper", 2181, forListeningPort())
                    .withExposedService("schema-registry", 8081, forListeningPort())
                    .withExposedService("kafka", 9092, forListeningPort())
                    .withExposedService("redis", 6379, forListeningPort())
                    .withExposedService("mongodb", 27017, forListeningPort());
            docker.start();
            context.getRoot().getStore(GLOBAL).put("any unique name", this);
        }
    }

    @Override
    public void close() {
        docker.stop();
    }
}
