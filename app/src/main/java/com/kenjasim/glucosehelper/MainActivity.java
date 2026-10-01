package com.kenjasim.glucosehelper;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.kenjasim.glucosehelper.fragments.CalcFragment;
import com.kenjasim.glucosehelper.fragments.FoodsFragment;
import com.kenjasim.glucosehelper.fragments.LogbookFragment;
import com.kenjasim.glucosehelper.fragments.MainFragment;
import com.kenjasim.glucosehelper.other.CarbData;
import com.kenjasim.glucosehelper.other.CircleTransform;
import com.kenjasim.glucosehelper.other.GlucoseData;
import com.kenjasim.glucosehelper.other.LocalStore;


import java.util.Date;



import static com.kenjasim.glucosehelper.R.mipmap.ic_launcher;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private LocalStore store;
    private NavigationView navigationView;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName;
    private ProgressDialog progressDialog;
    final Context context = this;
    private String carbratio, bgRatio, ideallevel;
    private final Runnable dataListener = new Runnable() {
        @Override
        public void run() {
            retrieveRatios();
            loadNavHeader();
        }
    };

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;

//Here all the classes that are needed are decalred




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        store = new LocalStore(this);
        if (savedInstanceState == null) {
            displayScreen(R.id.nav_home);
        }
        // This makes the default fragment the home fragment

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);

        //Links the FAB to the xml decalred classes

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               inputDialog();

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInputDialog();
            }
        });

        //Code for what happens when the FABs are pressed


        retrieveRatios();
        //The blood level ratios are retrieved


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //This declares the drawer layout and navigation drawer

        loadNavHeader();
        // load nav menu header data

    }

        private void loadNavHeader() {
            String name = store.getName();
            //The name is retrieved from the phone's storage

            if (name == null || name.isEmpty()){
                txtName.setText("NAME NOT SET");
                //If the name is null then the name is set to Name not set
            }else{
                txtName.setText(name);
                //If not then the name is set
            }

            Glide.with(this).load(ic_launcher)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .thumbnail(0.5f)
                    .transform(new CircleTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);
            //The app logo is set as the profile picture
        }

    @Override
    public void onBackPressed() {
        //This allows the user when the drawer is open to press a back button to close the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayScreen(item.getItemId());
        //when a particular item is pressed that item id is passes to the display screen method
        return true;
    }

    private void displayScreen(int itemID){
        Fragment fragment = null;
        //first a new instance of the fragment class is made and is set as null
        switch(itemID){
            case R.id.nav_home:
                fragment = new MainFragment();
                break;
            case R.id.nav_food:
                fragment = new FoodsFragment();
                break;
            case R.id.nav_calc:
                fragment = new CalcFragment();
                break;
            case R.id.nav_logbook:
                fragment = new LogbookFragment();
                break;
            //This checks through each of the classes and if they are the partcular item then the fragmet is set as which ever fragment is relevant
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            // If the case is for the settings item then the settings activity is opened
            case R.id.nav_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                String shareBody = "Get Glucose Helper";
                i.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                i.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(i, "Share using"));
                break;
            //if the case is the share item then the share action is opened


       }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FrameContent, fragment);
            ft.commit();
            //this starts the fragment transaction when the fragment isnt null
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        //This just links the drawer layout to the method
    }



    private void displayInputDialog(){
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.prompt_food, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText foodNameET = (EditText) promptsView.findViewById(R.id.foodNameET);
        final EditText carbsET = (EditText) promptsView.findViewById(R.id.carbsET);
        final EditText amountET = (EditText) promptsView.findViewById(R.id.amountET);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                progressDialog = ProgressDialog.show(MainActivity.this, "Data Is Being Added",
                                        "Please Wait...", true);
                                String foodName = foodNameET.getText().toString();
                                String carbs = carbsET.getText().toString();
                                String amount = amountET.getText().toString();

                                if (TextUtils.isEmpty(foodName)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(carbs)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(amount)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }else{
                                    String dataid = LocalStore.newId();

                                    CarbData carbData = new CarbData(foodName, carbs, amount, dataid);
                                    store.saveFood(carbData);
                                    progressDialog.dismiss();
                                    dialog.cancel();
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        //This shows the alert dialog for the inputing of food data

    }

    private void inputDialog(){
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.prompt_input, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);
        // Here the alert dialog is linked to the xml design file I made for it
        final EditText bloodLevelET = (EditText) promptsView.findViewById(R.id.bloodLevelET);
        final EditText carbsET = (EditText) promptsView.findViewById(R.id.carbsET);
        final EditText insulinET = (EditText) promptsView.findViewById(R.id.insulinET);
        final EditText notesET = (EditText) promptsView.findViewById(R.id.notesET);
        Button calcButton = (Button) promptsView.findViewById(R.id.calcButton);
        //Here the classes I declared in the xml files are linked to classes I declared here



        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bloodLevel = bloodLevelET.getText().toString();
                String carbs = carbsET.getText().toString();

                if (TextUtils.isEmpty(bloodLevel)){
                    bloodLevelET.setError("Please Enter Something!!");
                }if (TextUtils.isEmpty(carbs)){
                    carbsET.setError("Please Enter Something!!");
                }else{
                    Float BG = Float.parseFloat(bloodLevelET.getText().toString());

                    Float carb = Float.parseFloat(carbsET.getText().toString());

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

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                progressDialog = ProgressDialog.show(MainActivity.this, "Data Is Being Added",
                                        "Please Wait...", true);
                                String bloodReading = bloodLevelET.getText().toString();
                                String carbs = carbsET.getText().toString();
                                String insulin = insulinET.getText().toString();
                                String notes = notesET.getText().toString();

                                if (TextUtils.isEmpty(bloodReading)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(carbs)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(insulin)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }if (TextUtils.isEmpty(notes)){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Data Not Saved", Toast.LENGTH_SHORT).show();
                                }else{
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String ts = tsLong.toString();
                                    String dateTime = DateFormat.getDateTimeInstance().format(new Date());
                                    String dataid = LocalStore.newId();

                                    GlucoseData glucoseData = new GlucoseData(bloodReading, carbs, insulin, dateTime, dataid, ts, notes);
                                    store.saveEntry(glucoseData);
                                    progressDialog.dismiss();
                                    dialog.cancel();





                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        //This shows the alert dialog for the inputing of data

    }
    private void retrieveRatios(){
        carbratio = store.getCarbRatio();
        bgRatio = store.getBgRatio();
        ideallevel = store.getIdealLevel();
        //The ratios are loaded from the phone's storage (defaults are used if none are saved)
    }


    @Override
    public void onStart() {
        super.onStart();
        LocalStore.addListener(dataListener);
        dataListener.run();
        //This listens for changes to the saved data
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalStore.removeListener(dataListener);
        //and removes the listener when the activity is no longer visible
    }

}
