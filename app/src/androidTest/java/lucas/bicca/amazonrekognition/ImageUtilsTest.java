package lucas.bicca.amazonrekognition;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.amazonaws.services.rekognition.model.Image;

import awsutils.ImageUtils;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ImageUtilsTest {

    @Test
    public void validarInstancia() throws Exception {
        String bucket = "s3bucket";
        String imageName = "image.jpg";
        Image image = ImageUtils.getImageUtil(bucket, imageName);
        assertEquals(bucket, image.getS3Object().getBucket());
        assertEquals(imageName, image.getS3Object().getName());
    }
}
