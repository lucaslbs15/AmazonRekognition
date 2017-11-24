package awsutils;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;

import java.nio.ByteBuffer;

public class CompareUtils {

    public static CompareFacesResult compareImages(Context context, ByteBuffer byteImage01, ByteBuffer byteImage02, float threshold) {
        AmazonRekognition amazonRekognition = AmazonRekognitionUtils.getAmazonRekognition(context);

        Image image01 = new Image().withBytes(byteImage01);
        Image image02 = new Image().withBytes(byteImage02);

        CompareFacesRequest compareFacesRequest = new CompareFacesRequest()
                .withSourceImage(image01)
                .withTargetImage(image02)
                .withSimilarityThreshold(threshold);
        return amazonRekognition.compareFaces(compareFacesRequest);
    }
}
