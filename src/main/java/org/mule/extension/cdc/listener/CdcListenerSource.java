package org.mule.extension.cdc.listener;

import org.mule.extension.cdc.CdcService;
import org.mule.extension.cdc.attributes.HttpRequestAttributes;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.metadata.MetadataScope;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.annotation.source.OnBackPressure;
import org.mule.runtime.extension.api.runtime.source.BackPressureContext;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.runtime.source.SourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@MediaType("*/*")
@Alias("cdclistener")
@EmitsResponse
@MetadataScope(outputResolver = OutputResolver.class)
public class CdcListenerSource extends Source<Map<String, Object>, HttpRequestAttributes> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdcListenerSource.class);

    @Parameter
    private String channel;

    @Parameter
    private String replayFrom;

    public static final String REPLAY_ID = "REPLAY_ID";

    @Connection
    private ConnectionProvider<CdcService> connection;
    private CdcService cdcService;

    public void onStart(SourceCallback<Map<String, Object>, HttpRequestAttributes> sourceCallback) throws ConnectionException {
        cdcService = connection.connect();
        cdcService.setSubject(channel);
        cdcService.setChannel(channel);
        cdcService.subscribe(sourceCallback, replayFrom);
    }

    public void onStop() {
        LOGGER.debug("Stopping Source");
        connection.disconnect(cdcService);
    }

    @OnSuccess
    public void onSuccess(@Content Object responseBody, SourceCallbackContext ctx) {
        LOGGER.debug("Successes");
    }

    @OnError
    public void onError(Error error, SourceCallbackContext callbackContext) {
        String replayId = (String) callbackContext.getVariable(REPLAY_ID).orElseGet(() -> {
            throw new RuntimeException("ReplayId was not processed correctly");
        });

        LOGGER.error("\nTopic: {}\nReplayId: {}\nError type: {}\nDescription: {}",
                cdcService.getChannel(),
                replayId,
                error.getErrorType(),
                error.getDetailedDescription());
        String emailMessage = String.format("Topic: %s \nReplayId: %s \nError type: %s \nDescription: %s ", cdcService.getChannel(), replayId, error.getErrorType(), error.getDetailedDescription());
        cdcService.sendEmail("Error " + emailMessage);
    }

    @OnTerminate
    public void onTerminate(SourceResult sourceResult) {
        String replayId = (String) sourceResult.getSourceCallbackContext().getVariable(REPLAY_ID).orElseGet(() -> {
            throw new RuntimeException("ReplayId was not processed correctly");
        });
        LOGGER.info("Terminate for: \nTopic: {}\nReplayId: {}\nInvocation Error: {}\nResponse Error: {}",
                cdcService.getChannel(),
                replayId,
                sourceResult.getInvocationError().isPresent() ? sourceResult.getInvocationError().get().getErrorType() +"\n" + sourceResult.getInvocationError().get().getDetailedDescription() : "No error occurred",
                sourceResult.getResponseError().isPresent() ? sourceResult.getResponseError().get().getErrorType() + "\n" + sourceResult.getResponseError().get().getDetailedDescription()  : "No error occurred");
    }

    @OnBackPressure
    public void onBackPressure(BackPressureContext ctx) {
        String replayId = (String) ctx.getSourceCallbackContext().getVariable(REPLAY_ID).orElseGet(() -> {
            throw new RuntimeException("ReplayId was not processed correctly");
        });

        LOGGER.error("OnBackPressure for \nTopic: {}\nReplayId: {}\nError: {}",
                cdcService.getChannel(),
                replayId,
                ctx.getEvent().getError().isPresent() ? ctx.getEvent().getError().get().getErrorType() + "\n" + ctx.getEvent().getError().get().getDetailedDescription() : "No error occurred");
        cdcService.sendEmail("OnBackPressure for replyId: " + replayId);
    }
}