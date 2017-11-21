package awsutils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;

public class AmazonRekognitionUtils {

    public static AmazonRekognition getAmazonRekognition() {
        return AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(AWSCredentialsUtils.createCredentials("UserAdmin")))
                .build();
    }
}