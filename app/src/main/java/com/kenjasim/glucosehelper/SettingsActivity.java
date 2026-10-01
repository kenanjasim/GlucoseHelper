package com.kenjasim.glucosehelper;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.kenjasim.glucosehelper.other.LocalStore;

import java.util.*;


public class SettingsActivity extends AppCompatActivity{

    private String dName;
    final Context context = this;
    private LocalStore store;
    private Button deleteButton, saveDataButton;
    private String carbratio, bgRatio, ideallevel;
    private EditText carbRatioET, insSensET, idealET;
    private ListView accountLV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        store = new LocalStore(this);
        deleteButton = (Button) findViewById(R.id.DeleteAccount);
        saveDataButton = (Button) findViewById(R.id.saveDataButton);
        carbRatioET = (EditText) findViewById(R.id.carbRatioET);
        insSensET = (EditText) findViewById(R.id.insSensET);
        idealET = (EditText) findViewById(R.id.idealET);
        accountLV = (ListView) findViewById(R.id.accountLV);

        retrieveRatios();
        getUserDetails();
        addItemsToList();




        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();

            }
        });

        accountLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    changeName();
                }

            }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() { //Creates a yes no option
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                store.clearAll();
                                Toast.makeText(SettingsActivity.this, "All data has been deleted", Toast.LENGTH_SHORT).show();
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                //This deletes all saved data after the user is sure
            }
        });




    }

    private void retrieveRatios(){
        carbratio = store.getCarbRatio();
        bgRatio = store.getBgRatio();
        ideallevel = store.getIdealLevel();
        //The ratios are loaded from the phone's storage

        carbRatioET.setText(carbratio);
        insSensET.setText(bgRatio);
        idealET.setText(ideallevel);
    }

    public void saveData(){
        String carbChange = carbRatioET.getText().toString();
        String insSensChange = insSensET.getText().toString();
        String idealChange = idealET.getText().toString();

        if (TextUtils.isEmpty(carbChange)){
            carbRatioET.setError("Please Enter A Number");

        }if(TextUtils.isEmpty(insSensChange)){
            insSensET.setError("Please Enter A Number");
        }if(TextUtils.isEmpty(idealChange)){
            idealET.setError("Please Enter A Number");
            //Validation
        }else{
            store.setRatios(carbChange, insSensChange, idealChange);

            Toast.makeText(SettingsActivity.this, "Data Has Been Saved", Toast.LENGTH_SHORT).show();

        }

    }

    public void getUserDetails()
    {
        dName = store.getName();
        if (dName == null || dName.isEmpty()){
            dName = "NOT SET";
        }
        //Validation
    }

    public void changeName()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                store.setName(userInput.getText().toString().trim());
                                getUserDetails();
                                addItemsToList();
                                //The name is saved and the list is refreshed

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        //This allows the user to input a new name
    }

    public void addItemsToList()
    {
        LinkedHashMap<String,String> accountMap = new LinkedHashMap<>();
        accountMap.put("Name:", dName);

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.text1, R.id.text2});


        Iterator rit = accountMap.entrySet().iterator();
        while (rit.hasNext())
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)rit.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }
        //Items are looped through to add them to the list

        accountLV.setAdapter(adapter);
        //Items are shown in the listviews

    }

}
