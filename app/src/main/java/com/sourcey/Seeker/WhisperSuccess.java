package com.sourcey.Seeker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class WhisperSuccess extends AppCompatActivity {

    private Button next;
    private TextView countdown;

    private long diff=WhisperSetting.dayDiff;
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵
            sendBroadcast(new Intent("CloseActivities"));
            Intent intent=new Intent(WhisperSuccess.this,MainScreen.class);
            startActivity(intent);
            WhisperSuccess.this.finish();
        }

        return false;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whisper_success);
        processViews();
        processControllers();
    }
    private void processViews(){
        next=(Button) findViewById(R.id.whisper_next3);
        countdown=(TextView) findViewById(R.id.whisperdisappeartime);
    }
    private void processControllers(){
        countdown.setText(String.valueOf(diff)+"天");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("CloseActivities"));
                Intent intent=new Intent(WhisperSuccess.this,MainScreen.class);
                startActivity(intent);
                WhisperSuccess.this.finish();
            }
        });
    }
}
