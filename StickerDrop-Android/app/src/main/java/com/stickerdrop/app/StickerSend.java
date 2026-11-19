package com.stickerdrop.app;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/** Shares PNG as image. Native sticker messages require using WhatsApp's imported sticker pack. */
public final class StickerSend {
    private StickerSend() {}
    public static void share(Context context, int i, String preferredPackage) {
        Uri uri = StickerAssets.shareUri(i);
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("image/png");
        send.putExtra(Intent.EXTRA_STREAM, uri);
        send.setClipData(ClipData.newUri(context.getContentResolver(), "StickerDrop sticker",uri));
        send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        String[] choices = preferredPackage != null && preferredPackage.equals("com.whatsapp.w4b")
                ? new String[]{"com.whatsapp.w4b", "com.whatsapp"}
                : new String[]{"com.whatsapp", "com.whatsapp.w4b"};
        for (String pkg : choices) {
            try {
                send.setPackage(pkg);
                context.startActivity(send);
                return;
            } catch (ActivityNotFoundException ignored) { /* Try Business or consumer app. */ }
            catch (SecurityException ignored) { /* Try other package. */ }
        }
        Toast.makeText(context, "WhatsApp not found. Install WhatsApp and try again.",Toast.LENGTH_LONG).show();
    }
}
