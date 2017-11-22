package awsutils;


import android.content.Context;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

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
}
