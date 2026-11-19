package com.stickerdrop.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends Activity {
    private int dp(int n) { return Math.round(n*getResources().getDisplayMetrics().density); }
    private TextView label(String text,int size,int color,boolean bold) {
        TextView view=new TextView(this);
        view.setText(text); view.setTextSize(size); view.setTextColor(color);
        view.setLineSpacing(dp(3),1f);
        if(bold) view.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return view;
    }
    private void addText(LinearLayout col,String t,int s,int c,boolean b,int margin) {
        TextView x=label(t,s,c,b); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,dp(margin),0,0); col.addView(x,p);
    }
    private Button button(String t,int bg) {
        Button b=new Button(this);b.setText(t);b.setAllCaps(false);
        b.setTextColor(Color.rgb(13,26,40));b.setTextSize(15);
        GradientDrawable shape=new GradientDrawable();shape.setCornerRadius(dp(16));shape.setColor(bg);
        b.setBackground(shape);
        return b;
    }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(13,21,35));
        LinearLayout col=new LinearLayout(this);col.setOrientation(LinearLayout.VERTICAL);
        col.setPadding(dp(20),dp(42),dp(20),dp(40));scroll.addView(col);
        addText(col,"STICKERDROP",35,Color.rgb(121,237,216),true,0);
        addText(col,"Add stickers to WhatsApp",18,Color.WHITE,true,28);
        addText(col,"This adds the six stickers to WhatsApp's built-in sticker picker, so they send as actual stickers when selected there.",14,Color.rgb(192,209,222),false,8);
        Button add=button("Add StickerDrop pack to WhatsApp",Color.rgb(255,210,102));
        LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(54));ap.topMargin=dp(14);col.addView(add,ap);
        add.setOnClickListener(v-> {
            Intent i=new Intent("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
            i.putExtra("sticker_pack_id",StickerAssets.PACK_ID);
            i.putExtra("sticker_pack_authority",StickerAssets.PACK_AUTHORITY);
            i.putExtra("sticker_pack_name",StickerAssets.PACK_NAME);
            try {startActivity(i);} catch(ActivityNotFoundException e) {
                android.widget.Toast.makeText(this,"WhatsApp not installed, or sticker import unavailable.",android.widget.Toast.LENGTH_LONG).show();
            }
        });
        addText(col,"Preview and share",18,Color.WHITE,true,28);
        addText(col,"Tap an image below to share it as a PNG. Actual sticker messages use the imported pack above.",14,Color.rgb(192,209,222),false,8);
        HorizontalScrollView previews=new HorizontalScrollView(this);previews.setHorizontalScrollBarEnabled(false);
        LinearLayout pics=new LinearLayout(this);pics.setOrientation(LinearLayout.HORIZONTAL);
        for(int k=0;k<StickerAssets.WORDS.length;k++) {
            final int id=k;
            ImageView v=new ImageView(this);
            try { Bitmap img=StickerAssets.loadPreview(this,k);v.setImageBitmap(img); }
            catch(IOException ignored){}
            v.setScaleType(ImageView.ScaleType.FIT_CENTER);
            v.setContentDescription("Share "+StickerAssets.WORDS[k]);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(dp(110),dp(110));
            params.setMargins(0,dp(16),dp(7),dp(10));pics.addView(v,params);
            v.setOnClickListener(view -> StickerSend.share(this,id,"com.whatsapp"));
        }
        previews.addView(pics); col.addView(previews);
        addText(col,"Privacy: no internet permission, no clipboard access, and no saved message text. StickerDrop is a prototype and is not affiliated with WhatsApp.",13,Color.rgb(146,165,181),false,22);
        setContentView(scroll);
    }
}
