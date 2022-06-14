package org.mule.extension.customlistener.internal;


import org.mule.runtime.extension.api.runtime.source.SourceCallback;

import java.util.Map;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class CustomListenerConnection {

  private final String loginEndpoint;
  private final String username;
  private final String password;
  private final String channel;
  private String replayId;

  public void setReplayId(String replayId) {
    this.replayId = replayId;
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

  public String getReplayId() {
    return replayId;
  }

  public CustomListenerConnection(String loginEndpoint, String username, String password, String chanel){
    this.loginEndpoint = loginEndpoint;
    this.username = username;
    this.password = password;
    this.channel = chanel;
  }

  public void invalidate() {
    // do something to invalidate this connection!
  }

  public void subscribeChannel(SourceCallback<Map<String, Object>, Object> sourceCallback) throws Throwable {
    LoginExample loginExample = new LoginExample(sourceCallback);
    loginExample.processEvents(loginEndpoint, username, password, channel, replayId);
  }
}
