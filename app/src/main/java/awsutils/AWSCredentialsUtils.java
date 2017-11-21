package awsutils;


import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class AWSCredentialsUtils {

    public static AWSCredentials createCredentials(String profileName) {
        try {
            return new ProfileCredentialsProvider(profileName).getCredentials();
        } catch (Exception ex) {
            throw new AmazonClientException("Cannot load the credentials from the credential " +
                    "profiles file. Please make sure that your credentials file is at the " +
                    "correct location (/Users/userid/.aws/credentials), and is in valid format.", ex);
        }
    }
}
