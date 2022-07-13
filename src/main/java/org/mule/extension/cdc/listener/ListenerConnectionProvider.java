package org.mule.extension.cdc.listener;

import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;


public class ListenerConnectionProvider implements PoolingConnectionProvider<ListenerConnection> {

  @Parameter
  private String loginEndpoint;

  @Parameter
  private String username;

  @Parameter
  @Password
  private String password;

  @Parameter
  private String channel;

  @Placement(order = 1, tab = "Email")
  @Parameter
  @DisplayName("Host")
  private String emailHost;

  @Placement(order = 2, tab = "Email")
  @Parameter
  @DisplayName("Port")
  private Integer emailPort;

  @Parameter
  @Placement(order = 3, tab = "Email")
  @DisplayName("Username")
  private String emailUsername;

  @Parameter
  @Placement(order = 4, tab = "Email")
  @DisplayName("Password")
  private String emailPassword;

  @Parameter
  @Placement(order = 5, tab = "Email")
  @DisplayName("From")
  private String emailFrom;

  @Parameter
  @Placement(order = 6, tab = "Email")
  @DisplayName("To")
  private String emailTo;

  @Parameter
  @Placement(order = 7, tab = "Email")
  @DisplayName("Subject")
  private String emailSubject;

  public ListenerConnectionProvider() {
  }

  @Override
  public ListenerConnection connect() {
    return new ListenerConnection(loginEndpoint, username, password, channel, emailHost, emailPort, emailUsername, emailPassword, emailFrom, emailTo, emailSubject);
  }

  @Override
  public void disconnect(ListenerConnection listenerConnection) {
  }

  @Override
  public ConnectionValidationResult validate(ListenerConnection connection) {
    return ConnectionValidationResult.success();
  }


}
