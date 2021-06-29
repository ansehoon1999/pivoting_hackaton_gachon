package visual.camp.sample.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import visual.camp.sample.app.activity.DemoActivity;
import visual.camp.sample.app.activity.MainActivity;

public class Third_Fragment2_2 extends AppCompatActivity {
    private TextView tvtitle,tvdescription,entering;
    private ImageView img;
    private long start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third__fragment2_2);

        tvtitle = (TextView) findViewById(R.id.txttitle);
        tvdescription = (TextView) findViewById(R.id.txtDesc);
        entering = (TextView) findViewById(R.id.entering);

        img = (ImageView) findViewById(R.id.bookthumbnail);

        // Recieve data
        Intent intent = getIntent();
        String Title = intent.getExtras().getString("Title");
        String Description = intent.getExtras().getString("Description");
        int image = intent.getExtras().getInt("Thumbnail") ;

        // Setting values
        entering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start = System.currentTimeMillis();
                Log.i("TAG", "before start: " + String.valueOf(start));
                Bundle bundle = new Bundle();
                bundle.putLong("start_time", start);

                Intent first_intent = new Intent(getBaseContext(), MainActivity.class);
                first_intent.putExtras(bundle);


                startActivity(first_intent);

            }
        });
        tvtitle.setText(Title);
        tvdescription.setText(Description);
        img.setImageResource(image);

    }
}