package com.muratipek.munote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChronometerActivity extends AppCompatActivity {

    TextView textView;
    Button startButton;
    EditText editText;
    int second;
    int minute;
    int hour;
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometer);

        textView = findViewById(R.id.textView);
        startButton = findViewById(R.id.startButton);
        second = 0;
        minute = 0;
        hour = 0;
    }
    public void start(View view){
        handler = new Handler(Looper.getMainLooper());
        startButton.setEnabled(false);
        runnable = new Runnable() {
            @Override
            public void run() {

                if (second < 10 && minute == 0 && hour == 0) {
                    textView.setText("00:00:0" + second);
                }else if (minute == 0 && hour == 0){
                    textView.setText("00:00:" + second);
                }else if (second < 10 && minute < 10 && hour == 0){
                    textView.setText("00:0" + minute + ":0" + second);
                }else if (minute < 10 && hour == 0){
                    textView.setText("00:0" + minute + ":" + second);
                }else if (second < 10 && hour == 0){
                    textView.setText("00:" + minute + ":0" + second);
                }else if (hour == 0){
                    textView.setText("00:" + minute + ":" + second);
                }else if (second < 10 && minute < 10 && hour < 10){
                    textView.setText("0" + hour + ":0" + minute + ":0" + second);
                }else if (minute < 10 && hour < 10){
                    textView.setText("0" + hour + ":0" + minute + ":" + second);
                }else if (second < 10 && hour < 10){
                    textView.setText("0" + hour + ":" + minute + ":0" + second);
                }else if (hour < 10){
                    textView.setText("0" + hour + ":" + minute + ":" + second);
                }else if (second < 10 && minute < 10){
                    textView.setText(hour + ":0" + minute + ":0" + second);
                }else if (minute < 10){
                    textView.setText(hour + ":0" + minute + ":" + second);
                }else if (second < 10){
                    textView.setText(hour + ":" + minute + ":0" + second);
                }else{
                    textView.setText(hour + ":" + minute + ":" + second);
                }
                second++;
                if(second == 60){
                    second = 0;
                    minute++;
                }
                if(minute == 60){
                    minute = 0;
                    hour++;
                }
                if(hour == 99){
                    textView.setText("This Is the End");
                    handler.removeCallbacks(runnable);
                }
                handler.postDelayed(runnable, 1000);
            }
        };
        handler.post(runnable);
    }
    public void stop(View view){
        startButton.setEnabled(true);
        handler.removeCallbacks(runnable);
        second = 0;
        minute = 0;
        hour = 0;
        textView.setText("00:00:00");
    }
    public void limit(View view){

    }
}