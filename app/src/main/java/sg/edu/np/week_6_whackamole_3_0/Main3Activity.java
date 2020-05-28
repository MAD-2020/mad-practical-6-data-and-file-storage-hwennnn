package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static java.lang.String.*;

public class Main3Activity extends AppCompatActivity {

    private static final String FILENAME = "Main3Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    private String username;
    MyDBHandler handler;
    RecyclerView mRecyclerView;
    CustomScoreAdaptor myAdapter;
    private Button back_to_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        handler = new MyDBHandler(this);
        back_to_login = findViewById(R.id.back_to_login);

        Bundle b = getIntent().getExtras();
        username = b.getString("username");

        Log.v(TAG, FILENAME + ": Show level for User: "+ username);

        UserData userData = handler.findUser(username);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new CustomScoreAdaptor(this, userData);
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new CustomScoreAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "onItemClick: "+position);
                nextLevelQuery(username, position+1);
            }
        });
        /* Hint:
        This method receives the username account data and looks up the database for find the
        corresponding information to display in the recyclerView for the level selections page.
         */

        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect_to_login = new Intent(Main3Activity.this, MainActivity.class);
                startActivity(redirect_to_login);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void nextLevelQuery(final String username, final int level){
        Log.v(TAG, "Advance option given to user!");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning! Whack-A-Mole Incoming!");
        builder.setMessage(format("Would you like to challenge Level %d ?",level));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Log.v(TAG, "User accepts!");
                nextLevel(username, level);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Log.v(TAG,"User decline!");
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void nextLevel(String username, int level){
        Intent activityName = new Intent(Main3Activity.this, Main4Activity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putInt("level", level);
        activityName.putExtras(extras);
        startActivity(activityName);
    }
}
