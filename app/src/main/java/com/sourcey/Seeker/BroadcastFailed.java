package com.sourcey.Seeker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class BroadcastFailed extends AppCompatActivity {
    private Button back;
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵
            sendBroadcast(new Intent("CloseActivities"));
            Intent intent=new Intent(BroadcastFailed.this,MainScreen.class);
            startActivity(intent);
            BroadcastFailed.this.finish();

        }

        return false;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_failed);
        back=(Button) findViewById(R.id.broadcast_back3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BroadcastFailed.this,BroadcastSetting.class);
                startActivity(intent);
                BroadcastFailed.this.finish();
            }
        });
    }
}
