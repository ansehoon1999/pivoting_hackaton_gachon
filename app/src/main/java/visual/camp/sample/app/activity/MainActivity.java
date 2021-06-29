package visual.camp.sample.app.activity;
//규연이꺼
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.BoundingPoly;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.InitializationCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.constant.CalibrationModeType;
import camp.visual.gazetracker.constant.InitializationErrorType;
import camp.visual.gazetracker.constant.StatusErrorType;
import camp.visual.gazetracker.device.GazeDevice;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.EyeMovementState;
import camp.visual.gazetracker.state.ScreenState;
import camp.visual.gazetracker.state.TrackingState;
import camp.visual.gazetracker.util.ViewLayoutChecker;
import visual.camp.sample.app.AnalyzeActivity;
import visual.camp.sample.app.GazeTrackerManager;
import visual.camp.sample.app.R;
import visual.camp.sample.app.calibration.CalibrationDataStorage;
import visual.camp.sample.view.CalibrationViewer;
import visual.camp.sample.view.PointView;

public class MainActivity<GazePathView> extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA // 시선 추적 input
    };
    private static final int REQ_PERMISSION = 1000;
    private GazeTracker gazeTracker;
    private ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private HandlerThread backgroundThread = new HandlerThread("background");
    private Handler backgroundHandler;
    Word word[]= new Word[100000];
    int length_word;

    private GazePathView gazePathView;
    private GazeTrackerManager gazeTrackerManager;
    private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(
            2, 30, 0.5F, 0.001F, 1.0F);

    private static final String CLOUD_VISION_API_KEY = "AIzaSyD1u3UQ60Y1mUJPXK9WxloHWG-sZCPzl3E";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private TextView mImageDetails;
    private ImageView mMainImage;
    public TextView mImageVertex;
    public float deviceHeight;
    public float deviceWidth;

    private int timer = 0;
    private boolean gazing = false;
    private int thisWord = -1;

    private boolean running = false;

    private BitmapDrawable drawable;
    private Bitmap bitmap;

    //수정
    public int min_x=0;
    public int max_x=0;
    public int min_y=0;
    public int max_y=0;
    public BoundingPoly boundingPoly;

    //distnace 수정
    private boolean isRun = false;
    private double distance = 0;
    //역행률 (페이지에서 응시한 거리 / 페이지에 있는 줄 수 = 한 줄을 읽으면서 지나간 거리의 평균)
    private double goback = 0;
    //db에 저장해놓고 받아와야함
    public int[] page_line = {11,12,12,12,4};

    private int d_timer = 0;
    private float pre_x = -1;
    private float pre_y = -1;
    private Intent intent;

    private int page = 0;
    public int[] image = {R.drawable.page1,R.drawable.page2,R.drawable.page3,R.drawable.page4, R.drawable.page5};

    private int flip_timer = 0;

    public ViewFlipper view;

    private static long start;
    private long end;
    private long total; // total 독서 시간 측정
    private long u_start=0;
    private long u_end;
    private long u_total=0; // unknown 독서 시간
    private String eye_state; // 시선 상태
    private long concentration; // 집중도

    private long a_start;
    private long a_end;
    private long a_total=0;



    int text = 100; // 책 총 글자 수(수정 필요)
    private static double read_speed;

    private DatabaseReference reference;
    private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private static float real_tmp_ability1;
    private static float real_tmp_ability2;
    private static float real_tmp_ability3;

    private static float person_ability1;
    private static float person_ability2;
    private static float person_ability3;
    private static float person_count;


    private static float total_ability1;
    private static float total_ability2;
    private static float total_ability3;
    private static int total_count;

    private Balloon balloon;
    public TextureView textureView;

    private boolean balloon_true = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //수정
        textureView=findViewById(R.id.preview);

        mMainImage = findViewById(R.id.main_image);
        view= (ViewFlipper) findViewById(R.id.viewFlipper);

        Intent intent = getIntent();
        start = intent.getLongExtra("start_time", 0);

        Log.i(TAG, "first starttime" + String.valueOf(start));

        DisplayMetrics displayMetrics;
        displayMetrics = new DisplayMetrics();
        WindowManager windowManager=(WindowManager) (getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

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
                    } else if (uid == null) {

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

        reference.child("tmp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터 list를 추출해냄
                    String uid = snapshot.child("uid").getValue(String.class); //uid

                    if (uid.equals(myUid)) {
                        String tmp1 = snapshot.child("ability1").getValue(String.class);
                        String tmp2 = snapshot.child("ability2").getValue(String.class);
                        String tmp3 = snapshot.child("ability3").getValue(String.class);

                        real_tmp_ability1 = Float.parseFloat(tmp1);
                        real_tmp_ability2 = Float.parseFloat(tmp2);
                        real_tmp_ability3 = Float.parseFloat(tmp3);

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

                    break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        deviceHeight = displayMetrics.heightPixels;
        deviceWidth=displayMetrics.widthPixels;
        Log.i("devWidth", String.valueOf(displayMetrics.widthPixels));
        Log.i("devHeight", String.valueOf(displayMetrics.heightPixels));

        drawable = (BitmapDrawable) mMainImage.getDrawable();
        bitmap = drawable.getBitmap();

        callCloudVision(bitmap);
        mMainImage.setImageBitmap(bitmap);

        mMainImage.post(new Runnable(){
            @Override
            public void run() {
                Log.i("DEBUG","this"+getResources().getDisplayMetrics().density);
                Log.i("DEBUG", "h2: " + mMainImage.getHeight());
                Log.i("DEBUG", "w2: " + mMainImage.getWidth());

            }
        });

        Log.i(TAG, "gazeTracker version: " + GazeTracker.getVersionName());
        initView();
        checkPermission(); //카메라 권한 체크
        initHandler();

        //수정
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                float curX = motionEvent.getX();

                if (action == MotionEvent.ACTION_UP && curX > (deviceWidth / 2)) {
                    page++;
                    if(page == image.length){
                        page = 0;
                    }
                    mMainImage.setImageResource(image[page]);

                } else if (action == MotionEvent.ACTION_UP && curX < (deviceWidth / 2)) {
                    page--;
                    if(page == -1){
                        page = 0;
                    }
                    mMainImage.setImageResource(image[page]);

                }
                return true;
            }
        });
    }
    //안드로이드 life cycle

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        // 화면 전환후에도 체크하기 위해
        setOffsetOfView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseHandler();
        viewLayoutChecker.releaseChecker();
        releaseGaze();
    }

    // handler

    private void initHandler() {
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void releaseHandler() {
        backgroundThread.quitSafely();
    }

    // handler end
    // 카메라 권한
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check permission status
            if (!hasPermissions(PERMISSIONS)) {

                requestPermissions(PERMISSIONS, REQ_PERMISSION);
            } else {
                checkPermission(true);
            }
        } else {
            checkPermission(true);
        }
    }

    //수정
    private void papago(String string, final int x, final int y){
        new Papago(string, "en", "ko"){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.i(TAG,"Papago: "+result);
                balloon(result,x,y);
                view12(x,y);
                return;
            }
        }.excute(null);
        running = false;
    }

    public boolean view12(int x, int y){
        //int[] location=new int[2];
        int[] location1=new int[2];
        mMainImage.getLocationOnScreen(location1);
        //view.getLocationOnScreen(location);

        final int realRight = location1[0] + mMainImage.getWidth();
        final int realBottom = location1[1] + mMainImage.getHeight();


        Log.i(TAG,"view coord:  "+location1[0]);
        Log.i(TAG,"view coord:  "+location1[1]);
        Log.i(TAG,"view coord:  "+mMainImage.getWidth());
        Log.i(TAG,"view coord:  "+mMainImage.getHeight());

        return false;
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private boolean hasPermissions(String[] permissions) {
        int result;
        // Check permission status in string array
        for (String perms : permissions) {
            if (perms.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!Settings.canDrawOverlays(this)) {
                    return false;
                }
            }
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                // When if unauthorized permission found
                return false;
            }
        }
        // When if all permission allowed
        return true;
    }

    private void checkPermission(boolean isGranted) {
        if (isGranted) {
            permissionGranted();
        } else {
            showToast("not granted permissions", true);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraPermissionAccepted) {
                        checkPermission(true);
                    } else {
                        checkPermission(false);
                    }
                }
                break;
        }
    }

    private void permissionGranted() {
        initGaze();
    }
    // permission end

    // view
    // private TextureView preview;
    private View layoutProgress;
    private View viewWarningTracking;
    private PointView viewPoint;
    private Button btnInitGaze, btnStopGaze;
    private Button btnStartTracking, btnStopTracking;
    private Button btnStartCalibration, btnStopCalibration, btnSetCalibration;
    private CalibrationViewer viewCalibration;
    private Button quit;


    // gaze coord filter
    private SwitchCompat swUseGazeFilter;
    private boolean isUseGazeFilter = true;
    // calibration type
    private RadioGroup rgCalibration;
    private CalibrationModeType calibrationType = CalibrationModeType.FIVE_POINT;

    private AppCompatTextView txtGazeVersion;

    //초기 화면
    private void initView() {

        layoutProgress = findViewById(R.id.layout_progress);
        layoutProgress.setOnClickListener(null);

        viewWarningTracking = findViewById(R.id.view_warning_tracking);

        btnInitGaze = findViewById(R.id.start_gaze);
        btnStopGaze = findViewById(R.id.stop_gaze);
        btnInitGaze.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                running = false;
                initGaze();
            }
        });
        btnStopGaze.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                releaseGaze();
            }
        });
        btnStartCalibration = findViewById(R.id.start_calibration);
        btnStartCalibration.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startCalibration();
            }
        });

        viewPoint = findViewById(R.id.view_point);
        viewCalibration = findViewById(R.id.view_calibration);


        quit=findViewById(R.id.Quit);
        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                releaseGaze();

                finish();

                Intent intent=new Intent(getApplicationContext(), AnalyzeActivity.class);


                Bundle bundle = new Bundle();
                            bundle.putFloat("total_ability1", total_ability1);
                            bundle.putFloat("total_ability2", total_ability2);
                            bundle.putFloat("total_ability3", total_ability3);
                            bundle.putInt("total_count", (int) total_count);

                            bundle.putFloat("person_ability1", person_ability1);
                            bundle.putFloat("person_ability2", person_ability2);
                            bundle.putFloat("person_ability3", person_ability3);
                            bundle.putInt("person_count", (int) person_count);

                            bundle.putFloat("tmp_ability1", real_tmp_ability1);
                            bundle.putFloat("tmp_ability2", real_tmp_ability2);
                            bundle.putFloat("tmp_ability3", real_tmp_ability3);

                            bundle.putLong("concentration", concentration);
                            bundle.putDouble("read speed", read_speed);
                            bundle.putDouble("reverse rate", goback);
                            intent.putExtras(bundle);

                            startActivity(intent);

                        }

        });


        //swUseGazeFilter = findViewById(R.id.sw_use_gaze_filter);
        //rgCalibration = findViewById(R.id.rg_calibration);
        calibrationType = CalibrationModeType.ONE_POINT;

        setOffsetOfView();
    }

    // The gaze or calibration coordinates are delivered only to the absolute coordinates of the entire screen.
    // The coordinate system of the Android view is a relative coordinate system,
    // so the offset of the view to show the coordinates must be obtained and corrected to properly show the information on the screen.
    private void setOffsetOfView() {
        viewLayoutChecker.setOverlayView(viewPoint, new ViewLayoutChecker.ViewLayoutListener() {
            @Override
            public void getOffset(int x, int y) {
                viewPoint.setOffset(x, y);
                viewCalibration.setOffset(x, y);
            }
        });
    }

    private void showProgress() {
        if (layoutProgress != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutProgress.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void hideProgress() {
        if (layoutProgress != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutProgress.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void showTrackingWarning() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewWarningTracking.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideTrackingWarning() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewWarningTracking.setVisibility(View.INVISIBLE);
            }
        });
    }



    private void showToast(final String msg, final boolean isShort) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
            }
        });
    }

    //수정
    //Gaze Thread
    //gaze 하는 곳을 알려줌.
    //Inside_of_Screen: 시선 추적이 성공적이고 시선 지점이 기기 화면 안에 있음.
    //OUT_OF_SCREEN: 시선 추적이 성공적이고 시선 지점이 기기 화면 밖에 있음.
    private void showGazePoint(final float x, final float y, final ScreenState type,EyeMovementState eye, float timestamp, final GazeInfo gazeInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPoint.setType(type == ScreenState.INSIDE_OF_SCREEN ? PointView.TYPE_DEFAULT : PointView.TYPE_OUT_OF_SCREEN);
                viewPoint.setPosition(x, y);
                //distance 수정
                d_timer++;
                if(d_timer % 50 == 0){
                    if(pre_x == -1){
                        pre_x = x;
                        pre_y = y;
                    }else{
                        double diff_x = pre_x - x;
                        double diff_y = pre_y - y;
                        double dis = Math.pow(diff_x,2) + Math.pow(diff_y,2);
                        distance = distance + Math.pow(dis,0.5);
                    }
                    Log.i("Gaze_coordinate","x : " + x + "  y : " + y + "  distance : "+distance);
                }
            }
        });
    }

    private void BookFlip(final float x, final float y, final ScreenState type, float timestamp, final GazeInfo gazeInfo){
        runOnUiThread(new Runnable() {
            public void run() {
                flip_timer++;

                float xx = (float) ((float) x*0.9 + 70);
                float yy = (float) ((float) y*0.9 + 50);

                if (gazeInfo.eyeMovementState == EyeMovementState.FIXATION) { // fixxation 상태일때
                    if (xx > min_x*1.2 && xx < max_x *0.85 && yy >= min_y && yy <= max_y) {
                        flip_timer = 0;
                        Log.d("GachNext", String.valueOf(flip_timer));
                    }
                    else if ( xx  > (deviceWidth / 2)*0.9) {
                        if (flip_timer > 50) {
                            //view.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_in));
                            //view.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_out));
                            //view.showNext();

                            Log.i("GachNext"," Next Page!! "+"  x : " + xx + "  y : " + yy + "   min_x = " + min_x + "   max_x = " + max_x + "   max_y = " + max_y + "   min_y = " + min_y);

                            page++;
                            if(page == image.length){
                                page = 0;
                            }
                            mMainImage.setImageResource(image[page]);

                            drawable = (BitmapDrawable) mMainImage.getDrawable();
                            bitmap = drawable.getBitmap();
                            callCloudVision(bitmap);
                            onResume();

                            flip_timer = 0;
                            //역행률 (페이지에서 응시한 거리 / 페이지에 있는 줄 수 = 한 줄을 읽으면서 지나간 거리의 평균)
                            if(goback == 0){
                                goback = distance/20;
                                distance = 0;
                                Log.i("Gaze_coordinate","goback : " + goback);
                            }else{
                                double page_d = distance / page_line[page];
                                goback = (goback + page_d)/20;
                                Log.i("Gaze_coordinate","goback : " + goback);
                            }

                        }
                    } else if (xx > 0 && yy > 0 && x < (deviceWidth / 2)*0.9) {
                        if (flip_timer > 50) {
                            //view.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
                            //view.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
                            //view.showPrevious();
                            Log.i("GachNext"," Preview Page!! "+"  x : " + xx + "  y : " + yy + "   min_x = " + min_x + "   max_x = " + max_x + "   min_y = " + max_y + "   min_y = " + min_y);

                            //수정
                            page--;
                            if(page == -1){
                                page = 0;
                            }
                            mMainImage.setImageResource(image[page]);

                            drawable = (BitmapDrawable) mMainImage.getDrawable();
                            bitmap = drawable.getBitmap();
                            callCloudVision(bitmap);
                            onResume();


                            flip_timer = 0;
                            //역행률 (페이지에서 응시한 거리 / 페이지에 있는 줄 수 = 한 줄을 읽으면서 지나간 거리의 평균)
                            if(goback == 0){
                                goback = distance;
                                distance = 0;
                                Log.i("Gaze_coordinate","goback : " + goback);
                            }else{
                                double page_d = distance / page_line[page];
                                goback = (goback + page_d)/2;
                                Log.i("Gaze_coordinate","goback : " + goback);
                            }
                        }
                    }
                    else{
                        Log.i("GachNext"," Nothing! "+"  x : " + xx + "  y : " + yy + "   min_x = " + min_x + "   max_x = " + max_x + "   min_y = " + max_y + "   min_y = " + min_y);

                    }
                }
            }


        });
    }

    //OCR Thread
    private void OCR(final float x, final float y, final ScreenState type, float timestamp, final GazeInfo gazeInfo){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float xx = (float) ((float) x*0.9 + 70);
                float yy = (float) ((float) y*0.87 + 50);
                timer++;
//                gazing = false; // 현재 한 단어를 응시중인지
//                thisWord = -1; // 현재 응시중인 단어의 index
                if(timer%7 == 0){
                    //만약 응시중이 아니라면
                    if(thisWord == -1){
                        running = true;
                        //지금 찍힌 좌표가 어디 단어에 속해있는지 확인
                        for (int i = 0; i < length_word; i++) {
                            Log.i("Gach false","Gazing ? "+ gazing + "  thisWorld = " + thisWord+"  x : " + xx + "  y : " + yy + "   min_x = " + word[i].min_x + "   max_x = " + word[i].max_x + "   min_y = " + word[i].max_y + "   min_y = " + word[i].min_y + "   word[i] = " + word[i].name);

                            //그 좌표를 찾았다면

                            if ((xx >= word[i].min_x *0.9 ) && (xx <= word[i].max_x*0.9) && ( yy >= word[i].min_y) && (yy <= word[i].max_y)) {
                                //응시중이라고 표시(true)하고 timer를 초기화 시킨다. thisWord는 지금 응시중인 단어의 index를 저장 이러고 for문을 나감
                                gazing = true;
                                thisWord = i;
                                timer = 0;
                                Log.i("FoundGazing", "x : " + xx + "  y : " + yy + "   min_x = " + word[i].min_x + "   max_x = " + word[i].max_x + "   min_y = " + word[i].max_y + "   min_y = " + word[i].min_y + "   word[i] = " + word[i].name);
                                Log.i("Gach","timer : "+timer+"  word : "+word[thisWord].getName());
                                Log.i("FoundGaze","Gazing ? "+ gazing + "  thisWorld = " + thisWord+ "  word : "+word[thisWord].name);
                                running = false;
                                break;
                            }
                        }
                        running = false;
                    }
                    else{
                        //계속 같은 단어를 응시중이라면
                        Log.i("GachTrue","Gazing ? "+ gazing + "  thisWorld = " + thisWord+"  x : " + x + "  y : " + y + "   min_x = " + word[thisWord].min_x + "   max_x = " + word[thisWord].max_x + "   min_y = " + word[thisWord].max_y + "   min_y = " + word[thisWord].min_y + "   word[thisWord] = " + word[thisWord].name);
                        Log.i("GachTrue","timer : "+timer+"  word : "+word[thisWord].getName());
                        //이거 빼냄
                        //timer > 원하는 응시 시간(시간만큼 응시했다면 멈추고 로그 출력)
                        if(timer >= 21){
                            int x_coord= (int) x;
                            int y_coord= (int) y;
                            balloon_true = true;
                            papago(word[thisWord].name,x_coord,y_coord);
                            stopTracking();
                            Log.i("this is log",word[thisWord].name);
                            Log.i("this is log", String.valueOf(word[thisWord].min_x));
                            Log.i("this is log", String.valueOf(word[thisWord].max_x));
                            Log.i("this is log", String.valueOf(x));
                            System.out.print(word[thisWord].name);
                            running = false;
                        }

                        if(timer % 7 == 0){
                            if ((xx >= word[thisWord].min_x *0.9 ) && (xx <= word[thisWord].max_x *0.9) && ( yy >= word[thisWord].min_y) && (yy <= word[thisWord].max_y)) {
                                Log.i("Gach true","Gazing ? "+ gazing + "  thisWorld = " + thisWord+"  xx : " + xx + "  yy : " + yy + "   min_x = " + word[thisWord].min_x + "   max_x = " + word[thisWord].max_x + "   max_y = " + word[thisWord].max_y + "   min_y = " + word[thisWord].min_y + "   word[thisWord] = " + word[thisWord].name);
                                //원래여기

                            }
                            //그 단어의 범위를 넘어갔다면
                            else{
                                //타이머, thisWord 초기화, gazing false로 바꿈
                                timer = 0;
                                thisWord = -1;
                                gazing = false;
                            }
                        }
                        running = false;
                    }
                }
//                if("FIXATION".equals(gazeInfo.eyeMovementState)) { // fixation 상태일때
//                    for (int i=0; i<length_word; i++) {
//                        if (x > word[i].min_x && x <= word[i].max_x && y >= word[i].min_y && y <= word[i].max_y && timestamp >= 2000) {
//                            stopTracking();
//                            Log.d("this is log",word[i].name);
//                        }
//                    }
//                }
            }
        });
    }
    private void setCalibrationPoint(final float x, final float y) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCalibration.setVisibility(View.VISIBLE);
                viewCalibration.changeDraw(true, null);
                viewCalibration.setPointPosition(x, y);
                viewCalibration.setPointAnimationPower(0);
            }
        });
    }

    private void setCalibrationProgress(final float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCalibration.setPointAnimationPower(progress);
            }
        });
    }

    private void hideCalibrationView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCalibration.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setViewAtGazeTrackerState() {
//        Log.i(TAG, "gaze : " + isGazeNonNull() + ", tracking " + isTracking());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnInitGaze.setEnabled(!isGazeNonNull());
                btnStopGaze.setEnabled(isGazeNonNull());

                btnStartCalibration.setEnabled(isGazeNonNull() && isTracking());

                if (!isTracking()) {
                    hideCalibrationView();
                }
            }
        });
    }

    // view end



    // gazeTracker. gaze tracking하면 true.
    private boolean isTracking() {
        if (isGazeNonNull()) {
            return gazeTracker.isTracking();
        }
        return false;
    }

    private boolean isGazeNonNull() {
        return gazeTracker != null;
    }

    private InitializationCallback initializationCallback = new InitializationCallback() {
        @Override
        public void onInitialized(GazeTracker gazeTracker, InitializationErrorType error) {
            if (gazeTracker != null) { //만약 gaze tracker이 성공한다면
                initSuccess(gazeTracker);
            } else {
                initFail(error);
            }
        }
    };

    private void initSuccess(GazeTracker gazeTracker) {
        this.gazeTracker = gazeTracker;
        this.gazeTracker.setCallbacks(gazeCallback, calibrationCallback, statusCallback);
        startTracking();
        hideProgress();
    }


    private void initFail(InitializationErrorType error) {
        String err = "";
        if (error == InitializationErrorType.ERROR_CAMERA_PERMISSION) {
            // When if camera permission doesn not exists
            err = "required permission not granted";
        } else {
            // Gaze library initialization failure
            // It can ba caused by several reasons(i.e. Out of memory).
            err = error.toString();
        }
        showToast(err, false);
        Log.w(TAG, "error description: " + err);
        hideProgress();
    }

    //private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(2);
    private GazeCallback gazeCallback = new GazeCallback() {
        @Override
        public void onGaze(GazeInfo gazeInfo) {

            //timeStamp: 시선 점의 타임스탬프 단위는 밀리초. 시간 형식은 UTC입니다.
            //x,y: x, y 시선 점의 좌표 값. 원점은 장치 화면입니다. 단위는 픽셀(px)입니다.
            //trackingState: SUCCESS, LOW_CONFIDENCE, UNSUPPORTED, FACE_MISSING
            //eyeMovementState: FIXATION (고정), SACCADE, UNKNOWN
            if (isGazeNonNull()) {
                TrackingState state = gazeInfo.trackingState;
                if (state == TrackingState.SUCCESS) {
                    hideTrackingWarning();
                    if (!gazeTracker.isCalibrating()) {
                        if (isUseGazeFilter) {
                            if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
                                float[] filteredPoint = oneEuroFilterManager.getFilteredValues();
                                //수정
                                if(!running){
                                    showGazePoint(filteredPoint[0], filteredPoint[1], gazeInfo.screenState,gazeInfo.eyeMovementState,gazeInfo.timestamp,gazeInfo);
                                    BookFlip(filteredPoint[0], filteredPoint[1], gazeInfo.screenState, gazeInfo.timestamp, gazeInfo);
                                    OCR(filteredPoint[0], filteredPoint[1], gazeInfo.screenState,gazeInfo.timestamp,gazeInfo);
                                    eye_state = String.valueOf(gazeInfo.eyeMovementState); // 시선 상태
                                    Log.i(TAG, "check eyeMovement " + eye_state + " (x,y) " + gazeInfo.x + " " + gazeInfo.y);
                                    UknownTime(eye_state);
                                }
                            }
                        } else {
                            showGazePoint(gazeInfo.x, gazeInfo.y, gazeInfo.screenState,gazeInfo.eyeMovementState,gazeInfo.timestamp,gazeInfo);
                            BookFlip(gazeInfo.x, gazeInfo.y, gazeInfo.screenState,gazeInfo.timestamp,gazeInfo);
                            OCR(gazeInfo.x, gazeInfo.y, gazeInfo.screenState,gazeInfo.timestamp,gazeInfo);
                            eye_state = String.valueOf(gazeInfo.eyeMovementState); // 시선 상태
                            Log.i(TAG, "check eyeMovement " + eye_state + " (x,y) " + gazeInfo.x + " " + gazeInfo.y);
                            UknownTime(eye_state);

                        }
                    }
                } else {
                    showTrackingWarning();
                }

//                eye_state = String.valueOf(gazeInfo.eyeMovementState); // 시선 상태
//                Log.i(TAG, "check eyeMovement " + eye_state + " (x,y) " + gazeInfo.x + " " + gazeInfo.y);
////                if(eye_state.equals("UNKNOWN")){ // 딴짓중
////                    if(u_start == 0) { // 이전에 기록이 없어 (그 전에는 집중 중)
////                        u_start = System.currentTimeMillis();
////                        UknownTime(eye_state);
////                    }
////                }
//
//
//
//                UknownTime(eye_state); // 집중력 측정
////                Log.i(TAG, "check eyeMovement " + gazeInfo.eyeMovementState);

            }
        }
    };

    //private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(2);
    private GazeCallback coordinatecallback = new GazeCallback() {
        @Override
        public void onGaze(GazeInfo gazeInfo) {
            //timeStamp: 시선 점의 타임스탬프 단위는 밀리초. 시간 형식은 UTC입니다.
            //x,y: x, y 시선 점의 좌표 값. 원점은 장치 화면입니다. 단위는 픽셀(px)입니다.
            //trackingState: SUCCESS, LOW_CONFIDENCE, UNSUPPORTED, FACE_MISSING
            //eyeMovementState: FIXATION (고정), SACCADE, UNKNOWN

            if (isGazeNonNull()) {
                TrackingState state = gazeInfo.trackingState;
                if (state == TrackingState.SUCCESS) {
                    hideTrackingWarning();
                    if (!gazeTracker.isCalibrating()) {
                        if (isUseGazeFilter) {
                            if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
                                float[] filteredPoint = oneEuroFilterManager.getFilteredValues();
                                for (int i = 0; i < length_word; i++) {
                                    if (gazeInfo.eyeMovementState == EyeMovementState.FIXATION) {
                                        if(filteredPoint[0]>50 && filteredPoint[0]<=500 && filteredPoint[1]>=50 &&filteredPoint[1]<=500 && gazeInfo.timestamp>=2000 ){
                                            stopTracking();
                                            Log.d("this is log",word[i].name);
                                        }

                                    }
                                }

                            }
                        } else {
                            showGazePoint(gazeInfo.x, gazeInfo.y, gazeInfo.screenState,gazeInfo.eyeMovementState,gazeInfo.timestamp,gazeInfo);
                        }
                    }
                } else {
                    showTrackingWarning();
                }
//
            }
        }
    };

    private CalibrationCallback calibrationCallback = new CalibrationCallback() {
        @Override
        //startCollectSample 기능이 실행되면 시작 값이 증가
        public void onCalibrationProgress(float progress) {
            setCalibrationProgress(progress);
        } //값은 0~1 사이


        @Override
        public void onCalibrationNextPoint(final float x, final float y) {
            setCalibrationPoint(x, y);
            // Give time to eyes find calibration coordinates, then collect data samples
            //calibration 좌표를 찾을 시간을 주고 데이터 샘플 collect
            // After drawing the calibration point,
            // call startCollectSamples after enough time (1,000 ms in this example) that considering user can recognize the point
            backgroundHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startCollectSamples(); //이 기능은 calibration이 진행 중일 때 볼 수 있는 점의 좌표를 보여줌.
                }
            }, 1000);
        }

        //모든 calibration 단계가 완료되면 이를 알리는 콜백 기능.
        //파라미터로 전달된 보정 데이터는 GazeTracker에 이미 적용됨.
        //또한 앱을 다시 시작할 때 setCalibrationData(CalibrationData)를 호출하여 새 보정 없이 이 보정 데이터를 저장하고 GazeTracker에 직접 로드.
        @Override
        public void onCalibrationFinished(double[] calibrationData) {
            // 캘리브레이션이 끝나면 자동으로 gazepoint에 적용되어있고
            // calibrationDataStorage에 calibrationData를 넣는것은 다음번에 캘리브레이션 하지않고 사용하게 하기 위함이다.
            CalibrationDataStorage.saveCalibrationData(getApplicationContext(), calibrationData);
            hideCalibrationView();
            showToast("calibrationFinished", true);
        }
    };

    private StatusCallback statusCallback = new StatusCallback() {
        @Override
        public void onStarted() {
            // isTracking true
            // When if camera stream starting
            setViewAtGazeTrackerState();
        }

        @Override
        public void onStopped(StatusErrorType error) {
            // isTracking false
            // When if camera stream stopping
            setViewAtGazeTrackerState();
            if (error != StatusErrorType.ERROR_NONE) {
                switch (error) {
                    case ERROR_CAMERA_START:
                        // When if camera stream can't start
                        showToast("ERROR_CAMERA_START ", false);
                        break;
                    case ERROR_CAMERA_INTERRUPT:
                        // When if camera stream interrupted
                        showToast("ERROR_CAMERA_INTERRUPT ", false);
                        break;
                }
            }
        }
    };

    private void initGaze() { //GazeTracker는 장치의 전면 카메라에서 비디오에서 시선 추적 데이터를 생성하는 클래스
        showProgress();
        //GazeDevice:
        //이 클래스는 GazeTracker에서 설정할 카메라의 화면 원점 정보를 관리.
        //
        //기본적으로 여러 장치의 화면 원점에 대한 정보를 가짐.
        GazeDevice gazeDevice = new GazeDevice();
        if (gazeDevice.isCurrentDeviceFound()) {
            // 돌린 기기의 device info가 있는지확인
            Log.d(TAG, "이 디바이스는 gazeinfo 설정이 필요 없습니다.");
        } else {
            // 예시입니다. SM-T720은 갤럭시탭 s5e 모델명
            gazeDevice.addDeviceInfo("SM-T720", -72f, -4f);
        }

        String licenseKey = "dev_rzurv5ch6czzvcejx1h69xhta1b4e5f7vhngyyzn";
        GazeTracker.initGazeTracker(getApplicationContext(), gazeDevice, licenseKey, initializationCallback);
    }

    private void releaseGaze() {
        if (isGazeNonNull()) {
            end = System.currentTimeMillis();
            total = (long) ((end-start)/1000.0);

            Log.i(TAG, "Time " + total);
            Log.i(TAG, "Unknown time: " + u_total);

            double div = u_total/total;
            double one = 1 - div;
            double hundred = one * 100;

            concentration = (100*u_total)/total;
            Log.i(TAG, "Concentration result: " + concentration +"%");

            Read_Speed();

            GazeTracker.deinitGazeTracker(gazeTracker);
            gazeTracker = null;
        }
        setViewAtGazeTrackerState();
    }


    private void startTracking() { //gaze tracking 시작하기 전에 불려야함.
        if (isGazeNonNull()) {
            gazeTracker.startTracking();
            running = false;
        }
    }

    private void stopTracking() { // gazeTracker.startTracking() Fail
        if (isGazeNonNull()) {
            gazeTracker.stopTracking();
            running = false;
        }
    }

    private boolean startCalibration() {
        boolean isSuccess = false;
        if (isGazeNonNull()) {
            isSuccess = gazeTracker.startCalibration(calibrationType); //몇 포인트의 정확도를 가질것인가. 디폴트는 5.
            if (!isSuccess) {
                showToast("calibration start fail", false);
            }
        }
        setViewAtGazeTrackerState();
        return isSuccess;
    }

    // Collect the data samples used for calibration
    private boolean startCollectSamples() {
        boolean isSuccess = false;
        if (isGazeNonNull()) {
            isSuccess = gazeTracker.startCollectSamples();
        }
        setViewAtGazeTrackerState();
        return isSuccess;
    }

    private void stopCalibration() {
        if (isGazeNonNull()) {
            gazeTracker.stopCalibration();
        }
        hideCalibrationView();
        setViewAtGazeTrackerState();
    }

    private void setCalibration() {
        if (isGazeNonNull()) {
            double[] calibrationData = CalibrationDataStorage.loadCalibrationData(getApplicationContext());
            if (calibrationData != null) {
                // When if stored calibration data in SharedPreference
                if (!gazeTracker.setCalibrationData(calibrationData)) {
                    showToast("calibrating", false);
                } else {
                    showToast("setCalibrationData success", false);
                }
            } else {
                // When if not stored calibration data in SharedPreference
                showToast("Calibration data is null", true);
            }
        }
        setViewAtGazeTrackerState();
    }

    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();


        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("TEXT_DETECTION");
                labelDetection.setMaxResults(10);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);


        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                convertAnnotationToString(response);
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                //TextView imageDetail = activity.findViewById(R.id.image_details);
                //imageDetail.setText(result);

            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = (int) deviceWidth;
        int resizedHeight = (int) deviceHeight;

        if (originalHeight > originalWidth) {
            resizedHeight = (int) deviceWidth;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = (int) deviceWidth;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = (int) deviceWidth;
            resizedWidth = (int) deviceWidth;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message = labels.get(0).getDescription();

        } else {
            message = "nothing";
        }
        return message;
    }

    // Detects text in the specified image.
    public void convertAnnotationToString(BatchAnnotateImagesResponse response) {
        String message;
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        System.out.format("Text13232: %s%n", response.getResponses().get(0).getTextAnnotations().get(1).getBoundingPoly());
        boundingPoly = response.getResponses().get(0).getTextAnnotations().get(0).getBoundingPoly();
        min_x=boundingPoly.getVertices().get(0).getX();
        max_x=boundingPoly.getVertices().get(1).getX();
        min_y=boundingPoly.getVertices().get(1).getY();
        max_y=boundingPoly.getVertices().get(2).getY();

        List<AnnotateImageResponse> responses = response.getResponses();

        for (AnnotateImageResponse res : responses) {
            for (EntityAnnotation annotation : labels) {
                //System.out.printf("Text: %s\n", annotation.getDescription()); // 인식한 텍스트를 인식 단위 (단어?) 별로 출력
                //System.out.printf("Position : %s\n", annotation.getBoundingPoly()); // 해당 인식 단위에 대한 좌표 출력
            }
        }

        //Word split and Store by Word class
        String[] words=labels.get(0).getDescription().split("\\s+");
        if (labels != null) {
            message = labels.get(0).getDescription();
            words= message.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].replaceAll("[^\\w]", "");
            }

            System.out.format("Text: %s%n", labels.get(0).getDescription());
            //System.out.format("Vertex: %s%n", labels.get(0).getBoundingPoly().getVertices());

        }
        length_word=words.length;

        for (int i = 0; i <= words.length; i++) {
            if(i != 0){
                Log.i("checkWord","Name : "+ labels.get(i).getDescription() +"  max_x : "+ labels.get(i).getBoundingPoly().getVertices().get(1).getX() +
                        "  Min_x : " + labels.get(i).getBoundingPoly().getVertices().get(0).getX() +"  Min_y : "+ labels.get(i).getBoundingPoly().getVertices().get(1).getY()
                        +"  Max_y : "+ labels.get(i).getBoundingPoly().getVertices().get(2).getY());
//                    word[i].setName(annotation.getDescription());
//                    word[i].setMax_x(annotation.getBoundingPoly().getVertices().get(1).getX());
//                    word[i].setMin_x(annotation.getBoundingPoly().getVertices().get(0).getX());
//                    word[i].setMin_y(annotation.getBoundingPoly().getVertices().get(1).getY());
//                    word[i].setMax_y(annotation.getBoundingPoly().getVertices().get(2).getY());
                Word word_temp = new Word(labels.get(i).getDescription(), labels.get(i).getBoundingPoly().getVertices().get(1).getX(), labels.get(i).getBoundingPoly().getVertices().get(0).getX(),labels.get(i).getBoundingPoly().getVertices().get(1).getY(),labels.get(i).getBoundingPoly().getVertices().get(2).getY());
                word[i-1] = word_temp;
            }
        }
        for(int i = 0; i < words.length;i++){
            word[i].print();
        }
    }
    /*글을 읽지 않고 있는 시간 구하기*/
    private void UknownTime(final String EyeState) {
        final boolean[] stopped = {false};
        new Thread(new Runnable() {
            @Override
            public void run() {

//                    if(EyeState.equals("FIXATION") || EyeState.equals("SACCADE")){
//                        a_start = System.currentTimeMillis();
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        a_end = System.currentTimeMillis();
//                        a_total += (a_end - a_start) / 1000;
//                    }

                if (("FIXATION".equals(EyeState) || "SACCADE".equals(EyeState))) { // 시선이 책이 아닌 곳에 가있을 때
                    try {
                        if (u_start == 0) {
                            u_start = System.currentTimeMillis();
                        }
                        Thread.sleep(100); // 0.1초마다 반복 호출
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    stopped[0] = true; // while문 벗어나기 위해 boolean값 변경
                    u_end = System.currentTimeMillis();
                    if (u_start == 0 || u_end == 0) {

                    } else {
                        Log.i(TAG, "U_Start time " + u_start + " " + u_end);

                        u_total += ((u_end - u_start) / 1000); // 집중하지 않은 시간 측정 후 이전 기록과 합.
                    }

                    Log.i(TAG, "U_TOTAL: " + u_total);
                    u_start = 0; // 시작시간 초기화

                }
            }

        }).start();
    }

    //수정
    private double Read_Speed(){
        read_speed  = (text / (total-u_total))*10;
        Log.i(TAG, "Read Speed: " + read_speed);
        return read_speed;
    }
    public void balloon (String str,int x, int y){
        if(balloon_true){
            balloon_true = false;
            balloon = new Balloon.Builder(getApplicationContext())
                    .setArrowSize(10)
                    .setArrowOrientation(ArrowOrientation.TOP)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    .setArrowPosition(0.5f)
                    .setWidth(BalloonSizeSpec.WRAP)
                    .setHeight(65)
                    .setTextSize(25f)
                    .setCornerRadius(4f)
                    .setAlpha(0.9f)
                    .setText(str)
                    .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                    .setTextIsHtml(true)
                    .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();
            balloon.show(textureView,(int) x,(int) y-10);

            balloon.dismissWithDelay(7000L);
        }


    }
}



