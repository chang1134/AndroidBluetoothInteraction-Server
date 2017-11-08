package com.biggerchang.bluetoothserverv2.bluetooth.utils;


import com.biggerchang.bluetoothserverv2.bluetooth.utils.encoder.BASE64Decoder;
import com.biggerchang.bluetoothserverv2.bluetooth.utils.encoder.BASE64Encoder;

import java.io.IOException;

public class Base64Utils {
    /**
     * 编码
     *
     * @param bstr
     * @return String
     */
    public static String encode(byte[] bstr) {
        return new BASE64Encoder().encode(bstr);
    }

    /**
     * 解码
     */
    public static byte[] decode(String str) {
        byte[] bt = null;
        try {
            bt = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bt;
    }
}
