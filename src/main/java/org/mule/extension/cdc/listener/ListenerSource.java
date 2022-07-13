package org.mule.extension.cdc.listener;

import org.mule.extension.cdc.CdcConnectionProvider;
import org.mule.extension.emailService.EmailServiceImpl;
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
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.annotation.source.OnBackPressure;
import org.mule.runtime.extension.api.runtime.source.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


@MediaType("*/*")
@Alias("listener")
@EmitsResponse
@MetadataScope(outputResolver = OutputResolver.class)
public class ListenerSource extends Source<Map<String, Object>, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerSource.class);

    @Connection
    private ConnectionProvider<ListenerConnection> connection;

    private EmailServiceImpl emailService;
    private ListenerConnection connectionInstance;

    public void onStart(SourceCallback<Map<String, Object>, Object> sourceCallback) throws ConnectionException {
        connectionInstance = connection.connect();
        CdcConnectionProvider connectionProvider = new CdcConnectionProvider(sourceCallback);
        emailService = new EmailServiceImpl(connectionInstance.getEmailHost(), connectionInstance.getEmailPort(), connectionInstance.getEmailUsername(), connectionInstance.getEmailPassword());

        try {
            connectionProvider.processEvents(connectionInstance.getLoginEndpoint(), connectionInstance.getUsername(), connectionInstance.getPassword(), connectionInstance.getChannel(), null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        LOGGER.info("Stopping Source");
    }

    @OnSuccess
    public void onSuccess(@Content Object responseBody, SourceCallbackContext ctx) {
        LOGGER.info("Successes");
    }

    @OnError
    public void onError(Error error, SourceCallbackContext callbackContext) {
        LOGGER.info("Error" + emailService);
        LOGGER.info(error.getErrorType() + " " + error.getDetailedDescription());

        //emailService.sendEmail(connectionInstance.getEmailFrom(), connectionInstance.getEmailTo(), connectionInstance.getEmailSubject(), "Testowa Wiadomosc");
    }

    @OnTerminate
    public void onTerminate(SourceResult sourceResult) {
        LOGGER.info("Terminate");
        //emailService.sendEmail(connectionInstance.getEmailFrom(), connectionInstance.getEmailTo(), connectionInstance.getEmailSubject(), "Testowa Wiadomosc");
    }

    @OnBackPressure
    public void onBackPressure(BackPressureContext ctx) {
        LOGGER.info("onBackPressure");
        //emailService.sendEmail(connectionInstance.getEmailFrom(),  connectionInstance.getEmailTo(), connectionInstance.getEmailSubject(), "Testowa Wiadomosc");
    }

}