package com.nixsolutions.alextuleninov.modulthird;

import com.nixsolutions.alextuleninov.modulthird.cli.LightshowInputInteractiveCLI;
import com.nixsolutions.alextuleninov.modulthird.command.CommandFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LightshowApp {

    private static final Logger log = LoggerFactory.getLogger(LightshowApp.class);

    public static void main(String[] args) {

        System.exit(new LightshowApp().run());

    }

    public int run() {
        try (var serviceRegistry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
             var sessionFactory = new Configuration().buildSessionFactory(serviceRegistry)) {

            var commandFactory = new CommandFactory(sessionFactory);

            var cli = new LightshowInputInteractiveCLI(commandFactory);

            cli.run();

        } catch (Exception e) {
            log.error("Error during user interaction", e);
            return -1;
        }
        return 0;
    }

}