package com.sourcey.Seeker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class Adjust extends AppCompatActivity {
    Button personaldatabtn;
    Button friendscreenbtn;
    Button changepasswordbtn;
    Button logoutbtn;
    Button back;
    CheckBox displayfreewifi;
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵
            Intent intent=new Intent(Adjust.this,MainScreen.class);
            startActivity(intent);
            Adjust.this.finish();
        }

        return false;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);
        processViews();
        processControllers();
    }
    private void processViews(){
        personaldatabtn=(Button) findViewById(R.id.personaldatabtn);
        friendscreenbtn=(Button) findViewById(R.id.gotofriendscreen);
        changepasswordbtn=(Button) findViewById(R.id.gotochangepassword);
        logoutbtn=(Button) findViewById(R.id.logout);
        displayfreewifi=(CheckBox) findViewById(R.id.displayfreewifi);
        back=(Button) findViewById(R.id.setting_back);
        String checked=getConfig("wifi","show","");
        if(checked.equals("1")){
            displayfreewifi.setChecked(true);
        }
        else{
            displayfreewifi.setChecked(false);
        }
    }
    private void processControllers(){
        personaldatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Adjust.this,PersonalInformation.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Adjust.this,MainScreen.class);
                startActivity(intent);
                Adjust.this.finish();
            }
        });
        friendscreenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Adjust.this,FriendScreen.class);
                startActivity(intent);

            }
        });
        changepasswordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Adjust.this,SettingPassword.class);
                startActivity(intent);
            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Adjust.this,Login.class);
                startActivity(intent);
                Adjust.this.finish();
            }
        });

        displayfreewifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setConfig("wifi","show","1");
                }
                else{
                    setConfig("wifi","show","0");
                }
            }
        });
    }
    public  void setConfig(String name, String key,
                                 String value)
    {
        SharedPreferences settings =getSharedPreferences(name,0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(key, value);
        PE.commit();
    }
    public  String getConfig( String name , String
            key , String def)
    {
        SharedPreferences settings =getSharedPreferences(name,0);
        return settings.getString(key, def);
    }
}
