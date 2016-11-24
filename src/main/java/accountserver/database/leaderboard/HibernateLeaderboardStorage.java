package accountserver.database.leaderboard;

import accountserver.database.users.User;
import accountserver.database.users.UserDao;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.HibernateHelper;
import utils.SortedByValueMap;

import java.util.*;

/**
 * Created by Klissan on 24.11.2016.
 */
public class HibernateLeaderboardStorage
        implements LeaderboardDao
{
    private static final Logger log = LogManager.getLogger(HibernateLeaderboardStorage.class);

    public HibernateLeaderboardStorage() {
        log.info("Initialized Hibernate leaderboard storage");
    }


    @Override
    public void addUser(@NotNull User user) {
        log.info("Adding user " + user + " to leaderboard database");
        LeaderboardRecord lr = new LeaderboardRecord(user, 0);
        HibernateHelper.doTransactional(session -> session.save(lr));
    }

    @Override
    public void updateScore(@NotNull User user, int scoreToAdd) {
        log.info("Update score of user  " + user + " with adding score " + scoreToAdd);
        HibernateHelper.doTransactional(session -> session.createQuery("update LeaderboardRecord lr " +
                "set lr.score = lr.score + :scoreToAdd where lr.owner.id = :id")
                .setParameter("id", user.getId())
                .setParameter("scoreToAdd", scoreToAdd)
                .executeUpdate());
    }


    @Override
    public @NotNull Map<User, Integer> getTopUsers(int count) {
        List resp = HibernateHelper.selectTransactional(
                session -> session.createQuery("from LeaderboardRecord lr order by lr.score desc ")
                        .setMaxResults(count)//LIMIT count
                        .list()
        );
        List<LeaderboardRecord> leaders = (List<LeaderboardRecord>) resp;
        Iterator<LeaderboardRecord> it = leaders.iterator();
        Map<User, Integer>  result = new HashMap<>();
        while (it.hasNext()) {
            LeaderboardRecord elem = it.next();
            result.put(
                    ApplicationContext.instance()
                            .get(UserDao.class)
                            .getUserById(elem.getOwner().getId()),
                    elem.getScore()
            );
        }
        return SortedByValueMap.sortByValues(result);
    }

    @Override
    public void removeUser(@NotNull User user) {
        log.info("Removing user from leaderboard " + user);
        HibernateHelper.doTransactional(session -> session.createQuery("delete from  LeaderboardRecord lr " +
                "where lr.owner.id = :id")
                .setParameter("id", user.getId())
                .executeUpdate());
    }
}