class Word {
    String name;
    int min_x;
    int max_x;
    int min_y;
    int max_y;

    public Word(){
        this.name = "";
        this.min_x = 0;
        this.max_x = 0;
        this.min_y = 0;
        this.max_y = 0;
    }
    public Word(String name, int max_x, int min_x, int min_y, int max_y){
        this.name = name;
        this.min_x = min_x;
        this.max_x = max_x;
        this.min_y = min_y;
        this.max_y = max_y;
    }

    public void print(){
//        System.out.println("name: " + name);
//        System.out.println("min_x: " + min_x);
//        System.out.println("max_x: " + max_x);
//        System.out.println("min_y: " + min_y);
//        System.out.println("max_y: " + max_y);
        Log.i("WordPrint","name: " + name);
        Log.i("WordPrint","min_x: " + min_x);
        Log.i("WordPrint","max_x: " + max_x);
        Log.i("WordPrint","min_y: " + min_y);
        Log.i("WordPrint","max_y: " + max_y);
    }

    public void setName(String name){
        this.name=name;
    }
    public void setMin_x(int min_x){
        this.min_x=min_x;
    }
    public void setMin_y(int min_y){
        this.min_y=min_y;
    }
    public void setMax_x(int max_x){
        this.max_x=max_x;
    }
    public void setMax_y(int max_y){
        this.max_y=max_y;
    }

    public String getName(){return name;}
    public int getMin_x(){return min_x;}
    public int getMax_x(){return max_x;}
    public int getMin_y(){return min_y;}
    public int getMax_y(){return max_x;}

}