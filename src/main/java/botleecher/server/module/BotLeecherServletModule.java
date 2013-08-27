package botleecher.server.module;

import botleecher.client.LoginService;
import botleecher.client.MediatorService;
import botleecher.server.LoginServiceImpl;
import botleecher.server.MediatorServiceImpl;
import com.google.inject.servlet.ServletModule;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 27/08/13
 * Time: 09:42
 * To change this template use File | Settings | File Templates.
 */
public class BotLeecherServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(MediatorService.class).to(MediatorServiceImpl.class);
        bind(LoginService.class).to(LoginServiceImpl.class);
        serve("/botleecher/MediatorService").with(MediatorServiceImpl.class);
        serve("/botleecher/LoginService").with(LoginServiceImpl.class);
    }
}
