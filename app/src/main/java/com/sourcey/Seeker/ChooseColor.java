package com.sourcey.Seeker;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseColor extends Activity {
    private Button black,red,green,blue,gray,yellow,cyan,magenta,pink,darkgreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_color);
        processViews();
        processControllers();
    }
    private void processViews(){
        black=(Button) findViewById(R.id.black);
        red=(Button) findViewById(R.id.red);
        green=(Button) findViewById(R.id.green);
        blue=(Button) findViewById(R.id.blue);
        gray=(Button) findViewById(R.id.gray);
        yellow=(Button) findViewById(R.id.yellow);
        cyan=(Button) findViewById(R.id.cyan);
        magenta=(Button) findViewById(R.id.magenta);
        pink=(Button) findViewById(R.id.pink);
        darkgreen=(Button) findViewById(R.id.darkgreen);
    }
    private void processControllers(){
        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#000000";
                ChooseColor.this.finish();
            }
        });
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#FFFF0000";
                ChooseColor.this.finish();
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#FF00FF00";
                ChooseColor.this.finish();
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#FF0000FF";
                ChooseColor.this.finish();
            }
        });
        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#FF888888";
                ChooseColor.this.finish();
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#FFFFFF00";
                ChooseColor.this.finish();
            }
        });
        cyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#FF00FFFF";
                ChooseColor.this.finish();
            }
        });
        magenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#FFFF00FF";
                ChooseColor.this.finish();
            }
        });
        pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#eca5cf";
                ChooseColor.this.finish();
            }
        });
        darkgreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom.Mycolor="#334f1b";
                ChooseColor.this.finish();
            }
        });
    }
}
