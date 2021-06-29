package visual.camp.sample.app.activity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.EyeMovementState;
import camp.visual.gazetracker.util.ViewLayoutChecker;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import visual.camp.sample.app.GazeTrackerManager;
import visual.camp.sample.app.R;
import visual.camp.sample.view.GazePathView;

public class DemoActivity extends AppCompatActivity {

   private Button tmp;
   private Balloon balloon;
   private  ViewFlipper viewFlipper;

   private static final String TAG = DemoActivity.class.getSimpleName();
   private final ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
   private GazePathView gazePathView;
   private GazeTrackerManager gazeTrackerManager;
   private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(
      2, 30, 0.5F, 0.001F, 1.0F);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demo);
    gazeTrackerManager = GazeTrackerManager.getInstance();
    Log.i(TAG, "gazeTracker version: " + GazeTracker.getVersionName());
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(TAG, "onStart");
    gazeTrackerManager.setGazeTrackerCallbacks(gazeCallback);
    initView();
  }

  @Override
  protected void onResume() {
    super.onResume();
    gazeTrackerManager.startGazeTracking();
    setOffsetOfView();
    Log.i(TAG, "onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    gazeTrackerManager.stopGazeTracking();
    Log.i(TAG, "onPause");
  }

  @Override
  protected void onStop() {
    super.onStop();
    gazeTrackerManager.removeCallbacks(gazeCallback);
    Log.i(TAG, "onStop");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  private void initView() {
    gazePathView = findViewById(R.id.gazePathView);
    viewFlipper = findViewById(R.id.view_flipper);
    tmp =findViewById(R.id.tmp);

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


    tmp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        balloon.show(tmp);
        balloon.dismissWithDelay(1000L);

      }

    });


  }



    public void previousView(View v) {
    viewFlipper.showPrevious();
}
public void nextView(View v) {
    viewFlipper.showNext();
}


  private void setOffsetOfView() {
    viewLayoutChecker.setOverlayView(gazePathView, new ViewLayoutChecker.ViewLayoutListener() {
      @Override
      public void getOffset(int x, int y) {
        gazePathView.setOffset(x, y);
      }
    });
  }

  private final GazeCallback gazeCallback = new GazeCallback() {
    @Override
    public void onGaze(GazeInfo gazeInfo) {
      if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
        final float[] filtered = oneEuroFilterManager.getFilteredValues();
        gazePathView.onGaze(filtered[0], filtered[1], gazeInfo.eyeMovementState == EyeMovementState.FIXATION);


        Log.i(TAG, "gazeTracker version: " + gazeInfo.timestamp);

      }
    }
  };
}
