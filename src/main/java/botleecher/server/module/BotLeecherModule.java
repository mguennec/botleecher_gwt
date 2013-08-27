/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package botleecher.server.module;

import botleecher.client.EventMediatorService;
import botleecher.server.BotMediatorImpl;
import botleecher.server.EventMediatorServiceImpl;
import botleecher.server.security.LoginManager;
import botleecher.server.security.SessionManager;
import botleecher.server.security.annotations.DefaultLogin;
import botleecher.server.security.impl.XmlLoginManager;
import botleecher.server.security.impl.XmlSessionManager;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import fr.botleecher.rev.IrcConnection;
import fr.botleecher.rev.service.*;

/**
 * Guice configuration module
 *
 * @author fdb
 */
public class BotLeecherModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(Settings.class).to(SettingsImpl.class);
        bind(NicknameProvider.class).to(SettingsNicknameProvider.class);
        bind(BotLeecherFactory.class).to(BotLeecherFactoryImpl.class);
        bind(PackListReader.class).to(PackListReaderImpl.class);
        bind(BotMediator.class).to(BotMediatorImpl.class);
        bind(SessionManager.class).to(XmlSessionManager.class);
        bind(LoginManager.class).to(XmlLoginManager.class);
        bind(EventMediatorService.class).to(EventMediatorServiceImpl.class);

        bind(IrcConnection.class).toProvider(ConnectionProvider.class);
        final LoginInterceptor interceptor = new LoginInterceptor();
        requestInjection(interceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(DefaultLogin.class), interceptor);
    }



}
