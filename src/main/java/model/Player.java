package model;

import accountserver.database.users.User;
import main.ApplicationContext;
import org.jetbrains.annotations.NotNull;
import utils.IDGenerator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author apomosov
 */
public class Player {
  private final int id;
  @NotNull
  private final List<PlayerCell> cells = new ArrayList<>();
  private Field field;
  @NotNull
  private User user;
  private int windowWidth;
  private int windowHeight;

  public Player(int id, @NotNull User user) {
    this.id = id;
    this.user = user;
    addCell(new PlayerCell(ApplicationContext.instance().get(IDGenerator.class).next(), 0, 0));
  }

  @NotNull
  public Field getField() {
    return field;
  }

  public void setField(@NotNull Field field) {
    this.field = field;
  }

  public void addCell(@NotNull PlayerCell cell) {
    cells.add(cell);
  }

  public void removeCell(@NotNull PlayerCell cell) {
    cells.remove(cell);
  }

  @NotNull
  public User getUser() {
    return user;
  }

  @NotNull
  public List<PlayerCell> getCells() {
    return cells;
  }

  public int getTotalScore() {
    Optional<Integer> totalScore = cells.stream()
            .map(PlayerCell::getMass)
            .reduce(Math::addExact);
    return totalScore.isPresent() ?
            totalScore.get() :
            0;
  }

  public int getId() {
    return id;
  }

  Duration getMinTimeWithoutMovements() {
    Optional<PlayerCell> lastmoved = cells.stream()
            .min((c1, c2) -> Long.compare(c1.getLastMovementTime(), c2.getLastMovementTime()));
    if (!lastmoved.isPresent()) return Duration.ZERO;
    return Duration.ofMillis(lastmoved.get().getLastMovementTime());
  }

  public int getWindowWidth() {
    return windowWidth;
  }

  public void setWindowWidth(int windowWidth) {
    this.windowWidth = windowWidth;
  }

  public int getWindowHeight() {
    return windowHeight;
  }

  public void setWindowHeight(int windowHeight) {
    this.windowHeight = windowHeight;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Player) && (id == ((Player) obj).id);
  }

  @NotNull
  @Override
  public String toString() {
    return "Player{" +
            "name='" + user.getName() + '\'' +
        '}';
  }
}
