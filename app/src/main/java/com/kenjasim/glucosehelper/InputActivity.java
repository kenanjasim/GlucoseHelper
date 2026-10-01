package com.kenjasim.glucosehelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kenjasim.glucosehelper.other.GlucoseData;
import com.kenjasim.glucosehelper.other.LocalStore;

import java.security.Timestamp;
import java.util.Date;
import java.util.HashMap;

public class InputActivity extends AppCompatActivity {

    private EditText bgET, carbET, insulinET, notesET;
    private Button saveButton,button;
    private LocalStore store;
    private ProgressDialog progress;
//    private Float bgInsulin, carbInsulin, Insulin;
    private String carbratio, bgRatio, ideallevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        bgET = (EditText) findViewById(R.id.bgET);
        carbET = (EditText) findViewById(R.id.carbsET);
        insulinET = (EditText) findViewById(R.id.insulinET);
        saveButton = (Button) findViewById(R.id.saveButton);
        notesET = (EditText) findViewById(R.id.notesET);
        button = (Button) findViewById(R.id.Button);
        store = new LocalStore(this);
        carbratio = store.getCarbRatio();
        bgRatio = store.getBgRatio();
        ideallevel = store.getIdealLevel();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bgET == null){
                    bgET.setError("Please Enter Something!!");
                }if (carbET == null){
                    carbET.setError("Please Enter Something!!");
                }else{
                    Float BG = Float.parseFloat(bgET.getText().toString());

                    Float carb = Float.parseFloat(carbET.getText().toString());

                    Float bgRat = Float.parseFloat(bgRatio);

                    Float bgRatioFloat = (1/ bgRat);

                    Float carbratioFloat = (1/ Float.parseFloat(carbratio));

                    Float bgInsulin = (BG - Float.parseFloat(ideallevel)) * bgRatioFloat;

                    Float carbInsulin = (carb * carbratioFloat);

                    Float Insulin = (bgInsulin + carbInsulin);

                    insulinET.setText(String.valueOf(Insulin));
                }



            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bgET == null){
                    bgET.setError("Please Enter Something!!");
                }if (carbET == null) {
                    carbET.setError("Please Enter Something!!");
                }if (insulinET == null){
                    insulinET.setError("Please Enter Something!!");
                }if (notesET == null){
                    notesET.setText("N/A");
                }else{
                    progress = ProgressDialog.show(InputActivity.this, "Data Saving",
                            "Please Wait...", true);
                    addReading();

                }

            }
        });



            }
    private void addReading(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String BloodLevels = (bgET.getText().toString());
        String Carbohydrates = (carbET.getText().toString());
        String InsulinTaken = (insulinET.getText().toString());
        String DateandTime = DateFormat.getDateTimeInstance().format(new Date());
        String Notes = (notesET.getText().toString());

        String id = LocalStore.newId();

        GlucoseData glucoseData = new GlucoseData(BloodLevels, Carbohydrates, InsulinTaken, DateandTime, id, ts, Notes);

        store.saveEntry(glucoseData);
        progress.dismiss();
        Intent i = new Intent(InputActivity.this, MainActivity.class);
        startActivity(i);


    }





    }

