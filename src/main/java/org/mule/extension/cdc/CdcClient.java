package org.mule.extension.cdc;

import com.salesforce.emp.connector.LoginHelper;
import com.salesforce.emp.connector.TopicSubscription;
import com.salesforce.emp.connector.example.BearerTokenProvider;
import com.salesforce.emp.connector.example.LoggingListener;
import org.eclipse.jetty.util.ajax.JSON;
import org.mule.extension.cdc.attributes.HttpRequestAttributes;
import org.mule.extension.connector.EmpConnector;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static org.cometd.bayeux.Channel.META_CONNECT;
import static org.cometd.bayeux.Channel.META_DISCONNECT;
import static org.cometd.bayeux.Channel.META_HANDSHAKE;
import static org.cometd.bayeux.Channel.META_SUBSCRIBE;
import static org.cometd.bayeux.Channel.META_UNSUBSCRIBE;
import static org.mule.extension.cdc.listener.CdcListenerSource.REPLAY_ID;

public class CdcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdcClient.class);

    private EmpConnector connector;
    private String channel;

    public CdcClient(String loginEndpoint, String username, String password) {
        BearerTokenProvider tokenProvider = getBearerTokenProvider(loginEndpoint, username, password);
        connectionWithEmpConnector(tokenProvider);
    }

    public void subscribe(SourceCallback<Map<String, Object>, HttpRequestAttributes> sourceCallback, String replayFrom) {
        try {
            TopicSubscription subscription = connector.subscribe(channel, Long.parseLong(replayFrom), getConsumer(sourceCallback)).get(5, TimeUnit.SECONDS);
            LOGGER.info("Subscribed: {}", subscription);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error(e.getCause().toString());
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            LOGGER.error("Timed out subscribing");
            throw new RuntimeException(e);
        }
    }

    public void unsubscribe() {
        connector.unsubscribe(channel);
    }

    private BearerTokenProvider getBearerTokenProvider(String loginEndpoint, String username, String password) {
        return new BearerTokenProvider(() -> {
            try {
                return LoginHelper.login(new URL(loginEndpoint), username, password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void connectionWithEmpConnector(BearerTokenProvider tokenProvider) {
        try {
            connector = new EmpConnector(tokenProvider.login());

            LoggingListener loggingListener = new LoggingListener(true, true);
            connector.addListener(META_HANDSHAKE, loggingListener)
                    .addListener(META_CONNECT, loggingListener)
                    .addListener(META_DISCONNECT, loggingListener)
                    .addListener(META_SUBSCRIBE, loggingListener)
                    .addListener(META_UNSUBSCRIBE, loggingListener);

            connector.setBearerTokenProvider(tokenProvider);
            connector.start().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Consumer<Map<String, Object>> getConsumer(SourceCallback<Map<String, Object>, HttpRequestAttributes> sourceCallback) {
        return event -> {
            LOGGER.info("Received:\n {}", JSON.toString(event));
            SourceCallbackContext context = sourceCallback.createContext();
            context.addVariable(REPLAY_ID, getFieldValueFromReceivedMessage("replayId", event));
            sourceCallback.handle(Result.<Map<String, Object>, HttpRequestAttributes>builder().output(event).attributes(new HttpRequestAttributes(channel, getFieldValueFromReceivedMessage("type", event))).build(), context);
        };
    }

    private String getFieldValueFromReceivedMessage(String fieldName, Map<String, Object> message) {
        Map<String, Object> event = (Map<String, Object>) message.get("event");
        return String.valueOf(event.get(fieldName));
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}