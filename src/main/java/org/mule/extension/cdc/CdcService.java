package org.mule.extension.cdc;

import org.mule.extension.cdc.attributes.HttpRequestAttributes;
import org.mule.extension.emailService.EmailService;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;

import java.util.Map;

public class CdcService {

    private final CdcClient cdcClient;
    private final EmailService emailService;

    public CdcService(CdcClient cdcClient, EmailService emailService) {
        this.cdcClient = cdcClient;
        this.emailService = emailService;
    }

    public void subscribe(SourceCallback<Map<String, Object>, HttpRequestAttributes> sourceCallback, String replayFrom) {
        cdcClient.subscribe(sourceCallback, replayFrom);
    }

    public void unsubscribe() {
        cdcClient.unsubscribe();
    }

    public String getChannel() {
        return cdcClient.getChannel();
    }

    public void sendEmail(String text) {
        emailService.sendEmail(text);
    }

    public void setChannel(String channel) {
        cdcClient.setChannel(channel);
    }

    public void setSubject(String channel) {
        emailService.setSubject(channel);
    }
}