package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtils {

    private static final int SMALL_IMAGE_SAMPLE_FACTOR = 8;

    private static final String LOG_TAG = ImageUtils.class.getSimpleName();

    public static File getFile(Context context, String fileName) {
        return context.getFileStreamPath(fileName);
    }

    public static Bitmap getBitmap(String path) {
        File file = new File(path);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = SMALL_IMAGE_SAMPLE_FACTOR;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    public static void saveFile(Context context, byte[] data, String filePath) {
        try {
            FileOutputStream fos = context.openFileOutput(filePath, Context.MODE_PRIVATE);
            fos.write(data);
        } catch (Exception ex) {
            Log.e(LOG_TAG, String.format("saveImage() - fileName: %s, Exception: %s", filePath, ex.getMessage()));
        }
    }

    public static byte[] convertBitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static ByteBuffer getByteBuffer(Context context, String fileName) throws Exception {
        File file = getFile(context, fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        return ByteBuffer.wrap(IOUtils.toByteArray(fileInputStream));
    }

    public static ByteBuffer getByteBuffer(Context context, Bitmap bitmap) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return ByteBuffer.wrap(baos.toByteArray());
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
