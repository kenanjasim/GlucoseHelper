package com.kenjasim.glucosehelper;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kenjasim.glucosehelper.other.LocalStore;

public class RatiosActivity extends AppCompatActivity {

    private EditText carbRatio, bgRatio, idealLevel;
    private Button saveButton;
    private LocalStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratios);

        carbRatio = (EditText) findViewById(R.id.carbRatio);
        bgRatio = (EditText) findViewById(R.id.bgRatio);
        idealLevel = (EditText) findViewById(R.id.idealLevel);
        saveButton = (Button) findViewById(R.id.saveButton);
        store = new LocalStore(this);
        carbRatio.setText(store.getCarbRatio());
        bgRatio.setText(store.getBgRatio());
        idealLevel.setText(store.getIdealLevel());
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                store.setRatios(carbRatio.getText().toString(),
                        bgRatio.getText().toString(),
                        idealLevel.getText().toString());

            }
        });















    }


}
