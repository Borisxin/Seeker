package com.sourcey.Seeker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Show_Capsule_Time extends AppCompatActivity {

    private Button back;
    private TextView time;
    private long diff=0;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__capsule__time);
        Intent intent=getIntent();
        diff=intent.getLongExtra("diff",0);
        processViews();
        processControllers();
    }
    private void processViews(){
        back=(Button) findViewById(R.id.showcapsuletimeback);
        time=(TextView) findViewById(R.id.showcapsuletimetext);
    }
    private void processControllers(){
        time.setText(diff+"天");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
