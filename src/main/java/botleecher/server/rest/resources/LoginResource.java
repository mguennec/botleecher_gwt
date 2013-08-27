package botleecher.server.rest.resources;

import botleecher.server.security.LoginManager;
import botleecher.server.security.SessionManager;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/login")
public class LoginResource {

    @Inject
    private LoginManager loginManager;
    @Inject
    private SessionManager sessionManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSession(@Context final HttpServletRequest request) throws Exception {
        final String session;
        if (request.getHeader("sid") != null) {
            session = request.getHeader("sid");
        } else {
            final String login = request.getParameter("login");
            final String pass = request.getParameter("pass");
            if (StringUtils.isNotBlank(login) && StringUtils.isNotBlank(pass) && loginManager.isLoginValid(login, pass)) {
                session = sessionManager.createSession(login, request.getRemoteAddr());
            } else {
                session = null;
            }
        }
        return session;
    }
}
