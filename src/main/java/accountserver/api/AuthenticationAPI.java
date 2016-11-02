package accountserver.api;

import accountserver.database.Token;
import accountserver.database.TokensStorage;
import accountserver.database.User;
import accountserver.database.UsersStorage;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationAPI {
  private static final Logger log = LogManager.getLogger(AuthenticationAPI.class);

  @POST
  @Path("register")
  @Consumes("application/x-www-form-urlencoded")
  @Produces("text/plain")
  public Response register(@FormParam("user") String username,
                           @FormParam("password") String password) {

    if (username == null || password == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

    if (username.equals("") || password.equals("") ||
            ApplicationContext.instance().get(UsersStorage.class).getUserByName(username)!=null) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    ApplicationContext.instance().get(UsersStorage.class).addUser(new User(username,password));

    log.info("New user '{}' registered", username);
    return Response.ok("User " + username + " registered.").build();
  }

  @POST
  @Path("login")
  @Consumes("application/x-www-form-urlencoded")
  @Produces("text/plain")
  public Response authenticateUser(@FormParam("user") String username,
                                   @FormParam("password") String password) {

    if (username == null || password == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    if (username.equals("") || password.equals("")) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }
    try {
      // Authenticate the user using the credentials provided
      User user = ApplicationContext.instance().get(UsersStorage.class).getUserByName(username);
      if (user==null || !user.validatePassword(password)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      // Issue a token for the user
      Token token = ApplicationContext.instance().get(TokensStorage.class).generateToken(user.getId());
      log.info("User '{}' logged in", user);

      // Return the token on the response
      return Response.ok(token.toString()).build();

    } catch (Exception e) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  public static boolean validateToken(String rawToken) {
    Token token = Token.parse(rawToken);
    if (token==null || !ApplicationContext.instance().get(TokensStorage.class).isValidToken(token)) {
      return false;
    }
    log.info("Correct token from '{}'", ApplicationContext.instance().get(TokensStorage.class).getTokenOwner(token));
    return true;
  }

  @POST
  @Authorized
  @Path("logout")
  @Produces("text/plain")
  public Response logout(@Context HttpHeaders headers) {
    Token token = AuthenticationFilter.getTokenFromHeaders(headers);
    if (token==null)
      return Response.status(Response.Status.UNAUTHORIZED).build();
    ApplicationContext.instance().get(TokensStorage.class).removeToken(token);
    return Response.ok("Logged out").build();
  }
}