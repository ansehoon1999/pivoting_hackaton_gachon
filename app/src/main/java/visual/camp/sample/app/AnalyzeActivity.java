package visual.camp.sample.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AnalyzeActivity extends AppCompatActivity {

    private BarChart barChart1;
    private static float real_tmp_ability1;
    private static float real_tmp_ability2;
    private static float real_tmp_ability3;

    private static float person_ability1;
    private static float person_ability2;
    private static float person_ability3;
    private static int person_count;

    private static float total_ability1;
    private static float total_ability2;
    private static float total_ability3;
    private static int total_count;

    private double concentration; // 집중도
    private double read_speed;
    private double reverse_rate;
    private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze2);


        Intent intent = getIntent();
        real_tmp_ability1 = intent.getFloatExtra("tmp_ability1", 0.0f);
        real_tmp_ability2 = intent.getFloatExtra("tmp_ability2", 0.0f);
        real_tmp_ability3 = intent.getFloatExtra("tmp_ability3", 0.0f);

        person_ability1 = intent.getFloatExtra("person_ability1", 0.0f);
        person_ability2 = intent.getFloatExtra("person_ability2", 0.0f);
        person_ability3 = intent.getFloatExtra("person_ability3", 0.0f);
        person_count = intent.getIntExtra("person_count",0 );

        total_ability1 = intent.getFloatExtra("total_ability1", 0.0f);
        total_ability2 = intent.getFloatExtra("total_ability2", 0.0f);
        total_ability3 = intent.getFloatExtra("total_ability3", 0.0f);
        total_count = intent.getIntExtra("total_count",0 );

        read_speed = intent.getDoubleExtra("read speed", 0);
        concentration = intent.getLongExtra("concentration", 0);
        reverse_rate = intent.getDoubleExtra("reverse rate", 0);


        Log.i("TAG", "tmp ability1!!!!!!!!!!!" +String.valueOf(real_tmp_ability1));
        Log.i("TAG", "tmp ability2!!!!!!!!!!!!" +String.valueOf(real_tmp_ability2));
        Log.i("TAG", "tmp ability3!!!!!!!!!!!!" +String.valueOf(real_tmp_ability3));


        Log.i("TAG", "person ability1!!!!!!!!!!!" + String.valueOf(person_ability1));
        Log.i("TAG", "person ability2!!!!!!!!!!!!" +String.valueOf(person_ability2));
        Log.i("TAG", "person ability3!!!!!!!!!!!!" +String.valueOf(person_ability3));
        Log.i("TAG", "person ability3!!!!!!!!!!!!" +String.valueOf(person_count));

        Log.i("TAG", "our total ability1!!!!!!!!!!!!" +String.valueOf(total_ability1));
        Log.i("TAG", "our total ability2!!!!!!!!!!!!" +String.valueOf(total_ability2));
        Log.i("TAG", "our total ability3!!!!!!!!!!!!" +String.valueOf(total_ability3));
        Log.i("TAG", "our total count!!!!!!!!!!!!" +String.valueOf(total_count));

        Log.i("TAG", "our concentration !!!!!!!!!!!!" +String.valueOf(concentration));
        Log.i("TAG", "our read speed!!!!!!!!!!!!" +String.valueOf(read_speed));
        Log.i("TAG", "our reverse rate!!!!!!!!!!!!" +String.valueOf(reverse_rate));

        /*
            읽는 속도 = ability1
            역행률 = ability2
            집중도 = ability3
        */


            person_ability1 = (float) ((person_ability1 *  person_count + read_speed ) / (person_count + 1));
            person_ability2 = (float) ((person_ability2 *  person_count + reverse_rate ) / (person_count + 1));
            person_ability3 = (float) ((person_ability3 *  person_count + concentration ) / (person_count + 1));

            total_ability1 = (float) ((total_ability1 * total_count + read_speed) / (total_count + 1));
            total_ability2 = (float) ((total_ability2 *  total_count + reverse_rate ) / (total_count + 1));
            total_ability3 = (float) ((total_ability3 *  total_count + concentration ) / (total_count + 1));

        barChart1 = findViewById(R.id.myhabit_bar1);

        Ability a1 = new Ability();
       a1.ability1 = String.valueOf((long)read_speed);
       a1.ability2 = String.valueOf((long)reverse_rate);
       a1.ability3 = String.valueOf((long)concentration);
       a1.uid = myUid;
       //tmp
        //
        FirebaseDatabase.getInstance().getReference().child("tmp")
                .child(myUid).setValue(a1);
        //


        //person
        Ability a2 = new Ability();
        a2.ability1 = String.valueOf((long)person_ability1);
        a2.ability2 = String.valueOf((long)person_ability2);
        a2.ability3 = String.valueOf((long)person_ability3);


        a2.count = String.valueOf(person_count + 1);

        a2.uid = myUid;
        FirebaseDatabase.getInstance().getReference().child("person")
                .child(myUid).setValue(a2);

        //total
        Ability a3 = new Ability();
        a3.ability1 = String.valueOf((long)total_ability1);
        a3.ability2 = String.valueOf((long)total_ability2);
        a3.ability3 = String.valueOf((long)total_ability3);
        a3.count = String.valueOf(total_count + 1);
        FirebaseDatabase.getInstance().getReference().child("total")
                .child("total").setValue(a3);


        BarDataSet barDataSet1 = new BarDataSet(barEntries1(), "최근에 읽은 책");
        barDataSet1.setColor(Color.RED);
        BarDataSet barDataSet2 = new BarDataSet(barEntries2(), "이전에 읽은 책들");
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
        barEntries.add(new BarEntry(1, (float) read_speed));
        barEntries.add(new BarEntry(2, (float) reverse_rate));
        barEntries.add(new BarEntry(3, (float) concentration));

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