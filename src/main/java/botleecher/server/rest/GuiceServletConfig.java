package botleecher.server.rest;

import botleecher.server.module.BotLeecherModule;
import botleecher.server.module.BotLeecherServletModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import java.util.HashMap;
import java.util.Map;

public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new BotLeecherModule(), new BotLeecherServletModule(), new JerseyServletModule() {

            @Override
            protected void configureServlets() {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("com.sun.jersey.config.property.packages", "botleecher.server.rest.resources");
                params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

                serve("/rest/*").with(GuiceContainer.class, params);

            }
        });
    }
}
