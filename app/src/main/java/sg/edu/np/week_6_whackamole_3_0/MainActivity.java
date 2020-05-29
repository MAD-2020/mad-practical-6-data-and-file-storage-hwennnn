package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String FILENAME = "MainActivity.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    MyDBHandler dbhandler;
    private TextView username_field, password_field;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username_field = findViewById(R.id.login_username_field);
        password_field = findViewById(R.id.login_password_field);


        dbhandler = new MyDBHandler(this);

        Button login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = username_field.getText().toString();
                String password = password_field.getText().toString();

                Log.v(TAG, FILENAME + ": Logging in with: " + username + ": " + password);

                if (!isValidUser(username, password)){
                    Log.v(TAG, FILENAME + ": Invalid user!");
                    resetText();
                    return;
                }

                // redirect to level select page
                Log.v(TAG, FILENAME + ": Valid User! Logging in");
                Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                Bundle extras = new Bundle();
                extras.putString("username", username);
                intent.putExtras(extras);
                startActivity(intent);


            }
        });

        TextView redirect_signup = findViewById(R.id.redirect_signup);
        redirect_signup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG, FILENAME + ": Create new user!");
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                return true;
            }
        });


    }

    protected void onStop(){
        super.onStop();
        finish();
    }

    public boolean isValidUser(String userName, String password){

        UserData userData = dbhandler.findUser(userName);

        if (userData == null){
            Log.d(TAG, FILENAME+" Could not find the username!");
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.v(TAG, FILENAME + ": Running Checks..." + userData.getMyUserName() + ": " + userData.getMyPassword() +" <--> "+ userName + " " + password);

        if (!userData.getMyPassword().equals(password)){
            Log.d(TAG, FILENAME+" Password entered is invalid!");
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void resetText(){
        username_field.setText("");
        password_field.setText("");
    }

}
