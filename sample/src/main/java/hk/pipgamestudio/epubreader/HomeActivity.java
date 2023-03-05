/*
 * Copyright (C) 2016 Pedro Paulo de Amorim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hk.pipgamestudio.epubreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.ui.activity.FolioActivity;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.AppUtil;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadLocatorListener;
import com.folioreader.util.SharedPreferenceUtil;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements OnHighlightListener, ReadLocatorListener, FolioReader.OnClosedListener {
    
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();
    private static final int PICKFILE_RESULT_CODE = 1;
    private FolioReader folioReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this, FolioActivity.ADMOB_APP_ID);

        folioReader = FolioReader.get()
                .setOnHighlightListener(this)
                .setReadLocatorListener(this)
                .setOnClosedListener(this);

        getHighlightsAndSave();

        final String lastReadBook = SharedPreferenceUtil.getSharedPreferencesString(getApplicationContext(), Constants.LAST_READ_BOOK, "");
        if (null == lastReadBook || "".equals(lastReadBook)) {
            findViewById(R.id.btn_assest).setEnabled(false);
        }

        findViewById(R.id.btn_raw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*                Config config = AppUtil.getSavedConfig(getApplicationContext());
                if (config == null)
                    config = new Config();
                config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

                String file = Environment.getExternalStorageDirectory() + "/Download/TheSilverChair.epub";

                folioReader.setConfig(config, true).openBook(file);
                        //.openBook(R.raw.accessible_epub_3);*/

                showFileChooser();
            }
        });

        findViewById(R.id.btn_assest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(lastReadBook);
                if (null != file && file.exists()) {
                    ReadLocator readLocator = getLastReadLocator();

                    Config config = AppUtil.getSavedConfig(getApplicationContext());
                    if (config == null)
                        config = new Config();
                    config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

                    folioReader.setReadLocator(readLocator);
                    folioReader.setConfig(config, true)
                            .openBook(lastReadBook);
                } else {
                    Toast.makeText(getApplicationContext(), "之前的電子書讀取失敗，請再選擇電子書。", Toast.LENGTH_LONG).show();
                    findViewById(R.id.btn_assest).setEnabled(false);
                }

            }
        });
    }

    private ReadLocator getLastReadLocator() {

        //String jsonString = loadAssetTextAsString("Locators/LastReadLocators/last_read_locator_1.json");
        String jsonString = SharedPreferenceUtil.getSharedPreferencesString(getApplicationContext(), Constants.LAST_READ_POSITION, "");
        return ReadLocator.fromJson(jsonString);
    }

    @Override
    public void saveReadLocator(ReadLocator readLocator) {
        Log.d(LOG_TAG, "-> saveReadLocator -> " + readLocator.toJson());
        SharedPreferenceUtil.putSharedPreferencesString(getApplicationContext(), Constants.LAST_READ_POSITION, readLocator.toJson());
    }

    /*
     * For testing purpose, we are getting dummy highlights from asset. But you can get highlights from your server
     * On success, you can save highlights to FolioReader DB.
     */
    private void getHighlightsAndSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HighLight> highlightList = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    highlightList = objectMapper.readValue(
                            loadAssetTextAsString("highlights/highlights_data.json"),
                            new TypeReference<List<HighlightData>>() {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (highlightList == null) {
                    folioReader.saveReceivedHighLights(highlightList, new OnSaveHighlight() {
                        @Override
                        public void onFinished() {
                            //You can do anything on successful saving highlight list
                        }
                    });
                }
            }
        }).start();
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("HomeActivity", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("HomeActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FolioReader.clear();
    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {
        Toast.makeText(this,
                "highlight id = " + highlight.getUUID() + " type = " + type,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFolioReaderClosed() {
        Log.v(LOG_TAG, "-> onFolioReaderClosed");
    }

    private void showFileChooser() {
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try{
            this.startActivityForResult(Intent.createChooser(intent, "請選擇電子書 epub 文件"), PICKFILE_RESULT_CODE);
        } catch(ActivityNotFoundException ane) {
            Toast.makeText(getApplicationContext(), "你竟然没有裝個文件管理器，程序彻底没法用了！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKFILE_RESULT_CODE
                && resultCode == Activity.RESULT_OK) {

            if (data != null) {
                final Uri uri = data.getData();

                //File outFolderDefault = new File(getApplicationContext().getExternalFilesDir(null).getPath());
                File outFolderDefault = new File(Environment.getExternalStorageDirectory() + "/Books");
                if (!outFolderDefault.isDirectory()) outFolderDefault.mkdirs();

                String fileName = new File(uri.getPath()).getName();
                //String root = Environment.getExternalStorageDirectory() + "/Download/TheSilverChair.epub";
                if (fileName.contains(".epub")) {
                    final String file = outFolderDefault + "/" + fileName;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                    alertDialogBuilder.setTitle("確定?");
                    alertDialogBuilder
                            .setMessage("選擇此 EPUB 電子書?")
                            .setCancelable(false)
                            .setNegativeButton(R.string.Cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            //showFileChooser();
                                        }
                                    })
                            .setPositiveButton(R.string.OK,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            copyFile(uri, file);

                                            Config config = AppUtil.getSavedConfig(getApplicationContext());
                                            if (config == null)
                                                config = new Config();
                                            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

                                            ReadLocator readLocator = getLastReadLocator();
                                            folioReader.setReadLocator(readLocator);
                                            folioReader.setConfig(config, true).openBook(file);
                                            SharedPreferenceUtil.putSharedPreferencesString(getApplicationContext(), Constants.LAST_READ_BOOK, file);

                                            dialog.cancel();
                                        }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "格式出錯或電子書內容出錯，請再選擇。", Toast.LENGTH_LONG).show();
                }

                //Log.d(LOG_TAG, "------ path=" + uri.getPath();
            }
        }
    }

    private void copyFile(Uri uri, String file) {
        try {
            File outFile = new File(file);
            if (!outFile.exists()) {
                InputStream in = getContentResolver().openInputStream(uri);
                FileOutputStream out = new FileOutputStream(outFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1)
                {
                    out.write(buffer, 0, read);
                }

                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            }
        } catch (IOException ioe) {
            Log.e(LOG_TAG, ioe.toString());
        }
    }
}