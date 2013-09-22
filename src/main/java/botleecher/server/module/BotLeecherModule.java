/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package botleecher.server.module;

import botleecher.server.EventMediatorServiceImpl;
import botleecher.server.security.LoginManager;
import botleecher.server.security.SessionManager;
import botleecher.server.security.annotations.DefaultLogin;
import botleecher.server.security.impl.XmlLoginManager;
import botleecher.server.security.impl.XmlSessionManager;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import fr.botleecher.rev.IrcConnection;
import fr.botleecher.rev.enums.SettingProperty;
import fr.botleecher.rev.enums.StorageType;
import fr.botleecher.rev.module.ConnectionProvider;
import fr.botleecher.rev.service.*;
import fr.botleecher.rev.service.mongo.MongoSettingsImpl;
import fr.botleecher.rev.service.properties.SettingsImpl;
import fr.botleecher.rev.tools.PropertiesLoader;

/**
 * Guice configuration module
 *
 * @author fdb
 */
public class BotLeecherModule extends AbstractModule {

    private PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    @Override
    protected void configure() {
        loadStorage();
        bind(NicknameProvider.class).to(SettingsNicknameProvider.class);
        bind(BotLeecherFactory.class).to(BotLeecherFactoryImpl.class);
        bind(PackListReader.class).to(PackListReaderImpl.class);
        bind(SessionManager.class).to(XmlSessionManager.class);
        bind(LoginManager.class).to(XmlLoginManager.class);
        bind(EventMediatorService.class).to(EventMediatorServiceImpl.class);

        bind(IrcConnection.class).toProvider(ConnectionProvider.class);
        final LoginInterceptor interceptor = new LoginInterceptor();
        requestInjection(interceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(DefaultLogin.class), interceptor);
    }

    private void loadStorage() {
        final StorageType type = StorageType.getByType(propertiesLoader.getProperty(SettingProperty.PROP_STORAGETYPE.getPropertyName(), SettingProperty.PROP_STORAGETYPE.getDefaultValue().get(0)));
        switch (type) {
            case EMBEDDED_MONGO:
                bind(Settings.class).to(MongoSettingsImpl.class);
                break;
            default:
                // File
                bind(Settings.class).to(SettingsImpl.class);
                break;
        }
    }


}
