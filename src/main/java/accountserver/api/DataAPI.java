package accountserver.api;

import accountserver.database.TokenDAO;
import accountserver.database.User;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.JSONHelper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by xakep666 on 13.10.16.
 *
 * Provides REST API for work with users
 */
@Path("/data")
public class DataAPI {
    @NotNull
    private static final Logger log = LogManager.getLogger(DataAPI.class);

    /**
     * Method retrieves logged in users (with valid tokens) and serializes it to jso
     * @return serialized list
     */
    @GET
    @Produces("application/json")
    @Path("users")
    public Response loggedInUsers() {
        log.info("Logged in users list requested");
        UserInfo ret = new UserInfo();
        ret.users = ApplicationContext.instance().get(TokenDAO.class).getValidTokenOwners();
        return Response.ok(JSONHelper.toJSON(ret, new TypeToken<UserInfo>() {
        }.getType())).build();
    }

    private static class UserInfo {
        @Expose
        List<User> users;
    }
}
