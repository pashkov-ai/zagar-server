package network;

import com.google.gson.JsonObject;
import main.ApplicationContext;
import model.Player;
import network.handlers.PacketHandlerAuth;
import network.handlers.PacketHandlerWindowSize;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandAuth;
import protocol.commands.CommandWindowSize;
import utils.JSONHelper;

import java.util.Map;

public class ClientConnectionHandler extends WebSocketAdapter {
  @NotNull
  private final static Logger log = LogManager.getLogger(ClientConnectionHandler.class);

  @Override
  public void onWebSocketConnect(@NotNull Session sess) {
    super.onWebSocketConnect(sess);
    log.info("Socket connected: " + sess);
  }

  @Override
  public void onWebSocketText(@NotNull String message) {
    super.onWebSocketText(message);
    log.info("Received packet: " + message);
    if (getSession().isOpen()) {
      handlePacket(message);
    }
  }

  @Override
  public void onWebSocketClose(int statusCode, @NotNull String reason) {
    super.onWebSocketClose(statusCode, reason);
    log.info("Socket closed: [" + statusCode + "] " + reason);
    ClientConnections clientConnections = ApplicationContext.instance().get(ClientConnections.class);
    clientConnections.getConnections().forEach((entry)->{
      if(!entry.getValue().isOpen()) clientConnections.removeConnection(entry.getKey());
    });

  }

  @Override
  public void onWebSocketError(@NotNull Throwable cause) {
    super.onWebSocketError(cause);
    cause.printStackTrace(System.err);
  }

  public void handlePacket(@NotNull String msg) {
    JsonObject json = JSONHelper.getJSONObject(msg);
    String name = json.get("command").getAsString();
    switch (name) {
      case CommandAuth.NAME:
        new PacketHandlerAuth(getSession(), msg);
        break;
      case CommandWindowSize.NAME:
        new PacketHandlerWindowSize(getSession(), msg);
        break;
    }
  }
}
