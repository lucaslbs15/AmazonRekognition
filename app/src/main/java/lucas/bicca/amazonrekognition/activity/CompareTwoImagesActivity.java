package lucas.bicca.amazonrekognition.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.services.rekognition.model.CompareFacesResult;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import awsutils.CompareUtils;
import lucas.bicca.amazonrekognition.R;
import lucas.bicca.amazonrekognition.databinding.ActivityCompareTwoImagesBinding;
import utils.ImageUtils;
import utils.NetworkUtils;

public class CompareTwoImagesActivity extends AppCompatActivity {

    private ActivityCompareTwoImagesBinding binding;
    private final int LEFT_IMAGE_CODE_REQUEST = 10;
    private final int RIGHT_IMAGE_CODE_REQUEST = 15;
    private final int GALLERY_LEFT_IMAGE_CODE_REQUEST = 11;
    private final int GALLERY_RIGHT_IMAGE_CODE_REQUEST = 16;
    private int currentCodeRequest = -1;
    private byte[] byteImageLeft;
    private byte[] byteImageRight;
    private Bitmap bitmapLeft;
    private Bitmap bitmapRight;
    private final String LOG_TAG = CompareTwoImagesActivity.class.getSimpleName();
    private final String fileNameLeft = "leftImage.png";
    private final String fileNameRight = "rightImage.png";
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compare_two_images);
        initListeners();
    }

    private void initListeners() {
        initLeftButtonListener();
        initRightButtonListener();
        initCompareButtonListener();
        initLeftOpenGalleryButtonListener();
        iniRightOpenGalleryButtonListener();
    }

    private void initLeftButtonListener() {
        binding.activityCompareTwoImagesLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCodeRequest = LEFT_IMAGE_CODE_REQUEST;
                dispatchTakePictureIntent();
            }
        });
    }

    private void initRightButtonListener() {
        binding.activityCompareTwoImagesRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCodeRequest = RIGHT_IMAGE_CODE_REQUEST;
                dispatchTakePictureIntent();
            }
        });
    }

    private void initCompareButtonListener() {
        binding.activityCompareTwoImagesCompareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCompareImagesThread();
            }
        });
    }

    private void initLeftOpenGalleryButtonListener() {
        binding.activityCompareTwoImagesLeftGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCodeRequest = GALLERY_LEFT_IMAGE_CODE_REQUEST;
                openGallery(fileNameLeft);
            }
        });
    }

    private void iniRightOpenGalleryButtonListener() {
        binding.activityCompareTwoImagesRightGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCodeRequest = GALLERY_RIGHT_IMAGE_CODE_REQUEST;
                openGallery(fileNameRight);
            }
        });
    }

    private void openGallery(String fileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File f = new File(Environment.getExternalStorageDirectory(), fileName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imageUri = Uri.fromFile(f);
        startActivityForResult(intent, currentCodeRequest);
    }

    private void initCompareImagesThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    compareImages();
                } catch (Exception ex) {
                    Log.i(LOG_TAG, "initCompareButtonListener() - Exception: " + ex.getMessage());
                    Toast.makeText(CompareTwoImagesActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        thread.start();
    }

    private void compareImages() throws Exception {
        if (isValidCompare()) {
            NetworkUtils.initStricModeThreadPolicy();
            int threshold = Integer.parseInt(binding.activityCompareTwoImagesSimilarity.getText().toString());

            ByteBuffer byteBufferLeft = ImageUtils.getByteBuffer(this, bitmapLeft);
            ByteBuffer byteBufferRight = ImageUtils.getByteBuffer(this, bitmapRight);

            showCompareFacesResult(CompareUtils.compareImages(this, byteBufferLeft, byteBufferRight, threshold));
        } else {
            Toast.makeText(this, getString(R.string.activity_compare_two_images_empty_image), Toast.LENGTH_LONG).show();
        }
    }

    private void showCompareFacesResult(CompareFacesResult result) {
        Log.i(LOG_TAG, "showCompareFacesResult: " + result.toString());
    }

    private boolean isValidCompare() {
        boolean isValid = true;
        if (byteImageLeft == null) {
            isValid = false;
            Toast.makeText(this, getString(R.string.activity_compare_two_images_left_empty_image), Toast.LENGTH_LONG).show();
        }

        if (byteImageRight == null) {
            isValid = false;
            Toast.makeText(this, getString(R.string.activity_compare_two_images_right_empty_image), Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(binding.activityCompareTwoImagesSimilarity.getText().toString())) {
            isValid = false;
            Toast.makeText(this, getString(R.string.activity_compare_two_images_similarity_empty_image), Toast.LENGTH_LONG).show();
        }
        return isValid;
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, currentCodeRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && isCameraCapture()) {
            Bundle extras = data.getExtras();

            Bitmap bitmap = (Bitmap) extras.get("data");
            saveImage(bitmap);
        } else if (resultCode == RESULT_OK && isGalleryCapture()) {
            try {
                Bitmap bitmap = ImageUtils.getBitmap(this, data.getData());
                saveImage(bitmap);
            } catch (IOException ex) {
                Log.e(LOG_TAG, "onActivityResult - Exception: " + ex.getMessage());
            }
        } else {
            Log.e(LOG_TAG, "onActivityResult error!");
        }
    }

    private void saveImage(Bitmap bitmap) {
        setBitmap(bitmap);
        setImageView(bitmap);

        byte[] byteImage = ImageUtils.convertBitmapToBytes(bitmap);
        saveImageFile(byteImage);
        setByteImage(byteImage);
    }

    private boolean isCameraCapture() {
        return currentCodeRequest == LEFT_IMAGE_CODE_REQUEST || RIGHT_IMAGE_CODE_REQUEST == currentCodeRequest;
    }

    private boolean isGalleryCapture() {
        return currentCodeRequest == GALLERY_LEFT_IMAGE_CODE_REQUEST || currentCodeRequest == GALLERY_RIGHT_IMAGE_CODE_REQUEST;
    }

    private void saveImageFile(byte[] image) {
        String currentFileName = isLeftImageCaptured() ? fileNameLeft : fileNameRight;
        ImageUtils.saveFile(this, image, currentFileName);
    }

    private void setByteImage(byte[] image) {
        if (isLeftImageCaptured()) {
            byteImageLeft = image;
        } else {
            byteImageRight = image;
        }
    }

    private void setBitmap(Bitmap bitmap) {
        if (isLeftImageCaptured()) {
            bitmapLeft = bitmap;
        } else {
            bitmapRight = bitmap;
        }
    }

    private boolean isLeftImageCaptured() {
        return currentCodeRequest == LEFT_IMAGE_CODE_REQUEST
                || currentCodeRequest == GALLERY_LEFT_IMAGE_CODE_REQUEST;
    }

    private void setImageView(Bitmap bitmap) {
        if (isLeftImageCaptured()) {
            binding.activityCompareTwoImagesLeftImage.setImageBitmap(bitmap);
        } else {
            binding.activityCompareTwoImagesRightImage.setImageBitmap(bitmap);
        }
    }
}
