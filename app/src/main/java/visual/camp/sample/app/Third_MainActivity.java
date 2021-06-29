package visual.camp.sample.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import visual.camp.sample.app.Third.Third_Fragment1;
import visual.camp.sample.app.Third.Third_Fragment2;
import visual.camp.sample.app.Third.Third_Fragment3;

public class Third_MainActivity extends AppCompatActivity {

    private Third_Fragment2 third_fragment2 = new Third_Fragment2();
    private Third_Fragment3 third_fragment3 = new Third_Fragment3();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction;
    private BottomNavigationView bottomNavigationView;

    private String photoUrl;
    private String nickname;
    private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static int t=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third__main);

        Intent intent = getIntent();

        photoUrl = intent.getStringExtra("photoUrl");
        nickname = intent.getStringExtra("nickname");

        Bundle bundle = new Bundle();
        bundle.putString("photoUrl", photoUrl);
        bundle.putString("nickname", nickname);

        third_fragment3.setArguments(bundle);

        bottomNavigationView = findViewById(R.id.main_bottomNav);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainFrame, third_fragment2).commitAllowingStateLoss();

        if (t == 0 ) {
            Ability a3 = new Ability();
            a3.ability1 = String.valueOf((long) 0);
            a3.ability2 = String.valueOf((long) 0);
            a3.ability3 = String.valueOf((long) 0);
            a3.uid = myUid;
            FirebaseDatabase.getInstance().getReference().child("tmp")
                    .child(myUid).setValue(a3);


            Ability a2 = new Ability();
            a2.ability1 = String.valueOf((long) 0);
            a2.ability2 = String.valueOf((long) 0);
            a2.ability3 = String.valueOf((long) 0);
            a2.count = String.valueOf(0);
            a2.uid = myUid;

            FirebaseDatabase.getInstance().getReference().child("person")
                    .child(myUid).setValue(a2);

        } else {

        }

        // bottom navigation click action
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.fragment2: {
                        transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                                .replace(R.id.mainFrame, third_fragment2).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.fragment3: {
                        transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                                .replace(R.id.mainFrame, third_fragment3).commitAllowingStateLoss();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}