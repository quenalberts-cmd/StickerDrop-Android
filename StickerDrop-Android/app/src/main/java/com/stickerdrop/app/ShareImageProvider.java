package com.stickerdrop.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ShareImageProvider extends ContentProvider {
    @Override public boolean onCreate() { return true; }
    private int index(Uri uri) throws FileNotFoundException {
        List<String> s = uri.getPathSegments();
        if (s.size() != 2 || !s.get(0).equals("image")) throw new FileNotFoundException("Unknown URI");
        try {
            int i = Integer.parseInt(s.get(1)) - 1;
            if (!StickerAssets.valid(i)) throw new FileNotFoundException("Unknown sticker");
            return i;
        } catch (NumberFormatException e) { throw new FileNotFoundException("Invalid sticker"); }
    }
    @Override public String getType(Uri uri) { return "image/png"; }
    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!"r".equals(mode)) throw new FileNotFoundException("Read only");
        int i = index(uri);
        try {
            File f = StickerAssets.copyAsset(getContext(), "share/" + StickerAssets.pngName(i));
            return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (IOException e) { throw new FileNotFoundException(e.getMessage()); }
    }
    @Override public Cursor query(Uri uri, String[] projection, String sel, String[] args, String order) {
        try {
            int i = index(uri);
            String[] cols = projection == null ? new String[] {OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE} : projection;
            MatrixCursor cursor = new MatrixCursor(cols);
            File f = StickerAssets.copyAsset(getContext(), "share/" + StickerAssets.pngName(i));
            Object[] row = new Object[cols.length];
            for (int j=0;j<cols.length;j++) {
                if (OpenableColumns.DISPLAY_NAME.equals(cols[j])) row[j]=StickerAssets.pngName(i);
                else if (OpenableColumns.SIZE.equals(cols[j])) row[j]=f.length();
            }
            cursor.addRow(row);
            return cursor;
        } catch (IOException e) { return null; }
    }
    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri uri, String sel, String[] args) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String sel, String[] args) { return 0; }
}
