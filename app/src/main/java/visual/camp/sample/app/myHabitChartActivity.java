package visual.camp.sample.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class myHabitChartActivity extends AppCompatActivity {
    private BarChart barChart1;

    private float tmp_ability1;
    private float tmp_ability2;
    private float tmp_ability3;
    private int tmp_count;


    private float person_ability1;
    private float person_ability2;
    private float person_ability3;
    private int person_count;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_habit_chart);

        Intent intent = getIntent();
        tmp_ability1 = intent.getFloatExtra("tmp_ability1", 0.0f);
        tmp_ability2 = intent.getFloatExtra("tmp_ability2", 0.0f);
        tmp_ability3 = intent.getFloatExtra("tmp_ability3", 0.0f);
        tmp_count = intent.getIntExtra("tmp_count",0 );

        person_ability1 = intent.getFloatExtra("person_ability1", 0.0f);
        person_ability2 = intent.getFloatExtra("person_ability2", 0.0f);
        person_ability3 = intent.getFloatExtra("person_ability3", 0.0f);
        person_count = intent.getIntExtra("person_count",0 );

        Log.i("TAG", "tmp ability1!!!!!!!!!!!" +String.valueOf(tmp_ability1));
        Log.i("TAG", "tmp ability2!!!!!!!!!!!!" +String.valueOf(tmp_ability2));
        Log.i("TAG", "tmp ability3!!!!!!!!!!!!" +String.valueOf(tmp_ability3));
        Log.i("TAG", "tmp count!!!!!!!!!!!!" +String.valueOf(tmp_count));

        Log.i("TAG", "person ability1!!!!!!!!!!!!" +String.valueOf(person_ability1));
        Log.i("TAG", "person ability2!!!!!!!!!!!!" +String.valueOf(person_ability2));
        Log.i("TAG", "person ability3!!!!!!!!!!!!" +String.valueOf(person_ability3));
        Log.i("TAG", "person count!!!!!!!!!!!!" +String.valueOf(person_count));

        barChart1 = findViewById(R.id.myhabit_bar1);

        BarDataSet barDataSet1 = new BarDataSet(barEntries1(), "최근에 읽은 책");
        barDataSet1.setColor(Color.RED);
        BarDataSet barDataSet2 = new BarDataSet(barEntries2(), "다른 사람들이 읽은 책들    (시선 속도, 시선 이동 거리, 집중도)");
        barDataSet2.setColor(Color.BLUE);

        BarData data = new BarData (barDataSet1, barDataSet2);
        barChart1.setData(data);

        XAxis xAxis = barChart1.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChart1.setDragEnabled(true);
        barChart1.setVisibleXRangeMaximum(3);

        float barSpace = 0.08f;
        float groupSpace = 0.44f;
        data.setBarWidth(0.50f);

        barChart1.getXAxis().setAxisMinimum(0);
        barChart1.getXAxis().setAxisMaximum(0+barChart1.getBarData().getGroupWidth(groupSpace, barSpace) * 3);
        barChart1.getAxisLeft().setAxisMinimum(0);
        barChart1.groupBars(0, groupSpace, barSpace);

        barChart1.invalidate();
    }


    private ArrayList<BarEntry> barEntries1 () {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, tmp_ability1));
        barEntries.add(new BarEntry(2, tmp_ability2));
        barEntries.add(new BarEntry(3, tmp_ability3));

        return barEntries;

    }

    private ArrayList<BarEntry> barEntries2 () {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, person_ability1));
        barEntries.add(new BarEntry(2, person_ability2));
        barEntries.add(new BarEntry(3, person_ability3));

        return barEntries;

    }


}