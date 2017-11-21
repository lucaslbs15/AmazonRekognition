package awsutils;

import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;

public class ImageUtils {

    public static Image getImageUtil(String bucket, String imageName) {
        return new Image()
                .withS3Object(new S3Object()
                        .withBucket(bucket)
                        .withName(imageName));
    }
}
