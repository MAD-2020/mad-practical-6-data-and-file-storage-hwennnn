package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.String.format;

public class Main2Activity extends AppCompatActivity {
    /* Hint:
        1. This is the create new user page for user to log in
        2. The user can enter - Username and Password
        3. The user create is checked against the database for existence of the user and prompts
           accordingly via Toastbox if user already exists.
        4. For the purpose the practical, successful creation of new account will send the user
           back to the login page and display the "User account created successfully".
           the page remains if the user already exists and "User already exist" toastbox message will appear.
        5. There is an option to cancel. This loads the login user page.
     */


    private static final String FILENAME = "Main2Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    private TextView username, password;
    private Button register_btn, cancel_btn;
    MyDBHandler dbhandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        username = findViewById(R.id.create_username_field);
        password = findViewById(R.id.create_password_field);
        register_btn = findViewById(R.id.create_btn);
        cancel_btn = findViewById(R.id.cancel_btn);


        dbhandler = new MyDBHandler(this);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String _username = username.getText().toString();
                String _password = password.getText().toString();

                Log.d(TAG, FILENAME + format(": New user creation with %s: %s",_username,_password));

                if (dbhandler.findUser(_username) != null){
                    Log.d(TAG, "User already exist during new user creation.");
                    resetText();
                    return;
                }

                ArrayList<Integer> initial_level_list = new ArrayList<>();
                ArrayList<Integer> initial_score_list = new ArrayList<>();
                initial_list(initial_score_list, initial_level_list);

                UserData data = new UserData(_username, _password, initial_level_list, initial_score_list);
                dbhandler.addUser(data);
                Log.v(TAG, FILENAME + ": New user created successfully!");
                resetText();

                Intent activityName = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(activityName);
                Toast.makeText(Main2Activity.this, "The account is created successfully.", Toast.LENGTH_SHORT).show();


            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect_to_login = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(redirect_to_login);
                finish();
            }
        });
    }

    protected void onStop() {
        super.onStop();
        finish();
    }

    public void initial_list(ArrayList<Integer> scoreList, ArrayList<Integer> levelList){
        for (int i = 1; i < 11; i++){
            levelList.add(i);
            scoreList.add(0);
        }

    }

    private void resetText(){
        username.setText("");
        password.setText("");
    }
}
