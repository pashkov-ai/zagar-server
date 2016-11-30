package replication;

import main.ApplicationContext;
import matchmaker.MatchMaker;
import model.GameSession;
import network.ClientConnections;
import network.packets.PacketReplicate;
import protocol.model.Cell;
import protocol.model.Food;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alpi
 * @since 31.10.16
 */
public class FullStateReplicator implements Replicator {
  @Override
  public void replicate() {
    for (GameSession gameSession : ApplicationContext.instance().get(MatchMaker.class).getActiveGameSessions()) {
      List<Food> food = gameSession.getField().getFoods().stream()
              .map(f -> new Food(f.getX(), f.getY()))
              .collect(Collectors.toList());
      List<Cell> cells = new ArrayList<>();
      gameSession.getPlayers().forEach(player -> cells.addAll(
              player.getCells().stream()
                      .map(cell ->
                              new Cell(cell.getId(),
                                      player.getId(),
                                      false,
                                      cell.getMass(),
                                      cell.getX(),
                                      cell.getY()))
                      .collect(Collectors.toList())
      ));
      cells.addAll(
              gameSession.getField().getViruses().stream()
                      .map(virus ->
                              //negative IDs shows that cell not belongs to player
                              new Cell(-1, -1, true, virus.getMass(), virus.getX(), virus.getY()))
                      .collect(Collectors.toList())
      );
      ApplicationContext.instance().get(ClientConnections.class).getConnections().forEach(connection -> {
        if (gameSession.getPlayers().contains(connection.getKey())
                && connection.getValue().isOpen()) {
          try {
            new PacketReplicate(cells, food).write(connection.getValue());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }
  }
}
