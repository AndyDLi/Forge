package com.forge.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Logs a message when the application finishes starting up, confirming the background worker role is running.
 */
@Component
public class WorkerRoleBanner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(WorkerRoleBanner.class);

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Forge worker role active; no web server started");
    }
}
