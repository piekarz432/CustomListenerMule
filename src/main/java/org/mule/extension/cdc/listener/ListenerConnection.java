package org.mule.extension.cdc.listener;

public final class ListenerConnection {

  private final String loginEndpoint;
  private final String username;
  private final String password;
  private final String channel;
  private final String emailHost;
  private final Integer emailPort;
  private final String emailUsername;
  private final String emailPassword;
  private final String emailFrom;
  private final String emailTo;
  private final String emailSubject;

  public ListenerConnection(String loginEndpoint, String username, String password, String channel, String emailHost, Integer emailPort, String emailUsername, String emailPassword, String emailFrom, String emailTo, String emailSubject) {
    this.loginEndpoint = loginEndpoint;
    this.username = username;
    this.password = password;
    this.channel = channel;
    this.emailHost = emailHost;
    this.emailPort = emailPort;
    this.emailUsername = emailUsername;
    this.emailPassword = emailPassword;
    this.emailFrom = emailFrom;
    this.emailTo = emailTo;
    this.emailSubject = emailSubject;
  }

  public String getLoginEndpoint() {
    return loginEndpoint;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getChannel() {
    return channel;
  }

  public String getEmailHost() {
    return emailHost;
  }

  public Integer getEmailPort() {
    return emailPort;
  }

  public String getEmailUsername() {
    return emailUsername;
  }

  public String getEmailPassword() {
    return emailPassword;
  }

  public String getEmailFrom() {
    return emailFrom;
  }

  public String getEmailTo() {
    return emailTo;
  }

  public String getEmailSubject() {
    return emailSubject;
  }
}




