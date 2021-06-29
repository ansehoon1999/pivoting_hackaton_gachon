package visual.camp.sample.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class ourHabitChartActivity extends AppCompatActivity {
    private BarChart barChart2;

    private float person_ability1;
    private float person_ability2;
    private float person_ability3;
    private int person_count;

    private float total_ability1;
    private float total_ability2;
    private float total_ability3;
    private int total_count;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_habit_chart);
        barChart2 = findViewById(R.id.ourHabit);

        Intent intent = getIntent();
        person_ability1 = intent.getFloatExtra("person_ability1", 0.0f);
        person_ability2 = intent.getFloatExtra("person_ability2", 0.0f);
        person_ability3 = intent.getFloatExtra("person_ability3", 0.0f);
        person_count = intent.getIntExtra("count",0 );

        total_ability1 = intent.getFloatExtra("total_ability1", 0.0f);
        total_ability2 = intent.getFloatExtra("total_ability2", 0.0f);
        total_ability3 = intent.getFloatExtra("total_ability3", 0.0f);
        total_count = intent.getIntExtra("total_count",0 );


        Log.i("TAG", "person ability1!!!!!!!!!!!" + String.valueOf(person_ability1));
        Log.i("TAG", "person ability2!!!!!!!!!!!!" +String.valueOf(person_ability2));
        Log.i("TAG", "person ability3!!!!!!!!!!!!" +String.valueOf(person_ability3));
        Log.i("TAG", "person ability3!!!!!!!!!!!!" +String.valueOf(person_count));

        Log.i("TAG", "our total ability1!!!!!!!!!!!!" +String.valueOf(total_ability1));
        Log.i("TAG", "our total ability2!!!!!!!!!!!!" +String.valueOf(total_ability2));
        Log.i("TAG", "our total ability3!!!!!!!!!!!!" +String.valueOf(total_ability3));
        Log.i("TAG", "our total count!!!!!!!!!!!!" +String.valueOf(total_count));



        BarDataSet barDataSet1 = new BarDataSet(barEntries1(), "내가 읽은 책들(시선 속도, 시선 이동 거리, 집중도)");
        barDataSet1.setColor(Color.RED);
        BarDataSet barDataSet2 = new BarDataSet(barEntries2(), "다른 사람들이 읽은 책(시선 속도, 시선 이동 거리, 집중도)");
        barDataSet2.setColor(Color.BLUE);

        BarData data = new BarData (barDataSet1, barDataSet2);
        barChart2.setData(data);

        XAxis xAxis = barChart2.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChart2.setDragEnabled(true);
        barChart2.setVisibleXRangeMaximum(3);

        float barSpace = 0.08f;
        float groupSpace = 0.44f;
        data.setBarWidth(0.50f);

        barChart2.getXAxis().setAxisMinimum(0);
        barChart2.getXAxis().setAxisMaximum(0+barChart2.getBarData().getGroupWidth(groupSpace, barSpace) * 3);
        barChart2.getAxisLeft().setAxisMinimum(0);
        barChart2.groupBars(0, groupSpace, barSpace);

        barChart2.invalidate();
    }


    private ArrayList<BarEntry> barEntries1 () {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, person_ability1));
        barEntries.add(new BarEntry(2, person_ability2));
        barEntries.add(new BarEntry(3, person_ability3));

        return barEntries;

    }

    private ArrayList<BarEntry> barEntries2 () {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, total_ability1));
        barEntries.add(new BarEntry(2, total_ability2));
        barEntries.add(new BarEntry(3, total_ability3));

        return barEntries;

    }


}