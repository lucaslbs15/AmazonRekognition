package lucas.bicca.amazonrekognition.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import lucas.bicca.amazonrekognition.R;
import lucas.bicca.amazonrekognition.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.activityMainCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                intent.putExtra(getString(R.string.intent_label_key),
                        getResources().getInteger(R.integer.intent_label_value));
                startActivity(intent);
            }
        });
    }

}
