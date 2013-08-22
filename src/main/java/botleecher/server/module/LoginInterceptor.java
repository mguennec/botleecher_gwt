package botleecher.server.module;

import botleecher.server.security.LoginManager;
import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;


public class LoginInterceptor implements MethodInterceptor {

    @Inject
    private LoginManager loginManager;
    private static final String DEFAULT_LOGIN = "admin";
    private static final String DEFAULT_PASS = "admin";

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object retVal = null;
        if (methodInvocation.getArguments().length == 2 && methodInvocation.getArguments()[0] instanceof String && methodInvocation.getArguments()[1] instanceof String && loginManager.countUsers() == 0) {
            if (StringUtils.equalsIgnoreCase(DEFAULT_LOGIN, (String) methodInvocation.getArguments()[0]) && StringUtils.equals(DEFAULT_PASS, (String) methodInvocation.getArguments()[1])) {
                retVal = true;
            }
        }
        if (retVal == null) {
            retVal = methodInvocation.proceed();
        }
        return retVal;
    }
}
