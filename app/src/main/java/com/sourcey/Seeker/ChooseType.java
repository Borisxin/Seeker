package com.sourcey.Seeker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class ChooseType extends AppCompatActivity {

    private Button capsule,puzzle,broadcast,whisper;
    private Button back;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);
        processViews();
        processControllers();
        registerReceiver(close_myself, new IntentFilter("CloseActivities"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(close_myself);
    }
    private void processViews(){
        capsule=(Button) findViewById(R.id.choosecapsule);
        puzzle=(Button) findViewById(R.id.choosepuzzle);
        broadcast=(Button) findViewById(R.id.choosebroad);
        whisper=(Button) findViewById(R.id.choosewhisper);
        back=(Button) findViewById(R.id.choosetypeback);
    }
    private void processControllers(){
        capsule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ChooseType.this,CapsulePhoto.class);
                startActivity(intent);
            }
        });
        puzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ChooseType.this,PuzzlePhoto.class);
                startActivity(intent);
            }
        });
        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ChooseType.this,BroadcastPhoto.class);
                startActivity(intent);
            }
        });
        whisper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ChooseType.this,WhisperPhoto.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private final BroadcastReceiver close_myself = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("要被關了","choosetype");
            finish();
        }
    };
}
