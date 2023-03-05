package com.folioreader.util;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.edu.ynu.sei.dict.plugin.stardict.StarDictReader;
import cn.edu.ynu.sei.dict.service.exception.NotFoundWordException;

public class Dict {
    private static Dict single_instance = null;
    private static StarDictReader sdr = null;

    private static final String STARDICT_FILENAME = "dict";

    private Dict(Context context)
    {
        OutputStream os = null;
        InputStream is = null;

        try {
            String path = context.getFilesDir().getAbsolutePath();
            int length = 0;

            // check is the dz file is exist
            File dz = new File(path, STARDICT_FILENAME + ".dict.dz");
            if (!dz.exists()) {
                String fileStr = path + File.separator + STARDICT_FILENAME + ".dict.dz";
                os = new FileOutputStream(fileStr);

                byte[] buffer = new byte[1024];
                is = context.getAssets().open(STARDICT_FILENAME + ".dict.dz");
                if (is != null) {
                    while ( (length = is.read(buffer)) > 0 ) {
                        os.write(buffer);
                    }

                    is.close();
                    os.flush();
                    os.close();
                }
            }

            // check is the idx file is exist
            File idx = new File(path, STARDICT_FILENAME + ".idx");
            if (!idx.exists()) {
                String fileStr = path + File.separator + STARDICT_FILENAME + ".idx";
                os = new FileOutputStream(fileStr);

                byte[] buffer = new byte[1024];
                length = 0;
                is = context.getAssets().open(STARDICT_FILENAME + ".idx");
                if (is != null) {
                    while ( (length = is.read(buffer)) > 0 ) {
                        os.write(buffer);
                    }

                    is.close();
                    os.flush();
                    os.close();
                }
            }

            // check is the ifo file is exist
            File ifo = new File(path, STARDICT_FILENAME + ".ifo");
            if (!ifo.exists()) {
                String fileStr = path + File.separator + STARDICT_FILENAME + ".ifo";
                os = new FileOutputStream(fileStr);

                byte[] buffer = new byte[1024];
                length = 0;
                is = context.getAssets().open(STARDICT_FILENAME + ".ifo");
                if (is != null) {
                    while ( (length = is.read(buffer)) > 0 ) {
                        os.write(buffer);
                    }

                    is.close();
                    os.flush();
                    os.close();
                }
            }

            sdr = new StarDictReader(path, STARDICT_FILENAME);
        } catch (FileNotFoundException ffe) {
            sdr = null;
        } catch (IOException ioe) {
        } finally {
            try {
                if (is != null) is.close();
                if (os != null) os.close();
            } catch (IOException ioe) {}
        }
    }

    // static method to create instance of Singleton class
    public static Dict getInstance(Context context)
    {
        if (single_instance == null) single_instance = new Dict(context);

        return single_instance;
    }

    public String lookup(String word) {
        String s= word.toLowerCase();
        String exp = "找不到翻譯...";

        if (sdr != null) {
            try {
                exp = sdr.lookup(s).definition;
            } catch (NotFoundWordException e) {}

            // cut the end if 'ing', 'ed' or 's'
            if (exp.equals("找不到翻譯...")) {
                try {
                    if (s.endsWith("s")) {
                        exp = sdr.lookup(s.substring(0, s.length()-1)).definition;
                    }else if (s.endsWith("ed")) {
                        exp = sdr.lookup(s.substring(0, s.length()-2)).definition;
                        if (exp.equals("找不到翻譯...")) {
                            exp = sdr.lookup(s.substring(0, s.length()-1)).definition;
                        }
                    } else if (s.endsWith("ing")) {
                        exp = sdr.lookup(s.substring(0, s.length()-3)).definition;
                    }
                } catch (NotFoundWordException e) {}
            }
        }

        return exp;
    }
}