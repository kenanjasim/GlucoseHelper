package com.kenjasim.glucosehelper.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kenjasim.glucosehelper.R;
import com.kenjasim.glucosehelper.other.LocalStore;

public class CalcFragment extends Fragment  {
    private EditText editText;
    private Spinner spinner;
    private Button calcButton;
    private String carbratio, bgRatio, ideallevel ;
    private TextView answerET;
    //Classes are declared

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calc, container, false);
        //The layout is inflated
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getActivity().setTitle("Calculator");
        super.onActivityCreated(savedInstanceState);
        editText = (EditText) getView().findViewById(R.id.editText);
        calcButton = (Button) getView().findViewById(R.id.calcButton);
        spinner = (Spinner) getView().findViewById(R.id.spinner);
        answerET = (TextView) getView().findViewById(R.id.Answer);
        //The classes are linked to the ones that were declared in the xml file
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.calculator_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //The spinner is loaded with items

        retrieveRatios();

        //ratios retrieved

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if (position == 0){
                    calcButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calcCarbs();
                        }
                    });



                }if (position == 1){

                    calcButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calcBloodSugar();
                        }
                    });

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });






    }
    private void retrieveRatios(){
        LocalStore store = new LocalStore(getActivity());
        carbratio = store.getCarbRatio();
        bgRatio = store.getBgRatio();
        ideallevel = store.getIdealLevel();
        //The ratios are loaded from the phone's storage
    }

    private void calcBloodSugar()
    {
        Float answer = Float.parseFloat(editText.getText().toString());
        String answerStr = answer.toString();
        if (TextUtils.isEmpty(answerStr)){
            editText.setError("Enter Something");
        }
        Float BG = Float.parseFloat(bgRatio);
        Float BGFloat = (1 / BG);
        Float idealFloat = Float.parseFloat(ideallevel);

        Float idealtakeactual = (answer - idealFloat);
        Float BSAns = (idealtakeactual * BGFloat);

        answerET.setText(BSAns.toString());

        //This calculates the amount for the blood sugars and setting the text
    }

    private void calcCarbs()
    {
        Float answer = Float.parseFloat(editText.getText().toString());
        String answerStr = answer.toString();
        if (TextUtils.isEmpty(answerStr)){
            editText.setError("Enter Something");
        }
        Float carb = Float.parseFloat(carbratio);
        Float carbFloat = (1 / carb);

        Float number = answer * carbFloat;
        String numberStr = number.toString();

        answerET.setText(numberStr);

        //This calculates the amount for the carbs and setting the text
    }
}
