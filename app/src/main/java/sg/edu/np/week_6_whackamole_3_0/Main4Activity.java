package sg.edu.np.week_6_whackamole_3_0;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import static java.lang.String.format;
import static java.lang.String.valueOf;

public class Main4Activity extends AppCompatActivity {


    private static final String FILENAME = "Main4Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    private boolean resumeFlag = false; //this is to make sure the placeNewMole() methods will not run during the countdown
    Random ran = new Random();
    CountDownTimer readyTimer;
    int delay,level;
    String username;
    private int score = 0;

    private int last_location,last_location2 = 0;
    TextView result;
    private Button back_btn;
    private MyDBHandler dbHandler;


    private final Handler mhandler = new Handler();
    private final Runnable mrunnable = new Runnable() { //it is to make it run on thread as Only the original thread that created a view hierarchy can touch its views.
        @Override
        public void run() {
            Log.v(TAG, "New Mole Location!");
            setNewMole();
            //recursive to call the runnable
            mhandler.postDelayed(this, delay*1000); // this will be called every "delay"
        }
    };


    private void readyTimer(){

        readyTimer = new CountDownTimer(10000, 1000){
            public void onTick(long millisUntilFinished){
                final Toast toast = Toast.makeText(getApplicationContext(), format("Get Ready In %d seconds",millisUntilFinished/1000), Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Ready CountDown!" + millisUntilFinished/ 1000);
                Handler toast_handler = new Handler();
                toast_handler.postDelayed(new Runnable() { //this is to make sure every toast only run for 1 second
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1000);
            }

            public void onFinish(){
                Toast.makeText(getApplicationContext(), "GO!", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "Ready CountDown Complete!");
                readyTimer.cancel();
                setNewMole();
                placeMoleTimer();
                populateBtns();
                resumeFlag = true; // onResume() can work rn after the countdown

            }
        };
        readyTimer.start();
    }

    private void placeMoleTimer(){
        mhandler.postDelayed(mrunnable, delay*1000); // this will be called every "delay"
    }

    private static final int[] BUTTON_IDS = {R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
            R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        dbHandler = new MyDBHandler(this);

        result = findViewById(R.id.message);
        back_btn = findViewById(R.id.back_btn);

        Bundle b = getIntent().getExtras();
        username = b.getString("username");
        level = b.getInt("level");
        delay = Math.abs(level - 11); // to get the interval time of the placemoletimer. For example, level 10 will get 1s delay while level 1 will get 10s delay.
        Log.v(TAG, FILENAME+ ": Load level " + level + " for: " + username);
        Log.d(TAG, "Level: "+ level + " Delay: "+ delay + "s");

        readyTimer();
        result.setText(String.valueOf(score));
        Log.v(TAG, "Current User Score: " + score);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserScore();
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onPause() {
        // to make sure placemoletimer is not running when the activity is on paused
        Log.v(TAG,"Pause");
        mhandler.removeCallbacks(mrunnable); //this is to interrupt the runnable of placemoletimer
        super.onPause();
    }

    @Override
    protected void onResume() {
        // to resume the place mole timer from the pause state
        Log.v(TAG,"Resume!");
        if (resumeFlag){
            placeMoleTimer(); //this will only be triggered after the countdown by turning the flag
        }
        super.onResume();
    }

    private void doCheck(Button checkButton)
    {
        if (checkButton.getText().toString().equals("*")){
            score++;
            Log.v(TAG, "Hit, score added!");
        }else{
            if (score > 0){
                score--;
                Log.v(TAG, "Missed, point deducted!");
            }else{
                Log.v(TAG, "Missed Hit!");
            }
        }
        result.setText(valueOf(score));

    }

    private void populateBtns(){
        for(final int id : BUTTON_IDS){
            Button btn = findViewById(id);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int _id = view.getId();
                    Button click_btn = (Button) findViewById(_id);
                    doCheck(click_btn);
                    //methods below is to avoid calling of setNewMole() concurrently
                    mhandler.removeCallbacks(mrunnable); //this is to interrupt the runnable of placemoletimer
                    setNewMole(); // this will be triggered after user has clicked the button
                    placeMoleTimer(); // the runnable will run again

                }
            });
        }
    }

    public void setNewMole()
    {
        if (level < 6){ // set one random mole
            int randomLocation = ran.nextInt(9);
            Button this_btn = findViewById(BUTTON_IDS[randomLocation]);
            Button last_btn = findViewById(BUTTON_IDS[last_location]);
            last_btn.setText("O");
            this_btn.setText("*");
            last_location = randomLocation;
        }else{ // set two random mole
            int randomLocation = ran.nextInt(9);
            int randomLocation2 = ran.nextInt(9);
            while (randomLocation == randomLocation2){ // to make sure two random location is distinct
                randomLocation2 = ran.nextInt(9);
            }
            Button last_btn1 = findViewById(BUTTON_IDS[last_location]);
            Button last_btn2 = findViewById(BUTTON_IDS[last_location2]);
            last_btn1.setText("O");
            last_btn2.setText("O");

            Button this_btn = findViewById(BUTTON_IDS[randomLocation]);
            Button this_btn2 = findViewById(BUTTON_IDS[randomLocation2]);
            this_btn.setText("*");
            this_btn2.setText("*");

            last_location = randomLocation;
            last_location2 = randomLocation2;

        }

    }

    private void updateUserScore()
    {
        Log.v(TAG, FILENAME + ": Update User Score...");
        UserData userData = dbHandler.findUser(username);

        readyTimer.cancel(); // cancel the countdown timer if it is running
        mhandler.removeCallbacks(mrunnable); // this is to interrupt the runnable of placemoletimer


        int highest_score = userData.getScores().get(level-1);

        if (score > highest_score){
            // only update the score when the current score > highest record
            Log.d(TAG, "updateUserScore;");
            userData.getScores().set(level-1,score);
            dbHandler.deleteAccount(username);
            dbHandler.addUser(userData);
        }

        // redirect back to level select page
        Log.v(TAG, FILENAME + ": Redirect to level select page");
        Intent activityName = new Intent(Main4Activity.this, Main3Activity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        activityName.putExtras(extras);

        startActivity(activityName);
    }

}
