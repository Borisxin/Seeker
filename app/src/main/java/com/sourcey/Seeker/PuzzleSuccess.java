package com.sourcey.Seeker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;


public class PuzzleSuccess extends AppCompatActivity {

    private Button next;
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵
            sendBroadcast(new Intent("CloseActivities"));
            Intent intent=new Intent(PuzzleSuccess.this,MainScreen.class);
            startActivity(intent);
            PuzzleSuccess.this.finish();
        }

        return false;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_success);
        processViews();
        processControllers();
    }

    private void processViews(){
        next=(Button) findViewById(R.id.puzzle_next3);
    }
    private void processControllers(){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("CloseActivities"));
                Intent intent=new Intent(PuzzleSuccess.this,MainScreen.class);
                PuzzleSuccess.this.finish();
                startActivity(intent);
            }
        });
    }
}
