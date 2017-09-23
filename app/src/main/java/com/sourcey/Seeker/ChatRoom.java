    package com.sourcey.Seeker;

    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Color;
    import android.os.Handler;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.text.Spannable;
    import android.text.SpannableStringBuilder;
    import android.text.Spanned;
    import android.text.style.AbsoluteSizeSpan;
    import android.text.style.ForegroundColorSpan;
    import android.text.style.StyleSpan;
    import android.util.Log;
    import android.view.KeyEvent;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ScrollView;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.io.BufferedReader;
    import java.io.BufferedWriter;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.OutputStreamWriter;
    import java.io.PrintStream;
    import java.io.UnsupportedEncodingException;
    import java.net.Socket;
    import java.util.Timer;
    import java.util.TimerTask;

    public class ChatRoom extends AppCompatActivity {
        public static Handler mHandler = new Handler();
        private Button color;
        private TextView number;
        private TextView chat;
        private EditText talk;
        private Button submit;
        private Button back;
        private Socket client=null;
        private static  Toast toast;
        private String servererror="Server端異常，無法連上聊天室";
        private String temp;
        private String tempcolor;
        private String tempnum;
        private  int seconds=0;
        private boolean cansubmit=true;
        private BufferedReader reader;
        private PrintStream writer;
        private String nickname=Login.NICKNAME;
        private ScrollView scrollView;
        private Boolean isconnect=false;
        private Boolean isclosed=false;
        private Timer heartBeatTimer=null;
        public static  String Mycolor="#FF000000";

        @Override
        protected void onDestroy() {
            super.onDestroy();
            isclosed=true;
            if(heartBeatTimer != null)
            heartBeatTimer.cancel();
        }

        public void onBackPressed() {
            super.onBackPressed();
            Thread thread=new Thread(closeconnect);
            thread.start();
            ChatRoom.this.finish();
        }
        public boolean onKeyDown(int keyCode,KeyEvent event){

            if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵
                Thread thread=new Thread(closeconnect);
                thread.start();
                ChatRoom.this.finish();
            }

            return false;

        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_room);
            Thread connect=new Thread(connectthread);
            connect.start();
            processViews();
            processControllers();
        }
        private void processViews(){
            back=(Button) findViewById(R.id.chatroom_back);
            chat=(TextView) findViewById(R.id.Chat);
            talk=(EditText) findViewById(R.id.Talk);
            submit=(Button) findViewById(R.id.chatSubmit);
            scrollView=(ScrollView) findViewById(R.id.scroll);
            color=(Button) findViewById(R.id.changecolor);
            number=(TextView) findViewById(R.id.countpeople);
        }
        private void processControllers(){
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!talk.getText().toString().isEmpty() && cansubmit) {
                        Thread submitThread = new Thread(submitcontent);
                        submitThread.start();
                        cansubmit=false;
                        Thread wait=new Thread(WaitThreeSeconds);
                        wait.start();
                    }
                    else if(!cansubmit){
                        makeTextAndShow(ChatRoom.this,"請等待"+seconds+"秒後再傳送");
                    }
                    else if(talk.getText().toString().isEmpty()){
                        makeTextAndShow(ChatRoom.this,"請輸入訊息後再傳送");
                    }

                }
            });
            color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ChatRoom.this,ChooseColor.class);
                    startActivity(intent);
                }
            });
        }
        private static void makeTextAndShow(final Context context, final String text) {
            if (toast == null) {
                //如果還沒有用過makeText方法，才使用
                toast = android.widget.Toast.makeText(context, text, Toast.LENGTH_SHORT);
            } else {
                toast.setText(text);
                toast.setDuration(Toast.LENGTH_SHORT);
            }
            toast.show();
        }
        private  Runnable welcome= new Runnable() {
            @Override
            public void run() {
                if(client!=null && isconnect){
                    BufferedWriter bw;
                    try {
                        // 取得網路輸出串流
                        bw = new BufferedWriter( new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
                        // 寫入訊息
                        bw.write( "##### "+nickname+" 已經進入聊天室"+" #####\n");
                        bw.write(Mycolor+"\n");
                        // 立即發送
                        bw.flush();
                    } catch (IOException e) {

                    }
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(servererror);
                            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(90), 0, servererror.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            chat.append(spannableStringBuilder);
                            chat.append("\n");
                        }
                    });
                }
            }
        };
        private  Runnable closeconnect= new Runnable() {
            @Override
            public void run() {
                if(client!=null && isconnect){
                    BufferedWriter bw;
                    try {
                        // 取得網路輸出串流
                        bw = new BufferedWriter( new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
                        // 寫入訊息
                        bw.write("This Connection will be closed\n");
                        // 立即發送
                        bw.flush();
                        client.close();
                        isconnect=false;
                    } catch (IOException e) {
                        Intent intent=new Intent(ChatRoom.this,MainScreen.class);
                        startActivity(intent);
                    }
                }
            }
        };
        private  Runnable submitcontent= new Runnable() {
            @Override
            public void run() {
                if(client!=null && isconnect){
                    BufferedWriter bw;
                    try {
                        // 取得網路輸出串流
                        bw = new BufferedWriter( new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
                        // 寫入訊息
                        bw.write(nickname+": ");
                        bw.write(talk.getText().toString()+"\n");
                        bw.write(Mycolor+"\n");
                        // 立即發送
                        bw.flush();
                        } catch (IOException e) {
                    }
                         runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             talk.setText("");
                         }
                       });
                 }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            talk.setText("");
                            SpannableStringBuilder span1 = new SpannableStringBuilder("[系統公告]");
                            SpannableStringBuilder span2 = new SpannableStringBuilder(servererror);
                            span1.setSpan(new AbsoluteSizeSpan(60), 0, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            span1.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),0,6,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            span2.setSpan(new AbsoluteSizeSpan(60), 0, servererror.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            chat.append(span1);
                            chat.append("\n");
                            chat.append(span2);
                            chat.append("\n");
                        }
                    });
                }
        }
    };
        private  Runnable connectthread=new Runnable() {
    @Override
    public void run() {
            try {
                client = new Socket("134.208.97.233", 6004);
                isconnect=true;
                client.setKeepAlive(true);
                InputStreamReader streamReader = new InputStreamReader(client.getInputStream());
                reader = new BufferedReader(streamReader);
                writer = new PrintStream(client.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(
                client.getInputStream(),"UTF-8"));
                Thread thread=new Thread(welcome);
                thread.start();
                startHeartBeatThread();
                while (client.isConnected() && !isclosed) {
                String msg,color,num;
                msg=br.readLine();
                color=br.readLine();
                num=br.readLine();
                temp = msg;
                tempcolor=color;
                tempnum=num;
                if(temp!=null)
                mHandler.post(updateText);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        };
        private Runnable updateText = new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder(temp);
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(tempcolor));
                        spannableStringBuilder.setSpan(colorSpan,0,temp.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        int a=temp.indexOf("CurrentNumberinchat$$");
                        if(a == 0){
                            spannableStringBuilder.replace(0,21,"");
                            number.setText("目前人數為："+spannableStringBuilder);
                        }
                        else {
                            chat.append(spannableStringBuilder);
                            chat.append("\n");
                            scrollView.fullScroll(View.FOCUS_DOWN);
                            number.setText(tempnum);
                        }
                    }
                });
            }
        };
        private Runnable WaitThreeSeconds=new Runnable() {
            @Override
            public void run() {
                try {
                    seconds=3;
                    for(int i=0;i<3;i++) {
                        Thread.sleep(1000);
                        seconds--;
                    }
                    cansubmit=true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        private void startHeartBeatThread() {
            // 启动心跳线程
            heartBeatTimer = new Timer();
            TimerTask heartBeatTask = new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    sendOrder("heartbeat\n");
                }
            };
            heartBeatTimer.schedule(heartBeatTask, 20000, 20000);
        }
        private void sendOrder(String order){
            try {
                BufferedWriter bw;
                bw = new BufferedWriter( new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
                bw.write(order);
                bw.flush();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
