package sg.edu.np.week_6_whackamole_3_0;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CustomScoreAdaptor extends RecyclerView.Adapter<CustomScoreViewHolder> {

    UserData _userData;
    ArrayList<Integer> _levelList;
    ArrayList<Integer> _scoreList;
    Context c;
    private static final String FILENAME = "CustomScoreAdaptor.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    private CustomScoreAdaptor.OnItemClickListener mListener;
    static View view;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(CustomScoreAdaptor.OnItemClickListener listener){
        this.mListener = listener;
    }

    public CustomScoreAdaptor(Context c, UserData userdata){
        this._userData = userdata;
        this.c = c;
        this._levelList = userdata.getLevels();
        this._scoreList = userdata.getScores();
    }

    public CustomScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
         view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_select,null);

        return new CustomScoreViewHolder(view, mListener);
    }

    public void onBindViewHolder(CustomScoreViewHolder holder, final int position){

        String score = String.valueOf(_scoreList.get(position));
        String level = String.valueOf(_levelList.get(position));

        holder.level.setText(level);
        holder.score.setText(score);
        
        Log.v(TAG, FILENAME + " Showing level " + level + " with highest score: " + score);

    }

    public int getItemCount(){
        return _levelList.size();
    }
}