package com.sourcey.Seeker;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddFriendScreen extends AppCompatActivity {

    private AddFriendByAccount addFriendByAccount;
    private AddFriendByQRCode addFriendByQRCode;
    private android.app.FragmentManager manager;
    private Button QRCODE,ACCOUNT;
    private Button back;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(AddFriendScreen.this,FriendScreen.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_screen);
        processViews();
        processControllers();
        addFriendByAccount=new AddFriendByAccount();
        addFriendByQRCode=new AddFriendByQRCode();
        manager=getFragmentManager();
        manager.beginTransaction()
                .add(R.id.FriendLayout,addFriendByQRCode,"TAG-QRCODE")
                .add(R.id.FriendLayout,addFriendByAccount,"TAG-ACCOUNT")
                .hide(addFriendByAccount)
                .commit();
    }
    private void processViews(){
        back=(Button) findViewById(R.id.addfriendback);
        QRCODE=(Button) findViewById(R.id.ChangeQR);
        ACCOUNT=(Button) findViewById(R.id.ChangeAcc);
    }
    private void processControllers(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddFriendScreen.this,FriendScreen.class);
                startActivity(intent);
                finish();
            }
        });
        QRCODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeToQRCode();
            }
        });
        ACCOUNT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeToAccount();
            }
        });
    }
    public void ChangeToAccount(){
        manager.beginTransaction()
                .hide(addFriendByQRCode)
                .show(addFriendByAccount)
                .addToBackStack(null)
                .commit();
    }
    public void ChangeToQRCode(){
        manager.beginTransaction()
                .hide(addFriendByAccount)
                .show(addFriendByQRCode)
                .addToBackStack(null)
                .commit();
    }
}
