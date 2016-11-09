package tests;

import accountserver.api.data.LeaderboardApi;
import accountserver.database.User;
import accountserver.database.UserDao;
import accountserver.database.leaderboard.LeaderboardDao;
import com.squareup.okhttp.Response;
import main.ApplicationContext;
import org.junit.Test;
import utils.JSONHelper;
import utils.SortedByValueMap;

import java.util.SortedMap;
import java.util.TreeMap;

import static javax.ws.rs.core.Response.Status;
import static org.junit.Assert.*;

/**
 * Created by user on 07.11.16.
 * Leaderboard methods test
 */
public class TestLeaderboardApi extends WebServerTest {
    @Test
    public void testTop() {
        User[] users = new User[]{new User("Cat", "123"), new User("Dog", "456"), new User("Pig", "789")};
        int[] scores = new int[]{12, 78, 56};
        final String urlPostfix = "data/leaderboard";
        for (int i = 0; i < users.length; i++){
            ApplicationContext.instance().get(UserDao.class).addUser(users[i]);
            ApplicationContext.instance().get(LeaderboardDao.class).addUser(users[i].getId());
            ApplicationContext.instance().get(LeaderboardDao.class).updateScore(users[i].getId(), scores[i]);
        }
        SortedMap<String,Integer> top = new TreeMap<>();
        top.put(users[1].getName(),scores[1]);
        top.put(users[2].getName(),scores[2]);
        top = SortedByValueMap.sortByValues(top);
        try {
            Response actual = getRequest(urlPostfix,"N="+(users.length-1),null);
            assertEquals(Status.OK,Status.fromStatusCode(actual.code()));
            LeaderboardApi.UserInfo ui = JSONHelper.fromJSON(actual.body().string(),LeaderboardApi.UserInfo.class);
            assertNotNull(ui);
            assertEquals(top.toString(),ui.leadersWithScore.toString());
        } catch (Exception e) {
            fail(e.toString());
        } finally {
            for (int i = 0; i < users.length; i++) {
                ApplicationContext.instance().get(LeaderboardDao.class).removeUser(users[i].getId());
                ApplicationContext.instance().get(UserDao.class).removeUser(users[i]);
            }
        }
    }
}