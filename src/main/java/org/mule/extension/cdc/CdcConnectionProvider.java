package org.mule.extension.cdc;

import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.LoginHelper;
import com.salesforce.emp.connector.TopicSubscription;
import com.salesforce.emp.connector.example.BearerTokenProvider;
import com.salesforce.emp.connector.example.LoggingListener;
import org.eclipse.jetty.util.ajax.JSON;
import org.mule.extension.connector.EmpConnector;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static org.cometd.bayeux.Channel.*;

public class CdcConnectionProvider {

    private final SourceCallback<Map<String, Object>, Object> sourceCallback;

    public CdcConnectionProvider(SourceCallback<Map<String, Object>, Object> sourceCallback) {
        this.sourceCallback = sourceCallback;
    }

    private final ExecutorService workerThreadPool = Executors.newFixedThreadPool(1);

    public void processEvents(String loginEndpoint, String username, String password, String channel, String replayId) throws Throwable {

        BearerTokenProvider tokenProvider = new BearerTokenProvider(() -> {
            try {
                return LoginHelper.login(new URL(loginEndpoint), username, password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        BayeuxParameters params = tokenProvider.login();

        EmpConnector connector = new EmpConnector(params);

        LoggingListener loggingListener = new LoggingListener(true, true);

        connector.addListener(META_HANDSHAKE, loggingListener)
                .addListener(META_CONNECT, loggingListener)
                .addListener(META_DISCONNECT, loggingListener)
                .addListener(META_SUBSCRIBE, loggingListener)
                .addListener(META_UNSUBSCRIBE, loggingListener);

        connector.setBearerTokenProvider(tokenProvider);

        connector.start().get(5, TimeUnit.SECONDS);

        long replayFrom = EmpConnector.REPLAY_FROM_TIP;

        if (replayId != null) {
            replayFrom = Long.parseLong(replayId);
        }

        TopicSubscription subscription;
        try {
            subscription = connector.subscribe(channel, replayFrom, getConsumer()).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            System.err.println(e.getCause().toString());
            System.exit(1);
            throw e.getCause();
        } catch (TimeoutException e) {
            System.err.println("Timed out subscribing");
            System.exit(1);
            throw e.getCause();
        }
        System.out.printf("Subscribed: %s%n", subscription);

    }

    private Consumer<Map<String, Object>> getConsumer() {
        return event -> workerThreadPool.submit(() ->
        {
            System.out.printf("Received:\n%s%n", JSON.toString(event));
            try {
                messageToPayload(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void messageToPayload(Map<String, Object> message) throws InterruptedException {
        SourceCallbackContext context = sourceCallback.createContext();
        context.addVariable("message", message);
        sourceCallback.handle(Result.<Map<String, Object>, Object>builder().output(message).build(), context);
    }
}


