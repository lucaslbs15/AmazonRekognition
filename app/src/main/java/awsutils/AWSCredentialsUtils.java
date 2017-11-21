package awsutils;


import android.content.Context;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class AWSCredentialsUtils {

    public static AWSCredentials createCredentials() {
        try {
            //return new ProfileCredentialsProvider("AdminUser").getCredentials();
            return new BasicAWSCredentials("AKIAI3F6E73ZMK2VQ56A", "3qoIsx/bLSOBB2NhCxXt+9FF553j90yXbhrcncLa");
        } catch (Exception ex) {
            throw new AmazonClientException("Cannot load the credentials from the credential " +
                    "profiles file. Please make sure that your credentials file is at the, and is in valid format.", ex);
        }
    }
}
