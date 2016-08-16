package Core;

import Core.Utils.CalenderUtils;

/**
 * Created by vthiruvengadam on 8/10/16.
 */
public class Configuration {

    private static final String niprAlertEndpointField = "NIPR_ALERT_ENDPOINT";
    private static String getNiprAlertEndpoint;

    private static final String niprUserNameField = "NIPR_USERNAME";
    private static String niprUsername;

    private static final String niprPasswordField = "NIPR_PASSWORD";
    private static String niprPassword;

    private static final String salesForceConsumerKeyField = "SALESFORCE_CONSUMER_KEY";
    private static String salesForceConsumerKey;

    private static final String salesForceConsumerSecretField = "SALESFORCE_CONSUMER_SECRET";
    private static String salesForceConsumerSecret;

    private static final String salesForceUsernameField = "SALESFORCE_USERNAME";
    private static String salesForceUsername;

    private static final String salesForcePasswordField = "SALESFORCE_PASSWORD";
    private static String salesForcePassword;

    private static final String sendGridApiKeyField = "SENDGRID_API_KEY";
    private static String sendGridApiKey;

    private static final String alertEmailRecipientField = "EMAIL_ALERT_RECIPIENT";
    private static String alertEmailRecipient;

    private static final String alertEmailSenderField = "EMAIL_ALERT_SENDER";
    private static String alertEmailSender;

    private static final String reconcilerRetryField = "RETRY_INTERVAL";
    private static int reconcilerRetry;

    private static final String resyncDaysCountField = "RESYNC_DAYS_COUNT";
    private static int resyncDaysCount;

    private static final int defaultResyncDaysCount = 5;
    private static final int maxResyncDaysCount = 14;

    public static int GetResyncDaysCount() {
        return resyncDaysCount;
    }

    public static void LoadParams() {

        // "https://pdb-services.nipr.com/pdb-alerts-industry-services/services/industry-ws"
        getNiprAlertEndpoint = System.getenv(niprAlertEndpointField);
        throwIfEmpty(niprAlertEndpointField, getNiprAlertEndpoint);

        niprUsername = System.getenv(niprUserNameField);
        throwIfEmpty(niprUserNameField, niprUsername);

        niprPassword = System.getenv(niprPasswordField);
        throwIfEmpty(niprPasswordField, niprPassword);

        salesForceConsumerKey = System.getenv(salesForceConsumerKeyField);
        throwIfEmpty(salesForceConsumerKeyField, salesForceConsumerKey);

        salesForceConsumerSecret = System.getenv(salesForceConsumerSecretField);
        throwIfEmpty(salesForceConsumerSecretField, salesForceConsumerSecret);

        salesForceUsername = System.getenv(salesForceUsernameField);
        throwIfEmpty(salesForceUsernameField, salesForceUsername);

        salesForcePassword = System.getenv(salesForcePasswordField);
        throwIfEmpty(salesForcePasswordField, salesForcePassword);

        sendGridApiKey = System.getenv(sendGridApiKeyField);

        alertEmailRecipient = System.getenv(alertEmailRecipientField);
        throwIfEmpty(alertEmailRecipientField, alertEmailRecipient);

        alertEmailSender = System.getenv(alertEmailSenderField);
        throwIfEmpty(alertEmailSenderField, alertEmailSender);

        String lInterval = System.getenv(reconcilerRetryField);
        throwIfEmpty(reconcilerRetryField, lInterval);
        reconcilerRetry = Integer.parseInt(lInterval);

        String lDays = System.getenv(resyncDaysCountField);
        if(CalenderUtils.isNullOrWhiteSpace(lDays)) {
            resyncDaysCount = defaultResyncDaysCount; // Every minute
        }
        else {
            resyncDaysCount = Integer.parseInt(lDays);
            if(resyncDaysCount > maxResyncDaysCount) {
                // We cannot go back beyond MaxResyncDaysCount = 14
                resyncDaysCount = maxResyncDaysCount;
            }
            else if(resyncDaysCount < 0) {
                resyncDaysCount = defaultResyncDaysCount;
            }
        }
    }

    public static String getGetNiprAlertEndpoint() {
        return getNiprAlertEndpoint;
    }

    public static String getSendGridKey() {
        return sendGridApiKey;
    }

    public static String getNiprUsername() {
        return niprUsername;
    }

    public static String getNiprPassword() {
        return niprPassword;
    }

    public static String getSalesForceConsumerKey() {
        return salesForceConsumerKey;
    }

    public static String getSalesForceConsumerSecret() {
        return salesForceConsumerSecret;
    }

    public static String getSalesForceUsername() {
        return salesForceUsername;
    }

    public static String getSalesForcePassword() {
        return salesForcePassword;
    }

    public static String getSendGridApiKey() {
        return sendGridApiKey;
    }

    public static String getAlertEmailSender() {
        return alertEmailSender;
    }

    public static String getAlertEmailRecipient() {
        return alertEmailRecipient;
    }

    public static int getReconcilerRetry() {
        return reconcilerRetry;
    }

    public static int getResyncDaysCount() {
        return resyncDaysCount;
    }

    private static void throwIfEmpty(String aInDataField, String aInData) {

        if(CalenderUtils.isNullOrWhiteSpace(aInData)) {
            throw new IllegalArgumentException("ENV Field " + aInDataField + " is not set");
        }
    }

}
