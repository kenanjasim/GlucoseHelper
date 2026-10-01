package com.kenjasim.glucosehelper.fragments;

import android.graphics.Color;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.kenjasim.glucosehelper.R;
import com.kenjasim.glucosehelper.other.GlucoseData;
import com.kenjasim.glucosehelper.other.LocalStore;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private LocalStore store;
    private List <Float> glucosePieChart;
    private Float Below = 0f;
    private Float Above = 0f;
    private Float On = 0f;
    private String[] xData = {"Above Target", "On Target", "Below Target"};
    private TextView averageTV, latestBGTV;
    PieChart pieChart;
    private final Runnable dataListener = new Runnable() {
        @Override
        public void run() {
            loadData();
        }
    };

    //The classes are declared here

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
        //The layout is inflated to the activity
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Home");
        //The title is set to home

        averageTV = (TextView) getActivity().findViewById(R.id.averageTV);
        latestBGTV = (TextView) getActivity().findViewById(R.id.latestBGTV);
        pieChart = (PieChart) getActivity().findViewById(R.id.pieChart);
        store = new LocalStore(getActivity());
        //The classes are linked to the classes declared in the xml file

        glucosePieChart = new ArrayList<>();
        //Here i am declaring the list as a new array list
    }

    private void loadData() {
        Float idealLevelInt;
        try {
            idealLevelInt = Float.parseFloat(store.getIdealLevel());
        } catch (NumberFormatException e) {
            idealLevelInt = 7f;
        }
        //Here i get the ideal level the user input (7 if it isn't a number)

        final Float idealLevelUBInt = idealLevelInt + 1;
        final Float idealLevelLBInt = idealLevelInt - 1;
        //This creates a range that allows the pie chart to work

        List<GlucoseData> entries = store.getEntries();
        glucosePieChart.clear();
        GlucoseData latest = null;

        for (GlucoseData glucoseData : entries) {
            try {
                glucosePieChart.add(Float.parseFloat(glucoseData.getBloodLevels()));
            } catch (NumberFormatException e) {
                continue;
            }
            //This cycles through the entries and adds the blood levels to the list

            if (latest == null || parseTimestamp(glucoseData) >= parseTimestamp(latest)) {
                latest = glucoseData;
            }
            //This keeps track of the most recent entry
        }

        if (latest != null) {
            latestBGTV.setText(latest.getBloodLevels());
            //This sets the textView text to the latest blood level
        }

        double average = 0.0;
        if (glucosePieChart.size() != 0){
            for (int i = 0; i < glucosePieChart.size(); i++)  {
                average += glucosePieChart.get(i);
                //This sets the double as the sum of the items in the list
            }

            Double ans = average/glucosePieChart.size();
            //It is then divided by the size of the list

            BigDecimal bd = new BigDecimal(ans);
            bd = bd.setScale(1, RoundingMode.HALF_UP);
            //This rounds it down to 1dp

            String ansS = bd.toString();
            averageTV.setText(ansS);
            //This sets the text view to the average

            //This calculates the average of the data

        }

        Below = 0f;
        Above = 0f;
        On = 0f;

        for (int i=0; i<glucosePieChart.size(); i++) {
            if (glucosePieChart.get(i) < idealLevelLBInt){
                Below = Below + 1f;
            }else if (glucosePieChart.get(i) > idealLevelUBInt){
                Above = Above + 1f;
            }else{
                On = On + 1f;
            }
        }
        //This tells us how much of the data is on below or above

        Float yData[] = {Below, On , Above};
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();

        for (int i = 0; i < yData.length; i++){
            yEntries.add(new PieEntry(yData[i], i));
        }
        for (int i = 0; i < xData.length; i++){
            xEntries.add(xData[i]);
        }
        PieDataSet pieDataSet = new PieDataSet(yEntries, "Blood Levels");
        pieDataSet.setSliceSpace(0);
        pieDataSet.setValueTextSize(0);

        ArrayList<Integer> colours = new ArrayList<>();
        colours.add(Color.YELLOW);
        colours.add(Color.GREEN);
        colours.add(Color.RED);

        pieDataSet.setColors(colours);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(0);
        pieChart.setTransparentCircleAlpha(0);

        //This creates and edits the piechart
    }

    private long parseTimestamp(GlucoseData glucoseData) {
        try {
            return Long.parseLong(glucoseData.getTimestamp());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalStore.addListener(dataListener);
        loadData();
        //This loads the data and listens for any changes
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalStore.removeListener(dataListener);
    }
}
