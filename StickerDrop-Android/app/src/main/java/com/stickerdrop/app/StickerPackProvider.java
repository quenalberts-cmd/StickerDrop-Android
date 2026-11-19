package com.stickerdrop.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/** Implements the four interfaces in WhatsApp's official third-party sticker specification. */
public final class StickerPackProvider extends ContentProvider {
    private static final int METADATA=1, PACK=2, STICKERS=3, ASSET=4;
    private final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    @Override public boolean onCreate() {
        String a = StickerAssets.PACK_AUTHORITY;
        matcher.addURI(a, "metadata", METADATA);
        matcher.addURI(a, "metadata/*", PACK);
        matcher.addURI(a, "stickers/*", STICKERS);
        matcher.addURI(a, "stickers_asset/*/*", ASSET);
        return true;
    }
    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String order) {
        int m = matcher.match(uri);
        if (m == METADATA || (m == PACK && StickerAssets.PACK_ID.equals(uri.getLastPathSegment()))) {
            // Column names and order match WhatsApp's StickerContentProvider contract.
            MatrixCursor c = new MatrixCursor(new String[]{
                "sticker_pack_identifier", "sticker_pack_name", "sticker_pack_publisher",
                "sticker_pack_icon", "android_play_store_link", "ios_app_download_link",
                "sticker_pack_publisher_email", "sticker_pack_publisher_website",
                "sticker_pack_privacy_policy_website", "sticker_pack_license_agreement_website",
                "image_data_version", "whatsapp_will_not_cache_stickers", "animated_sticker_pack"});
            c.addRow(new Object[]{StickerAssets.PACK_ID, StickerAssets.PACK_NAME,
                "StickerDrop", "tray.png", "", "", "", "", "", "", "1", 0, 0});
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }
        if (m == STICKERS && StickerAssets.PACK_ID.equals(uri.getLastPathSegment())) {
            MatrixCursor c = new MatrixCursor(new String[]{
                "sticker_file_name", "sticker_emoji", "sticker_accessibility_text"});
            for (int i=0;i<StickerAssets.WORDS.length;i++) {
                c.addRow(new Object[]{StickerAssets.webpName(i), StickerAssets.EMOJIS[i], StickerAssets.LABELS[i]});
            }
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }
        return null;
    }
    private String asset(Uri uri) throws FileNotFoundException {
        if (matcher.match(uri) != ASSET) throw new FileNotFoundException("Unknown path");
        List<String> path=uri.getPathSegments();
        if (!StickerAssets.PACK_ID.equals(path.get(1))) throw new FileNotFoundException("Invalid pack");
        String name=path.get(2);
        if ("tray.png".equals(name)) return "pack/tray.png";
        for(int i=0;i<StickerAssets.WORDS.length;i++) {
            if (StickerAssets.webpName(i).equals(name)) return "pack/"+name;
        }
        throw new FileNotFoundException("Unknown sticker");
    }
    @Override public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        if (!"r".equals(mode)) throw new FileNotFoundException("Read only");
        try {
            File f = StickerAssets.copyAsset(getContext(),asset(uri));
            return new AssetFileDescriptor(ParcelFileDescriptor.open(f,ParcelFileDescriptor.MODE_READ_ONLY),
                    0, AssetFileDescriptor.UNKNOWN_LENGTH);
        } catch(IOException e) { throw new FileNotFoundException(e.getMessage()); }
    }
    @Override public String getType(Uri uri) {
        if (matcher.match(uri)==ASSET) return uri.getLastPathSegment().endsWith(".png")?"image/png":"image/webp";
        return "vnd.android.cursor.dir/vnd.stickerpack";
    }
    @Override public Uri insert(Uri u, ContentValues v) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri u,String s,String[] a) { return 0; }
    @Override public int update(Uri u,ContentValues v,String s,String[] a) { return 0; }
}
