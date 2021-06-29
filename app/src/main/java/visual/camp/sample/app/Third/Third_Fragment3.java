package visual.camp.sample.app.Third;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.data.RadarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import visual.camp.sample.app.R;
import visual.camp.sample.app.myHabitChartActivity;
import visual.camp.sample.app.ourHabitChartActivity;

public class Third_Fragment3 extends Fragment {
    private String photoUrl;
    private String nickname;
    private ImageView googleProfile;
    private TextView googleEmail;

    private Button myHabit;
    private Button ourHabit;
    private DatabaseReference reference;
    private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //
    private static float tmp_ability1;
    private static float tmp_ability2;
    private static float tmp_ability3;

    private static float person_ability1;
    private static float person_ability2;
    private static float person_ability3;
    private static float person_count;

    private static float total_ability1;
    private static float total_ability2;
    private static float total_ability3;
    private static int total_count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_third_3, container, false);

        Bundle bundle =getArguments();
        photoUrl = bundle.getString("photoUrl");
        nickname = bundle.getString("nickname");

        googleEmail = rootview.findViewById(R.id.googleEmail);
        googleProfile = rootview.findViewById(R.id.googleProfile);

        Glide.with(this).load(photoUrl).into(googleProfile);
        googleEmail.setText(nickname);


        reference = FirebaseDatabase.getInstance("https://pivoting-gachon-default-rtdb.firebaseio.com/").getReference();
        reference.child("person").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터 list를 추출해냄
                    String uid = snapshot.child("uid").getValue(String.class); //uid

                    if (uid.equals(myUid)) {
                        String tmp1 = snapshot.child("ability1").getValue(String.class);
                        String tmp2 = snapshot.child("ability2").getValue(String.class);
                        String tmp3 = snapshot.child("ability3").getValue(String.class);
                        String tmp_count = snapshot.child("count").getValue(String.class);


                        person_ability1 = Float.parseFloat(tmp1);
                        person_ability2 = Float.parseFloat(tmp2);
                        person_ability3 = Float.parseFloat(tmp3);
                        person_count = Integer.parseInt(tmp_count);

                        // 내 과거 읽기의 평균
                        // 다른 사람 전체 읽기의 평균

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


        //myhabit에는 person_ability와 tmp_ability가 들어가야한다
        myHabit = rootview.findViewById(R.id.myHabit);
        myHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                reference.child("tmp").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터 list를 추출해냄
                            String uid = snapshot.child("uid").getValue(String.class); //uid

                            if (uid.equals(myUid)) {


                                String tmp1 = snapshot.child("ability1").getValue(String.class);
                                String tmp2 = snapshot.child("ability2").getValue(String.class);
                                String tmp3 = snapshot.child("ability3").getValue(String.class);

                                tmp_ability1 = Float.parseFloat(tmp1);
                                tmp_ability2 = Float.parseFloat(tmp2);
                                tmp_ability3 = Float.parseFloat(tmp3);

                                Intent intent = new Intent(getContext(), myHabitChartActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putFloat("tmp_ability1", tmp_ability1);
                                bundle.putFloat("tmp_ability2", tmp_ability2);
                                bundle.putFloat("tmp_ability3", tmp_ability3);

                                bundle.putFloat("person_ability1", person_ability1);
                                bundle.putFloat("person_ability2", person_ability2);
                                bundle.putFloat("person_ability3", person_ability3);
                                bundle.putInt("person_count", (int) person_count);

                                intent.putExtras(bundle);
                                startActivity(intent);


                                //하나짜리
                                break;
                            }
                            else {
                                continue;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        });


        ourHabit = rootview.findViewById(R.id.ourHabit);
            //ourhabit에는 person_ability와 total_ability가 들어가야한다
        ourHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance("https://pivoting-gachon-default-rtdb.firebaseio.com/").getReference();
                reference.child("total").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터 list를 추출해냄


                            String tmp_ability1 = snapshot.child("ability1").getValue(String.class);
                            String tmp_ability2 = snapshot.child("ability2").getValue(String.class);
                            String tmp_ability3 = snapshot.child("ability3").getValue(String.class);
                            String count = snapshot.child("count").getValue(String.class);

                            total_ability1 = Float.parseFloat(tmp_ability1);
                            total_ability2 = Float.parseFloat(tmp_ability2);
                            total_ability3 = Float.parseFloat(tmp_ability3);
                            total_count = Integer.parseInt(count);

                            Intent intent = new Intent(getContext(), ourHabitChartActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putFloat("total_ability1", total_ability1);
                            bundle.putFloat("total_ability2", total_ability2);
                            bundle.putFloat("total_ability3", total_ability3);
                            bundle.putInt("total_count", (int) total_count);

                            bundle.putFloat("person_ability1", person_ability1);
                            bundle.putFloat("person_ability2", person_ability2);
                            bundle.putFloat("person_ability3", person_ability3);
                            bundle.putInt("person_count", (int) person_count);

                            intent.putExtras(bundle);
                            startActivity(intent);


                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });








        return rootview;
    }
}