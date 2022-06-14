package org.mule.extension.customlistener.internal;

import org.mule.runtime.api.connection.*;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link ConnectionProvider} if you want a new connection each time something requires one.
 */
public class CustomListenerConnectionProvider implements PoolingConnectionProvider<CustomListenerConnection> {

  private final Logger LOGGER = LoggerFactory.getLogger(CustomListenerConnectionProvider.class);

  @Parameter
  private String loginEndpoint;

  @Parameter
  private String username;

  @Parameter
  @Password
  private String password;

  @Parameter
  private String channel;


  @Override
  public CustomListenerConnection connect() throws ConnectionException {
    return new CustomListenerConnection(loginEndpoint, username, password, channel);
  }

  @Override
  public void disconnect(CustomListenerConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      LOGGER.error("Error while disconnecting [" + connection.getUsername() + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(CustomListenerConnection connection) {
    return ConnectionValidationResult.success();
  }
}
