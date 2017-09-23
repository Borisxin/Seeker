package com.sourcey.Seeker;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;




public class Activity_Detail extends Activity {
    public static ActivityItem activityItem=null;
    private TextView ActivityName;
    private TextView Address;
    private TextView Store;
    private TextView Start;
    private TextView End;
    private TextView Content;
    private TextView Phone;
    private Button close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__detail);
        processViews();
        processControllers();
    }
    private void processViews(){
        ActivityName=(TextView) findViewById(R.id.DetailActivityName);
        Address=(TextView) findViewById(R.id.DetailAddress);
        Store=(TextView) findViewById(R.id.DetailStoreName);
        Start=(TextView) findViewById(R.id.DetailStart);
        End=(TextView) findViewById(R.id.DetailEnd);
        Content=(TextView) findViewById(R.id.DetailContent);
        Phone=(TextView) findViewById(R.id.DetailPhone);
        close=(Button)findViewById(R.id.DetailClose);
    }
    private void processControllers(){
        ActivityName.setText(activityItem.getActivity_Name());
        Address.setText(activityItem.getAddress());
        Store.setText(activityItem.getStore_Name());
        Start.setText(activityItem.getStartDate());
        End.setText(activityItem.getFinishDate());
        Content.setText(activityItem.getContent());
        Phone.setText(activityItem.getPhone());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Detail.this.finish();
            }
        });
    }
}
