package awsutils;

import android.content.Context;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;

public class AmazonRekognitionUtils {

    public static AmazonRekognition getAmazonRekognition(Context context) {
        AmazonRekognitionClient amazonRekognitionClient = new AmazonRekognitionClient(AWSCredentialsUtils.createCredentials(context));
        amazonRekognitionClient.setRegion(Region.getRegion(Regions.DEFAULT_REGION));
        return amazonRekognitionClient;
    }
}
