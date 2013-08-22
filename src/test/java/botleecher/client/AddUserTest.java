package botleecher.client;

import botleecher.server.module.BotLeecherModule;
import botleecher.server.security.LoginManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 11/08/13
 * Time: 04:51
 * To change this template use File | Settings | File Templates.
 */
public class AddUserTest {

    @Test
    public void test() throws Exception {
        Injector injector = Guice.createInjector(new BotLeecherModule());
        final LoginManager loginManager = injector.getInstance(LoginManager.class);
        loginManager.addLogin("test", "test");
    }

}
