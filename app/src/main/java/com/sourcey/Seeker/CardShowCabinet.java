package com.sourcey.Seeker;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

public class CardShowCabinet extends AppCompatActivity {

    DisplayImageOptions options;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    private ViewPager mViewPager1;
    private ViewPager mViewPager2;
    private android.support.design.widget.TabLayout mTabs;
    private Toolbar toolbar;
    private Button back;
    private MaterialSearchView searchView;
    private List<View> mviewPagerGridList1=new ArrayList<>();
    private List<View> mviewPagerGridList2=new ArrayList<>();
    private LayoutInflater inflater;
    /*A=時光膠囊 B=記憶拼圖 C=收到的悄悄話 D=送出的悄悄話 E=大聲公*/
    private GridView A_GridView;
    private GridView B_GridView;
    private GridView C_GridView;
    private GridView D_GridView;

    private GridView N_A_GridView;
    private GridView N_B_GridView;
    private GridView N_C_GridView;
    private GridView N_D_GridView;

    public static List<CapsuleCard> CapsuleList=new ArrayList<>();
    public static List<MyCard> MyCardList=new ArrayList<>();
    public static List<FriendsCard> RecieveList=new ArrayList<>();
    public static List<FriendsCard> GiveList=new ArrayList<>();

    public static List<CapsuleCard> N_CapsuleList=new ArrayList<>();
    public static List<MyCard> N_MyCardList=new ArrayList<>();
    public static List<FriendsCard> N_RecieveList=new ArrayList<>();
    public static List<FriendsCard> N_GiveList=new ArrayList<>();
    private ReleaseBitmap releaseBitmap=new ReleaseBitmap();
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_show_cabinet);
        inflater = LayoutInflater.from(getApplicationContext());
        processViews();
        processController();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBitmap.cleanBitmapList();
        A_GridView.setAdapter(null);
        B_GridView.setAdapter(null);
        C_GridView.setAdapter(null);
        D_GridView.setAdapter(null);
        N_A_GridView.setAdapter(null);
        N_B_GridView.setAdapter(null);
        N_C_GridView.setAdapter(null);
        N_D_GridView.setAdapter(null);
    }
    private void processViews(){
        imageLoader.setDefaultLoadingListener(releaseBitmap);
        options= new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.final_ninja)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(this).threadPoolSize(3).defaultDisplayImageOptions(options).build();
        imageLoader.init(config);

        mTabs=(android.support.design.widget.TabLayout) findViewById(R.id.tabs);
        searchView=(MaterialSearchView) findViewById(R.id.search_view);
        mViewPager1 = (ViewPager) findViewById(R.id.viewpager1);
        mViewPager2 = (ViewPager) findViewById(R.id.viewpager2);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        /*範圍內的卡片~*/
        A_GridView=(GridView) inflater.inflate(R.layout.pager,mViewPager1,false);
        A_GridView.setGravity(Gravity.CENTER);
        B_GridView=(GridView) inflater.inflate(R.layout.pager,mViewPager1,false);
        B_GridView.setGravity(Gravity.CENTER);
        C_GridView=(GridView) inflater.inflate(R.layout.pager,mViewPager1,false);
        C_GridView.setGravity(Gravity.CENTER);
        D_GridView=(GridView) inflater.inflate(R.layout.pager,mViewPager1,false);
        D_GridView.setGravity(Gravity.CENTER);
        /*範圍外的卡片~*/
        N_A_GridView=(GridView) inflater.inflate(R.layout.pager,mViewPager2,false);
        N_A_GridView.setGravity(Gravity.CENTER);
        N_B_GridView=(GridView) inflater.inflate(R.layout.pager,mViewPager2,false);
        N_B_GridView.setGravity(Gravity.CENTER);
        N_C_GridView=(GridView) inflater.inflate(R.layout.pager,mViewPager2,false);
        N_C_GridView.setGravity(Gravity.CENTER);
        N_D_GridView=(GridView) inflater.inflate(R.layout.pager,mViewPager2,false);
        N_D_GridView.setGravity(Gravity.CENTER);
        back=(Button) findViewById(R.id.cabinet_back);
    }

    private void processController() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /**/
        View view1=getLayoutInflater().inflate(R.layout.customtab,null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.final_capsule_select);
        View view2=getLayoutInflater().inflate(R.layout.customtab,null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.final_puzzle_select);
        View view3=getLayoutInflater().inflate(R.layout.customtab,null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.final_recieve_chat_select);
        View view4=getLayoutInflater().inflate(R.layout.customtab,null);
        view4.findViewById(R.id.icon).setBackgroundResource(R.drawable.final_chat_send_select);
        /*自訂義tab樣式*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Seeker Search");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mTabs.addTab(mTabs.newTab().setCustomView(view1));
        mTabs.addTab(mTabs.newTab().setCustomView(view2));
        mTabs.addTab(mTabs.newTab().setCustomView(view3));
        mTabs.addTab(mTabs.newTab().setCustomView(view4));
        A_GridView.setAdapter(new GridAdapter_Capsule(CardShowCabinet.this,CapsuleList,imageLoader));
        A_GridView.setNumColumns(4);
        B_GridView.setAdapter(new GridAdapter_MyCard(CardShowCabinet.this,MyCardList,imageLoader));
        B_GridView.setNumColumns(4);
        C_GridView.setAdapter(new GridAdapter_Whisper_recieve(CardShowCabinet.this,RecieveList,imageLoader));
        C_GridView.setNumColumns(4);
        D_GridView.setAdapter(new GridAdapter_Whisper_send(CardShowCabinet.this,GiveList,imageLoader));
        D_GridView.setNumColumns(4);
        mviewPagerGridList1.add(A_GridView);
        mviewPagerGridList1.add(B_GridView);
        mviewPagerGridList1.add(C_GridView);
        mviewPagerGridList1.add(D_GridView);
        mViewPager1.setAdapter(new PageAdapter(mviewPagerGridList1));

        N_A_GridView.setAdapter(new GridAdapter_Capsule(CardShowCabinet.this,N_CapsuleList,imageLoader));
        N_A_GridView.setNumColumns(5);
        N_B_GridView.setAdapter(new GridAdapter_MyCard(CardShowCabinet.this,N_MyCardList,imageLoader));
        N_B_GridView.setNumColumns(5);
        N_C_GridView.setAdapter(new GridAdapter_Whisper_recieve(CardShowCabinet.this,N_RecieveList,imageLoader));
        N_C_GridView.setNumColumns(5);
        N_D_GridView.setAdapter(new GridAdapter_Whisper_send(CardShowCabinet.this,N_GiveList,imageLoader));
        N_D_GridView.setNumColumns(5);
        mviewPagerGridList2.add(N_A_GridView);
        mviewPagerGridList2.add(N_B_GridView);
        mviewPagerGridList2.add(N_C_GridView);
        mviewPagerGridList2.add(N_D_GridView);
        mViewPager2.setAdapter(new PageAdapter(mviewPagerGridList2));

        mViewPager1.setPageTransformer(true,new CubeTransformer());
        mViewPager1.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mViewPager2.setPageTransformer(true,new CubeTransformer());
        mViewPager2.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager1));
        mTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager2));
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){
                    List<CapsuleCard> tempCapsuleCard=new ArrayList<>();
                    /*  */
                    List<MyCard> tempMyCard=new ArrayList<>();
                     /*  */
                    List<FriendsCard> tempRecieve=new ArrayList<>();
                     /*  */
                    List<FriendsCard> tempGiven=new ArrayList<>();
                    /*  */
                    for(CapsuleCard capsuleCard : CapsuleList){
                        if(capsuleCard.getTitle().contains(newText)){
                            tempCapsuleCard.add(capsuleCard);
                        }
                    }
                    for(MyCard myCard : MyCardList){
                        if(myCard.getTitle().contains(newText)){
                            tempMyCard.add(myCard);
                        }
                    }
                    for(FriendsCard friendsCard: RecieveList){
                        if(friendsCard.getTitle().contains(newText)){
                            tempRecieve.add(friendsCard);
                        }
                    }
                    for(FriendsCard friendsCard:GiveList){
                        if(friendsCard.getTitle().contains(newText)){
                            tempGiven.add(friendsCard);
                        }
                    }
                    A_GridView.removeAllViewsInLayout();
                    B_GridView.removeAllViewsInLayout();
                    C_GridView.removeAllViewsInLayout();
                    D_GridView.removeAllViewsInLayout();
                    A_GridView.setAdapter(new GridAdapter_Capsule(CardShowCabinet.this,tempCapsuleCard,imageLoader));
                    B_GridView.setAdapter(new GridAdapter_MyCard(CardShowCabinet.this,tempMyCard,imageLoader));
                    C_GridView.setAdapter(new GridAdapter_Whisper_recieve(CardShowCabinet.this,tempRecieve,imageLoader));
                    D_GridView.setAdapter(new GridAdapter_Whisper_send(CardShowCabinet.this,tempGiven,imageLoader));
                }
                else{
                    A_GridView.removeAllViewsInLayout();
                    B_GridView.removeAllViewsInLayout();
                    C_GridView.removeAllViewsInLayout();
                    D_GridView.removeAllViewsInLayout();
                    A_GridView.setAdapter(new GridAdapter_Capsule(CardShowCabinet.this,CapsuleList,imageLoader));
                    B_GridView.setAdapter(new GridAdapter_MyCard(CardShowCabinet.this,MyCardList,imageLoader));
                    C_GridView.setAdapter(new GridAdapter_Whisper_recieve(CardShowCabinet.this,RecieveList,imageLoader));
                    D_GridView.setAdapter(new GridAdapter_Whisper_send(CardShowCabinet.this,GiveList,imageLoader));
                }
                return true;
            }
        });
        mViewPager1.setOffscreenPageLimit(3);
        mViewPager2.setOffscreenPageLimit(3);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }


}
