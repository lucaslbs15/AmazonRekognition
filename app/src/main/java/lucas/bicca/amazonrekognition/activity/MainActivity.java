package lucas.bicca.amazonrekognition.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import awsutils.CollectionUtils;
import lucas.bicca.amazonrekognition.R;
import lucas.bicca.amazonrekognition.databinding.ActivityMainBinding;
import utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private String collectionId;
    private int collectionIdStatusCode = -1;
    private int COLLECTION_ID_STATUS_CODE_OK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initListeners();
    }

    private void initListeners() {
        initCaptureListener();
        initCreateCollectionListener();
        initStoreFaceListener();
        initCompareImagesListener();
    }

    private void initCaptureListener() {
        binding.activityMainCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetectLabels();
            }
        });
    }

    private void initStoreFaceListener() {
        binding.activityMainStoreFaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionId = binding.activityMainCollectionIdToStore.getText().toString();
                if (TextUtils.isEmpty(collectionId)) {
                    Toast.makeText(MainActivity.this, getString(R.string.activity_main_collection_id_empty), Toast.LENGTH_SHORT).show();
                } else {
                    openStoreFace();
                }
            }
        });
    }

    private void initCompareImagesListener() {
        binding.activityMainCompareImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CompareTwoImagesActivity.class));
            }
        });
    }

    private void openDetectLabels() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        intent.putExtra(getString(R.string.intent_code_request_key),
                getResources().getInteger(R.integer.intent_label_value));
        startActivity(intent);
    }

    private void openStoreFace() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        intent.putExtra(getString(R.string.intent_code_request_key),
                getResources().getInteger(R.integer.intent_store_face_value));
        intent.putExtra(getString(R.string.intent_collection_id), collectionId);
        startActivity(intent);
    }

    private void initCreateCollectionListener() {
        binding.activityMainCreateCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionId = binding.activityMainCollectionId.getText().toString();

                if (TextUtils.isEmpty(collectionId)) {
                    Toast.makeText(MainActivity.this, getString(R.string.activity_main_collection_id_empty), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        createCollectionId(collectionId);
                    } catch (Exception ex) {
                        showCollectionIdError(ex.getMessage());
                        return;
                    }
                    showCollectionIdStatusMessage();
                }
            }
        });
    }

    private void showCollectionIdError(String exceptionMessage) {
        String message = String.format("%s: %s",
                getString(R.string.activity_main_collection_id_error), exceptionMessage);
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showCollectionIdStatusMessage() {
        if (collectionIdStatusCode == COLLECTION_ID_STATUS_CODE_OK) {
            Toast.makeText(MainActivity.this, getString(R.string.activity_main_collection_id_ok), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.activity_main_collection_id_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void createCollectionId(String collectionId) {
        NetworkUtils.initStricModeThreadPolicy();
        collectionIdStatusCode = CollectionUtils.createCollection(this, collectionId);
    }

}
