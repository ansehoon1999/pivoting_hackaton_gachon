package visual.camp.sample.app.Third;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import visual.camp.sample.app.Ability;
import visual.camp.sample.app.R;


public class Third_Fragment1 extends Fragment {
    private Button button;
    public static final float MAX = 12, MIN = 1f;
    public static final int NB_QUALITIES = 5;
    private RadarChart chart1;
    private RadarChart chart2;

    private DatabaseReference reference;
    private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private float total_count;
    private float total_ability1;
    private float total_ability2;
    private float total_ability3;
    private float total_ability4;
    private float total_ability5;

    private int personal_count;
    private float personal_ability1;
    private float personal_ability2;
    private float personal_ability3;
    private float personal_ability4;
    private float personal_ability5;

    private float onetime_ability1;
    private float onetime_ability2;
    private float onetime_ability3;
    private float onetime_ability4;
    private float onetime_ability5;
    ArrayList<RadarEntry> employee1 = new ArrayList<>();
    ArrayList<RadarEntry> employee2 = new ArrayList<>();

    RadarDataSet set1;
    RadarDataSet set2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_third_1, container, false);


        chart1 = rootview.findViewById(R.id.main_chart1);
        chart1.setBackgroundColor(Color.rgb(60, 65, 82));
        chart1.getDescription().setEnabled(false);
        chart1.setWebLineWidth(1f);
        chart1.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_background));
        
        chart1.setWebColor(Color.BLACK);
        chart1.setWebLineWidth(1f);
        chart1.setWebColorInner(Color.BLACK);
        chart1.setWebAlpha(100);
        chart1.setElevation(6);

        button = rootview.findViewById(R.id.renewButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData1();

            }
        });


        Ability user = new Ability();







        chart1.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = chart1.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0);
        xAxis.setXOffset(0);
        xAxis.setValueFormatter(new IAxisValueFormatter() {


            private String[] qualities = new String[] {"읽는 속도", "역행률", "완독 퍼센트", "책 응시률", "시선도 약폭"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return qualities[(int)value%qualities.length];
            }
        });

        xAxis.setTextColor(Color.BLACK);
        YAxis yAxis = chart1.getYAxis();
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(MIN);
        yAxis.setAxisMaximum(MAX);
        yAxis.setDrawLabels(false);

        Legend l = chart1.getLegend();
        l.setTextSize(15f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.BLACK);


        //===================================================

        chart2 = rootview.findViewById(R.id.main_chart2);
        chart2.setBackgroundColor(Color.rgb(60, 65, 82));
        chart2.getDescription().setEnabled(false);
        chart2.setWebLineWidth(1f);

        chart2.setWebColor(Color.BLACK);
        chart2.setWebLineWidth(1f);
        chart2.setWebColorInner(Color.BLACK);
        chart2.setWebAlpha(100);
        chart2.setElevation(6);
        chart2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_background));


        setData2();

        chart2.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis2 = chart2.getXAxis();
        xAxis2.setTextSize(9f);
        xAxis2.setYOffset(0);
        xAxis2.setXOffset(0);
        xAxis2.setValueFormatter(new IAxisValueFormatter() {


            private String[] qualities2 = new String[] {"읽는 속도", "역행률", "완독 퍼센트", "책 응시률", "시선도 약폭"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return qualities2[(int)value%qualities2.length];
            }
        });

        xAxis2.setTextColor(Color.BLACK);
        YAxis yAxis2 = chart2.getYAxis();
        yAxis2.setTextSize(9f);
        yAxis2.setAxisMinimum(MIN);
        yAxis2.setAxisMaximum(MAX);
        yAxis2.setDrawLabels(false);

        Legend l2 = chart2.getLegend();
        l2.setTextSize(15f);
        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l2.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l2.setDrawInside(false);
        l2.setXEntrySpace(7f);
        l2.setYEntrySpace(5f);
        l2.setTextColor(Color.BLACK);


        return rootview;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshValues:
                setData1();
                chart1.invalidate();
                setData2();
                chart2.invalidate();
                break;

            case R.id.toggleValues :
                for(IDataSet<?> set : chart1.getData().getDataSets()) {
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                chart1.invalidate();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void setData1() {



        reference = FirebaseDatabase.getInstance("https://pivoting-gachon-default-rtdb.firebaseio.com/").getReference();

        reference.child("person").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터 list를 추출해냄
                    String uid = snapshot.child("uid").getValue(String.class); //uid

                    if (uid.equals(myUid)) {
                        String tmp_count = snapshot.child("ability1").getValue(String.class);
                        float f = Float.parseFloat(tmp_count);
                        employee1.add(new RadarEntry(4));
                        employee1.add(new RadarEntry(3));
                        employee1.add(new RadarEntry(7));

                        break;
                    } else {
                        continue;
                    }

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        set1 = new RadarDataSet(employee1, "Aa");
        set1.setColor(Color.RED);
        set1.setFillColor(Color.RED);
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightIndicators(false);
        set1.setDrawHighlightCircleEnabled(true);

        set2 = new RadarDataSet(employee2, "Employee B");
        set2.setColor(Color.GREEN);
        set2.setFillColor(Color.GREEN);
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightIndicators(false);
        set2.setDrawHighlightCircleEnabled(true);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.BLACK);

        chart1.setData(data);
        chart1.invalidate();
    }

    private void setData2() {
        ArrayList<RadarEntry> employee1 = new ArrayList<>();
        ArrayList<RadarEntry> employee2 = new ArrayList<>();


        for(int i=0; i< NB_QUALITIES; i++) {
            float val1 = (int) (Math.random() * MAX ) + MIN ;
            employee1.add(new RadarEntry(val1));

            float val2 = (int) (Math.random() * MAX) + MIN;
            employee2.add(new RadarEntry(val2));
        }

        RadarDataSet set1 = new RadarDataSet(employee1, "Employee A");
        set1.setColor(Color.RED);
        set1.setFillColor(Color.RED);
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightIndicators(false);
        set1.setDrawHighlightCircleEnabled(true);

        RadarDataSet set2 = new RadarDataSet(employee2, "Employee B");
        set2.setColor(Color.GREEN);
        set2.setFillColor(Color.GREEN);
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightIndicators(false);
        set2.setDrawHighlightCircleEnabled(true);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.BLACK);

        chart2.setData(data);
        chart2.invalidate();
    }
}