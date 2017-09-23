package com.sourcey.Seeker;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PuzzlePhoto extends AppCompatActivity {

    private DisplayMetrics mPhone;
    private final static int CAMERA = 66 ;
    private final static int PHOTO = 99 ;
    // 檔案名稱
    private String fileName;
    private String realName;
    // 照片
    private Context context=this;
    private Uri uri;
    public static String puzzleimagepath="";
    public static String puzzlecontent="";
    public static String puzzletitle="";
    private Button mCamera;
    private Button mPhoto;
    private Button nextPage;
    private Button lastPage;
    private EditText Title;
    private EditText content;
    private final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS_C=0;
    private final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private void dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(PuzzlePhoto.this); //創建訊息方塊

        builder.setMessage("確定要返回？");

        builder.setTitle("返回");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }

        });

        builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                PuzzlePhoto.this.finish();

            }

        });



        builder.create().show();

    }
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵

            dialog();

        }

        return false;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_photo);
        processViews();
        processControllers();
        registerReceiver(close_myself, new IntentFilter("CloseActivities"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(close_myself);
    }
    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == PHOTO  && data != null)
        {
            puzzleimagepath = GetFilePathFromDevice.getPath(PuzzlePhoto.this,data.getData());
        }
        else if(requestCode==CAMERA){
            File photo=FileUtil.getAlbumStorageDir();
            puzzleimagepath=photo.getAbsolutePath()+'/'+realName;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS_C: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intentCamera =
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 照片檔案名稱
                    File pictureFile = configFileName("P", ".jpg");
                    uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pictureFile);
                    // 設定檔案名稱
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    // 啟動相機元件
                    startActivityForResult(intentCamera, CAMERA);

                } else {
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PHOTO);
                } else {
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private File configFileName(String prefix, String extension) {
        fileName = FileUtil.getUniqueFileName();
        realName=prefix + fileName + extension;
        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + fileName + extension);
    }
    private void processViews(){
        //讀取手機解析度
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        mCamera = (Button) findViewById(R.id.puzzlecamera);
        mPhoto = (Button) findViewById(R.id.puzzlephoto);
        nextPage=(Button) findViewById(R.id.puzzle_next1);
        lastPage=(Button) findViewById(R.id.puzzle_back1);
        content=(EditText) findViewById(R.id.puzzletext);
        Title=(EditText) findViewById(R.id.puzzletitle);
    }
    private void processControllers(){
        lastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!puzzleimagepath.isEmpty()) {
                    puzzlecontent=content.getText().toString();
                    puzzletitle=Title.getText().toString();
                    Intent intent = new Intent(PuzzlePhoto.this, PuzzleSetting.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(PuzzlePhoto.this,"您還沒選擇相片喔",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(PuzzlePhoto.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        ||ContextCompat.checkSelfPermission(PuzzlePhoto.this,
                        android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PuzzlePhoto.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            ||ActivityCompat.shouldShowRequestPermissionRationale(PuzzlePhoto.this,
                            android.Manifest.permission.CAMERA)) {
                        new AlertDialog.Builder(PuzzlePhoto.this)
                                .setMessage("必須要有此權限才能開啟相機喔!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PuzzlePhoto.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA},
                                                MY_PERMISSIONS_REQUEST_READ_CONTACTS_C);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();

                    } else {
                        ActivityCompat.requestPermissions(PuzzlePhoto.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS_C);
                    }
                }
                else {
                    Intent intentCamera =
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 照片檔案名稱
                    File pictureFile = configFileName("P", ".jpg");
                    uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pictureFile);
                    // 設定檔案名稱
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    // 啟動相機元件
                    startActivityForResult(intentCamera, CAMERA);
                }
            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(PuzzlePhoto.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        &ContextCompat.checkSelfPermission(PuzzlePhoto.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PuzzlePhoto.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            &ActivityCompat.shouldShowRequestPermissionRationale(PuzzlePhoto.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(PuzzlePhoto.this)
                                .setMessage("必須要有此權限才能開啟相簿喔!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PuzzlePhoto.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();

                    } else {
                        ActivityCompat.requestPermissions(PuzzlePhoto.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                }
                else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PHOTO);
                }
            }
        });
    }
    private final BroadcastReceiver close_myself = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };


}
