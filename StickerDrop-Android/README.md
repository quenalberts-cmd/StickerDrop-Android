# StickerDrop for Android

StickerDrop includes six stickers. Open the app and select **Add StickerDrop pack to
WhatsApp**, then confirm in WhatsApp. Use WhatsApp's sticker picker to send them as
stickers. Tap a preview in StickerDrop to share the same artwork as a PNG.

The app uses WhatsApp's sticker pack provider and Android's temporary read grants
for image sharing. It declares no Android runtime permissions.

## Build and test

Open this folder in Android Studio with JDK 17, Gradle 8.9 and Android SDK 35,
or run `gradle :app:assembleDebug`. GitHub Actions uploads the debug APK.

On an Android device with WhatsApp installed, confirm that all six stickers appear
in the imported pack and that tapping a preview opens the image-sharing flow.
