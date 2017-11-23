package awsutils;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;

public class AmazonRekognitionUtils {

    public static AmazonRekognition getAmazonRekognition(Context context) {
        /*return AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.DEFAULT_REGION)
                .withCredentials(new AWSStaticCredentialsProvider(AWSCredentialsUtils.createCredentials(context)))
                .build();*/
        Log.i("AmazonRekognition", "getAmazonRekognition will call builder attribute");
        AmazonRekognitionClientBuilder builder = AmazonRekognitionClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWSCredentialsUtils.createCredentials(context)))
                .withRegion(Regions.DEFAULT_REGION);
        Log.i("AmazonRekognition", "getAmazonRekognition will return builder attribute");
        return builder.build();
    }

    public static AmazonRekognition getAmazonRekognition() {
        Credentials credentials = AWSCredentialsUtils.getSessionCredentials();
        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                credentials.getAccessKeyId(), credentials.getSecretAccessKey(), credentials.getSessionToken());


        return AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.DEFAULT_REGION)
                .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                .build();
    }
}
