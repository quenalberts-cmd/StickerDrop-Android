package com.stickerdrop.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class StickerAssets {
    public static final String PACK_ID = "pop_1";
    public static final String PACK_NAME = "StickerDrop Originals";
    public static final String PACK_AUTHORITY = "com.stickerdrop.app.stickercontentprovider";
    public static final String SHARE_AUTHORITY = "com.stickerdrop.app.share";
    public static final String[] WORDS = {"YES!", "NOPE", "LOL", "OMG", "LOVE", "MONEY"};
    public static final String[] EMOJIS = {"✨", "😒", "😂", "😱", "❤️", "💸"};
    public static final String[] LABELS = {"A bright comic sticker saying Yes!",
            "A bold sticker saying Nope", "A comic sticker saying LOL",
            "A comic sticker saying OMG", "A heart themed sticker saying Love",
            "A playful money themed sticker saying MONEY"};
    private StickerAssets() {}

    public static boolean valid(int i) { return i >= 0 && i < WORDS.length; }
    public static String webpName(int i) { return "sticker" + (i+1) + ".webp"; }
    public static String pngName(int i) { return "sticker" + (i+1) + ".png"; }
    public static Uri shareUri(int i) {
        return new Uri.Builder().scheme("content").authority(SHARE_AUTHORITY)
                .appendPath("image").appendPath(Integer.toString(i+1)).build();
    }
    public static Bitmap loadPreview(Context context, int i) throws IOException {
        try (InputStream input = context.getAssets().open("share/" + pngName(i))) {
            return BitmapFactory.decodeStream(input);
        }
    }
    public static File copyAsset(Context context, String assetPath) throws IOException {
        // Only fixed, app-bundled assets are ever copied here; reject arbitrary paths.
        if (!assetPath.matches("(pack/(tray\\.png|sticker[1-6]\\.webp)|share/sticker[1-6]\\.png)")) {
            throw new IOException("Unknown asset");
        }
        File directory = new File(context.getCacheDir(), "sticker-assets");
        if (!directory.exists() && !directory.mkdirs()) throw new IOException("Cache unavailable");
        File dest = new File(directory, assetPath.replace('/', '_'));
        if (!dest.exists()) {
            try (InputStream in = context.getAssets().open(assetPath);
                 FileOutputStream out = new FileOutputStream(dest)) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            }
        }
        return dest;
    }
}
