package org.mule.extension.customlistener.internal;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.MetadataScope;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@MediaType("*/*")
@Alias("listener")
@EmitsResponse
@MetadataScope(outputResolver = OutputResolver.class)
public class CustomListenerSource extends Source<Map<String, Object>, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomListenerSource.class);

    @Optional
    @Parameter
    private String replayId;

    @Connection
    private ConnectionProvider<CustomListenerConnection> connection;

    @Config
    private CustomListenerConfiguration config;

    public void onStart(SourceCallback<Map<String, Object>, Object> sourceCallback) throws ConnectionException {
        CustomListenerConnection connectionInstance = connection.connect();
        connectionInstance.setReplayId(replayId);
        try {
            connectionInstance.subscribeChannel(sourceCallback);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        LOGGER.info("Stopping Source");
    }

}


