package utils;

import android.content.Context;

import java.io.File;

public class ImageUtils {

    public static File getFile(Context context, String fileName) {
        return context.getFileStreamPath(fileName);
    }
}
