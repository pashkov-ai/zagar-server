package accountserver.database.leaderboard;

import accountserver.database.tokens.Token;
import accountserver.database.users.User;
import com.google.gson.annotations.Expose;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Klissan on 24.11.2016.
 */
@Entity
@Table(name = "leaderboard")
public class LeaderboardRecord
        implements Serializable
{
    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @NotNull
    @Column(name = "score", nullable = false)
    private Integer score = 0;

    protected LeaderboardRecord(){}

    public LeaderboardRecord(@NotNull User user, @NotNull Integer score){
        this.owner = user;
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }

    public User getOwner() {
        return owner;
    }
}
