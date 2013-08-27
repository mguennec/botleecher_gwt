package botleecher.server.rest; /**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 27/08/13
 * Time: 00:31
 * To change this template use File | Settings | File Templates.
 */

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
        createInjector();
        return injector;
    }
    private static Injector injector;

    private static void createInjector() {
        if (injector == null) {
            synchronized (BotLeecherModule.class) {
                if (injector == null) {
                    injector = Guice.createInjector(new BotLeecherModule(), new BotLeecherServletModule(), new JerseyServletModule() {

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
        }
    }
}
