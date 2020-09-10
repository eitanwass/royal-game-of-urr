package com.stadio.urr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Utils {
    public final static char[] hexArray = "0123456789ABCDEF".toCharArray();


    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }


    public static Bitmap parseBitmapFromBase64(String b64) {
        byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
