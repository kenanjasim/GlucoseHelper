package com.kenjasim.glucosehelper.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.kenjasim.glucosehelper.R;
import com.kenjasim.glucosehelper.other.CarbAdapter;
import com.kenjasim.glucosehelper.other.CarbData;
import com.kenjasim.glucosehelper.other.LocalStore;

import java.util.ArrayList;
import java.util.List;


public class FoodsFragment extends Fragment {
    private LocalStore store;
    private final Runnable dataListener = new Runnable() {
        @Override
        public void run() {
            loadFoods();
        }
    };
    private ListView foodslv;
    private List<CarbData> foodsDataList;
    private ProgressDialog progressDialog;
    //Classes are declared


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_foods, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Foods");
        store = new LocalStore(getActivity());
        foodslv = (ListView) getActivity().findViewById(R.id.foodsLV);
        //The classes are linked to the xml classes

        foodsDataList = new ArrayList<>();
        //The options for the alert dialog is made and added to an array

        final CharSequence options[] = new CharSequence[] {"Edit Selection", "Delete Selection"};
        foodslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick an Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0){
                            LayoutInflater li = LayoutInflater.from(getActivity());
                            final View promptsView = li.inflate(R.layout.prompt_food, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    getActivity());

                            alertDialogBuilder.setView(promptsView);
                            final CarbData carbData = foodsDataList.get(position);

                            String oldFoodName = carbData.getFoodName();
                            String oldCarbs = carbData.getCarbs();
                            String oldAmount = carbData.getAmount();
                            final String dataId = carbData.getDataid();

                            final EditText foodNameET = (EditText) promptsView.findViewById(R.id.foodNameET);
                            final EditText carbsET = (EditText) promptsView.findViewById(R.id.carbsET);
                            final EditText amountET = (EditText) promptsView.findViewById(R.id.amountET);

                            foodNameET.setText(oldFoodName);
                            carbsET.setText(oldCarbs);
                            amountET.setText(oldAmount);

                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    progressDialog = ProgressDialog.show(getActivity(), "Data Is Being Added",
                                                            "Please Wait...", true);
                                                    String foodName = foodNameET.getText().toString();
                                                    String carbs = carbsET.getText().toString();
                                                    String amount = amountET.getText().toString();

                                                    if (TextUtils.isEmpty(foodName)){
                                                        foodNameET.setError("Enter Something");
                                                    }if (TextUtils.isEmpty(carbs)){
                                                        carbsET.setError("Enter Something");
                                                    }if (amountET.getText() == null){
                                                        amountET.setError("Enter Something");
                                                    }else{

                                                        CarbData carbData = new CarbData(foodName, carbs, amount, dataId);
                                                        store.saveFood(carbData);
                                                        dialog.cancel();
                                                        progressDialog.dismiss();

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

                        }else{
                            CarbData carbData = foodsDataList.get(position);
                            String dataid = carbData.getDataid();
                            store.deleteFood(dataid);

                            //This shows the data for deleting the food data
                        }

                    }
                });
                builder.show();
            }
        });



    }

    private void loadFoods() {
        foodsDataList.clear();
        foodsDataList.addAll(store.getFoods());
        //The foods are loaded from the phone's storage

        CarbAdapter carbAdapter = new CarbAdapter(getActivity(), foodsDataList);
        foodslv.setAdapter(carbAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalStore.addListener(dataListener);
        loadFoods();
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalStore.removeListener(dataListener);
    }

}
