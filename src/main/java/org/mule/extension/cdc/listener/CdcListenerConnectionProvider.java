package org.mule.extension.cdc.listener;

import org.mule.extension.cdc.CdcClient;
import org.mule.extension.cdc.CdcService;
import org.mule.extension.configuration.EmailFactory;
import org.mule.extension.emailService.EmailService;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.springframework.mail.javamail.JavaMailSender;

public class CdcListenerConnectionProvider implements PoolingConnectionProvider<CdcService> {

    @Parameter
    private String loginEndpoint;

    @Parameter
    private String username;

    @Parameter
    @Password
    private String password;

    @Placement(order = 1, tab = "Email")
    @Parameter
    @DisplayName("Host")
    private String emailHost;

    @Placement(order = 2, tab = "Email")
    @Parameter
    @DisplayName("Port")
    private Integer emailPort;

    @Parameter
    @Placement(order = 3, tab = "Email")
    @DisplayName("Username")
    private String emailUsername;

    @Parameter
    @Placement(order = 4, tab = "Email")
    @DisplayName("Password")
    private String emailPassword;

    @Parameter
    @Placement(order = 5, tab = "Email")
    @DisplayName("From")
    private String emailFrom;

    @Parameter
    @Placement(order = 6, tab = "Email")
    @DisplayName("To")
    private String emailTo;

    @Parameter
    @Placement(order = 7, tab = "Email")
    @DisplayName("Subject")
    private String emailSubjectPrefix;

    public CdcListenerConnectionProvider() {}

    @Override
    public CdcService connect() {
        JavaMailSender mailSender = EmailFactory.createEmailConfiguration(emailHost, emailPort, emailUsername, emailPassword);
        EmailService emailService = new EmailService(mailSender, emailFrom, emailTo, emailSubjectPrefix);
        CdcClient cdcClient = new CdcClient(loginEndpoint, username, password);

        return new CdcService(cdcClient, emailService);
    }

    @Override
    public void disconnect(CdcService cdcService) {
        cdcService.unsubscribe();
    }

    @Override
    public ConnectionValidationResult validate(CdcService cdcService) {
        return ConnectionValidationResult.success();
    }
}