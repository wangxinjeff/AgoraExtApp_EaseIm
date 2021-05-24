package com.hyphenate.easeim.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.adapter.GiftGridAdapter;
import com.hyphenate.easeim.domain.Gift;
import com.hyphenate.easeim.interfaces.GiftItemListener;
import com.hyphenate.easeim.interfaces.GiftViewListener;

import java.util.ArrayList;
import java.util.List;

public class GiftView extends LinearLayout implements GiftItemListener, View.OnClickListener {

    private ImageView closeView;
    private GridView gridView;
    private TextView scopeVeiw;
    private Context context;
    private GiftViewListener giftViewListener;

    public GiftView(Context context) {
        this(context, null);
    }

    public GiftView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.gift_view, this);
        initView();
    }

    private void initView(){
        closeView = findViewById(R.id.close_gift);
        gridView = findViewById(R.id.gift_grid);
        scopeVeiw = findViewById(R.id.scope);
        String scope = String.format("%s学分", "2000");
        scopeVeiw.setText(scope);
        List<Gift> giftList = new ArrayList<>();
        giftList.add(new Gift("鲜花", "https://lanhu.oss-cn-beijing.aliyuncs.com/SketchPng00ac5efe8c1d9a682b605523806cba0a7663025682aceda8973fd30f4e9d25aa", "50","呐~这朵花花送给你"));
        giftList.add(new Gift("比心", "https://lanhu.oss-cn-beijing.aliyuncs.com/SketchPnga261018b5c2d2d1b0fb81d42ea8149f2bed327c2b06afeedcba7d0dd1bc70613", "100","老师好棒，我是你的铁粉"));
        giftList.add(new Gift("鸡腿", "https://lanhu.oss-cn-beijing.aliyuncs.com/SketchPngb457abd9d2e7f4561a59d22a51db8f6622a9fcec37ed6b8d0d1e239c73da65e6", "200","讲得好，加鸡腿"));
        giftList.add(new Gift("可乐", "https://lanhu.oss-cn-beijing.aliyuncs.com/SketchPngebbd7053ac2c14d970abc8f73d84d3c24183ef6a6872bf1f64125b43d0dbdfd1", "200","一起干了这杯82年的可乐"));
        giftList.add(new Gift("润喉糖", "https://lanhu.oss-cn-beijing.aliyuncs.com/SketchPngbb46fbcc43fbbf8e1bd4cbbbf9039c6f145a0238e0db2b5c422d94d4d51c5ffc", "200","老师辛苦了，润润喉"));
        giftList.add(new Gift("血包", "https://lanhu.oss-cn-beijing.aliyuncs.com/SketchPng8001de704f6f6b801a88bdfa4c36765c1e7b162eee0dbc57d82ecd1ae9385aec", "500","给老师回回血"));
        giftList.add(new Gift("火箭", "https://lanhu.oss-cn-beijing.aliyuncs.com/SketchPnge79dd141528e728de3f138525972396633e0d925aae12eb311f566bc8eb8ee9e", "500","神仙老师，浑身都是优点"));
        GiftGridAdapter gridAdapter = new GiftGridAdapter(context, 1,giftList);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gridAdapter.setSeclection(i);
                gridAdapter.notifyDataSetChanged();
            }
        });

        gridAdapter.setGiftViewListener(this);
        closeView.setOnClickListener(this);
    }

    @Override
    public void onGiveGift(Gift gift) {
        giftViewListener.onGiftSend(gift);
    }

    public void setGiftViewListener(GiftViewListener giftViewListener){
        this.giftViewListener = giftViewListener;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.close_gift){
            giftViewListener.onCloseGiftView();
        }
    }
}
