package botleecher.server.rest.resources;

import botleecher.client.LoginService;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path("/session")
public class LoginResource {

    @Inject
    private LoginService loginService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/login")
    public Response getSession(@Context final HttpServletRequest request, @CookieParam("sid") final String sid, @CookieParam("user") final String user, @QueryParam("login") final String login, @QueryParam("pass") final String pass) throws Exception {
        final String session;
        final String loggedUser;
        if (StringUtils.isNotBlank(sid)) {
            loggedUser = user;
            session = sid;
        } else {
            loggedUser = login;
            session = loginService.login(login, pass, request.getRemoteAddr());
        }
        return (StringUtils.isBlank(session) ? Response.serverError() : Response.ok(session).cookie(new NewCookie[]{getCookie("sid", session), getCookie("user", loggedUser)})).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@CookieParam("sid") final String sid) throws Exception {
        loginService.logout(sid);
        return Response.ok().cookie(new NewCookie[]{getCookie("sid", null), getCookie("user", null)}).build();
    }

    private NewCookie getCookie(final String name, final String value) {
        return new NewCookie(name, value, "/", null, "Rest Service Cookie", NewCookie.DEFAULT_MAX_AGE, false);
    }
}
