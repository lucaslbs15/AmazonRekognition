package lucas.bicca.amazonrekognition.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import awsutils.DetectLabelsUtils;
import awsutils.StoreFaceUtils;
import lucas.bicca.amazonrekognition.R;
import lucas.bicca.amazonrekognition.databinding.ActivityCaptureBinding;
import utils.ImageUtils;
import utils.NetworkUtils;

public class CaptureActivity extends AppCompatActivity {

    public static final String MY_BUCKET = "UnicredS3Bucket";
    private final String LOG_TAG = CaptureActivity.class.getSimpleName();

    private int currentCodeRequest = -1;
    private String collectionId;
    private ActivityCaptureBinding binding;
    private static final String RESOURCE_DIRECTORY = "android.resource://";
    private String fileName;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_capture);

        setCurrentCodeRequest(getIntent());
        setCollectionId(getIntent());

        binding.activityCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileName = binding.activityCaptureFileName.getText().toString();
                if (TextUtils.isEmpty(fileName)) {
                    Toast.makeText(CaptureActivity.this, getString(R.string.activity_capture_file_name_empty), Toast.LENGTH_SHORT).show();
                } else {
                    setImagePath(fileName, "jpg");
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private void setImagePath(String imageName, String extension) {
        imagePath = imageName + "." + extension;
    }

    private void setCurrentCodeRequest(Intent intent) {
        if (intent == null) return;
        if (intent.hasExtra(getString(R.string.intent_code_request_key))) {
            currentCodeRequest = intent.getIntExtra(getString(R.string.intent_code_request_key), -1);
        }
    }

    private void setCollectionId(Intent intent) {
        if (intent == null) return;
        if (intent.hasExtra(getString(R.string.intent_collection_id))) {
            collectionId = intent.getStringExtra(getString(R.string.intent_collection_id));
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
                NetworkUtils.initStricModeThreadPolicy();
                DetectLabelsUtils.detectLabels(this, file, 10, 77F);
                StoreFaceUtils.storeFace(this, collectionId, fileName, MY_BUCKET);
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
