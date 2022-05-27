package org.mule.extension.customlistener.internal;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.runtime.source.SourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@MediaType("*/*")
@Alias("listener")
@EmitsResponse
public class CustomListenerSource extends Source<String, Void> {
    private static final String COUNTER_VARIABLE = "counter";

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomListenerSource.class);

    @Connection
    private ConnectionProvider<CustomListenerConnection> connection;

    @Config
    private CustomListenerConfiguration config;

    @Inject
    SchedulerService schedulerService;

    Scheduler scheduler;

    @Parameter
    private String message;

    @Parameter
    @Optional(
            defaultValue = "1000"
    )
    private long period;

    @Parameter
    @Optional(
            defaultValue = "MILLISECONDS"
    )
    private TimeUnit timeUnit;

    /**
     * This method is called to start the Message Source.
     * The Source is considered Started once the onStart method finished, so it's required to start a new thread to
     * execute the source logic.
     * In this case the SchedulerService is used to schedule at a fixed rate to execute the {@link SourceRunnable}
     */
    public void onStart(SourceCallback<String, Void> sourceCallback) throws ConnectionException {
        scheduler = schedulerService.cpuLightScheduler();
        CustomListenerConnection connectionInstance = connection.connect();
        scheduler.scheduleAtFixedRate(new SourceRunnable(sourceCallback, connectionInstance), 0, period, timeUnit);
    }

    public void onStop() {
        LOGGER.info("Stopping Source");
        scheduler.stop();
        scheduler.shutdown();
    }

    @OnSuccess
    public void onSuccess(SourceCallbackContext sourceCallbackContext) {
        LOGGER.info(String.format("The message number '%s' has been processed correctly", sourceCallbackContext.getVariable(COUNTER_VARIABLE).get()));;
    }

    @OnError
    public void onError(SourceCallbackContext sourceCallbackContext) {
        LOGGER.info(String.format("An error occurred processing the message number '%s'", sourceCallbackContext.getVariable(COUNTER_VARIABLE).get()));;
    }

    @OnTerminate
    public void onTerminate(SourceResult sourceResult) {
        LOGGER.info("Terminated message processing with Status: " + (sourceResult.isSuccess() ? "OK" : "FAILURE") );
    }

    class SourceRunnable implements Runnable {
        SourceCallback<String, Void> sourceCallback;

        CustomListenerConnection connection;

        int counter = 0;

        SourceRunnable(SourceCallback<String, Void> sourceCallback, CustomListenerConnection connection) {
            this.sourceCallback = sourceCallback;
            this.connection = connection;
        }

        public void run() {
            SourceCallbackContext context = sourceCallback.createContext();
            context.addVariable(COUNTER_VARIABLE, counter++);
            sourceCallback.handle(Result.<String,Void>builder().output(message).build(), context);
        }
    }
}