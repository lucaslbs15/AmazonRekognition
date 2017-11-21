package awsutils;

import android.util.Log;

import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import com.amazonaws.services.rekognition.model.Image;

public class DetectLabelsUtils {

    private final static String LOG_TAG = DetectLabelsUtils.class.getSimpleName();

    public static void detectLabels(File file, int maxLabels, float minConfidence) {
        //maxLabels = 10, minConfidence = 77F
        ByteBuffer imageBytes = getImageBytes(file);
        if (imageBytes == null) return;

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image().withBytes(imageBytes))
                .withMaxLabels(maxLabels)
                .withMinConfidence(minConfidence);

        try {
            DetectLabelsResult result = AmazonRekognitionUtils.getAmazonRekognition().detectLabels(request);
            List<Label> labelList = result.getLabels();
            Log.i(LOG_TAG, String.format("Detected labels for %s", file.getName()));
            for (Label label : labelList) {
                Log.i(LOG_TAG, String.format("%s: %s", label.getName(), label.getConfidence().toString()));
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, String.format("detectLabels() - fileName: %s, Exception: %s", file.getName(), ex.getMessage()));
        }
    }

    private static ByteBuffer getImageBytes(File file) {
        InputStream inputStream;
        ByteBuffer byteBuffer;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Log.e(LOG_TAG, String.format("getImageBytes() - InputStream exception: %s", ex.getMessage()));
            return null;
        }

        try {
            byteBuffer = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (IOException ex) {
            Log.e(LOG_TAG, String.format("getImageBytes() - ByteBuffer exception: %s", ex.getMessage()));
            return null;
        }

        return byteBuffer;
    }
}
