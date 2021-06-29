package visual.camp.sample.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

public class balloonActivity extends AppCompatActivity {
    Button button;
    private Balloon balloon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloon);

        button = findViewById(R.id.tmp);


        balloon = new Balloon.Builder(getApplicationContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setWidth(BalloonSizeSpec.WRAP)
                .setHeight(65)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("You can access your profile from now on.")
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                .setTextIsHtml(true)
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                balloon.show(button);
                balloon.dismissWithDelay(1000L);
            }
        });
    }
}