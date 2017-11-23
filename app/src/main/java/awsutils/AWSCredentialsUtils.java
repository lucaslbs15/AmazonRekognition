package awsutils;


import android.content.Context;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;

import lucas.bicca.amazonrekognition.R;

public class AWSCredentialsUtils {

    public static AWSCredentials createCredentials(Context context) {
        try {
            return new BasicAWSCredentials(context.getString(R.string.aws_access_key), context.getString(R.string.aws_secret_key));
        } catch (Exception ex) {
            throw new AmazonClientException("Cannot load the credentials from the credential " +
                    "profiles file. Please make sure that your credentials file is at the, and is in valid format.", ex);
        }
    }

    public static Credentials getSessionCredentials() {
        AWSSecurityTokenServiceClient tokenServiceClient = getTokenServiceCliente();
        GetSessionTokenRequest sessionTokenRequest = getSessionTokenResult();
        GetSessionTokenResult sessionTokenResult = getSessionTokenResult(tokenServiceClient, sessionTokenRequest);
        return sessionTokenResult.getCredentials();
    }

    public static AWSSecurityTokenServiceClient getTokenServiceCliente() {
        AWSSecurityTokenServiceClient tokenServiceClient = new AWSSecurityTokenServiceClient();
        tokenServiceClient.setEndpoint("sts-endpoint.amazonaws.com");
        return tokenServiceClient;
    }

    public static GetSessionTokenRequest getSessionTokenResult() {
        GetSessionTokenRequest sessionTokenRequest = new GetSessionTokenRequest();
        sessionTokenRequest.setDurationSeconds(7200);
        return sessionTokenRequest;
    }

    public static GetSessionTokenResult getSessionTokenResult(
            AWSSecurityTokenServiceClient tokenServiceClient,
            GetSessionTokenRequest sessionTokenRequest) {
        GetSessionTokenResult sessionTokenResult = tokenServiceClient.getSessionToken(sessionTokenRequest);
        return sessionTokenResult;
    }
}
