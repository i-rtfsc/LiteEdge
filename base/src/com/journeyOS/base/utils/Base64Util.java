package com.journeyOS.base.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class Base64Util {
    /**
     * @param message the message to be encoded
     * @return the enooded from of the message
     */
    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            String base64Sms = Base64.encodeToString(data, Base64.DEFAULT);
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param message the encoded message
     * @return the decoded message
     */
    public static String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
