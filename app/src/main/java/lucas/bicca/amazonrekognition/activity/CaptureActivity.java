package lucas.bicca.amazonrekognition.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import awsutils.DetectLabelsUtils;
import lucas.bicca.amazonrekognition.R;
import lucas.bicca.amazonrekognition.databinding.ActivityCaptureBinding;
import utils.ImageUtils;

public class CaptureActivity extends AppCompatActivity {

    private final String LOG_TAG = CaptureActivity.class.getSimpleName();

    private int currentCodeRequest = -1;
    private ActivityCaptureBinding binding;
    private static final String RESOURCE_DIRECTORY = "android.resource://";
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_capture);

        setCurrentCodeRequest(getIntent());
        setImagePath("image_" + currentCodeRequest, "jpg");

        binding.activityCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void setImagePath(String imageName, String extension) {
        imagePath = imageName + "." + extension;
    }

    private void setCurrentCodeRequest(Intent intent) {
        if (intent == null) return;
        if (intent.hasExtra(getString(R.string.intent_label_key))) {
            currentCodeRequest = intent.getIntExtra(getString(R.string.intent_label_key), -1);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, currentCodeRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == currentCodeRequest) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageByte = stream.toByteArray();
            saveImage(imageByte);
            File file = ImageUtils.getFile(this, imagePath);
            if (file != null) {
                DetectLabelsUtils.detectLabels(file, 10, 77F);
            }
        }
    }

    private void saveImage(byte[] data) {
        try {
            FileOutputStream fos = openFileOutput(imagePath, Context.MODE_PRIVATE);
            fos.write(data);
        } catch (Exception ex) {
            Log.e(LOG_TAG, String.format("saveImage() - fileName: %s, Exception: %s", imagePath, ex.getMessage()));
        }
    }
}
