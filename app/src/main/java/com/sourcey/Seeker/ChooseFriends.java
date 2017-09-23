package com.sourcey.Seeker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;


public class ChooseFriends extends Activity {

    private TextView textView;
    private Button yes,no;
    private List<String> friend=WhisperSetting.friendlist;
    private List<Integer> checked=WhisperSetting.checkedfriends;
    private boolean success=WhisperSetting.hasfriend;
    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friends);
        processViews();
        processControllers();
        if(success){
            textView.setText("請選擇要傳送的對象");
        }
        else{
            textView.setText("您目前沒有好友喔");
        }
        generatebox(friend,checked);
    }
    private void processViews(){
        yes=(Button) findViewById(R.id.choosefriendyes);
        no=(Button) findViewById(R.id.choosefriendno);
        textView=(TextView) findViewById(R.id.choosefriendtext);
        layout=(LinearLayout) findViewById(R.id.checkboxlayout);
    }
    private void processControllers(){
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
                ChooseFriends.this.finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseFriends.this.finish();
            }
        });
    }
    private void check(){
        WhisperSetting.chosenfriendlist.clear();
        WhisperSetting.checkedfriends.clear();
        WhisperSetting.chosenfriendaccountlist.clear();
        int c=layout.getChildCount();
        for(int i=0;i<c;i++){
            View v=layout.getChildAt(i);
            CheckBox cb= (CheckBox) v;
            if(cb.isChecked()==true) {
                WhisperSetting.chosenfriendlist.add(cb.getText().toString());
                WhisperSetting.checkedfriends.add(1);
                WhisperSetting.chosenfriendaccountlist.add(WhisperSetting.friendaccountlist.get(i));
            }
            else{
                WhisperSetting.checkedfriends.add(0);
            }
        }
    }
    private void generatebox(List<String> friend,List<Integer> checked){
        int n=friend.size();
        for(int i=0;i<n;i++){
            CheckBox cb=new CheckBox(this);
            cb.setText(friend.get(i));
            if(checked.get(i)==1) {
                cb.setChecked(true);
            }
            layout.addView(cb);
        }
    }

}
