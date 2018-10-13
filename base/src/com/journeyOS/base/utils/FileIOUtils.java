/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.journeyOS.base.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileIOUtils {
    private static final String TAG = FileIOUtils.class.getSimpleName();
    private static final String LINE_SEP = System.getProperty("line.separator");

    private static String mAppName;

    public static void init(String appName) {
        mAppName = appName;
    }

    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    /**
     * SDCard root
     */
    public static String sdcardRoot() {
        return Environment.getExternalStorageDirectory().toString();
    }

    public static String getAppFolder() {
        // Create the application workspace
        File cacheDir = new File(sdcardRoot() + File.separator + mAppName + File.separator);
        if (!cacheDir.exists()) {
            makeDir(cacheDir);
        }
        return cacheDir.getPath();
    }

    public static boolean makeDir(File dir) {
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return (dir.exists() && dir.isDirectory());
    }

    /**
     * Build a file, used to be inserted in the disk cache.
     *
     * @param fileId The name build the file.
     * @return A valid file.
     */
    public static File buildFile(String fileId) {
        File file = new File(getAppFolder(), fileId);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String readFile2String(final File file) {
        return readFile2String(file, null);
    }

    public static String readFile2String(final File file, final String charsetName) {
        if (!isFileExists(file)) return null;
        BufferedReader reader = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (BaseUtils.isNull(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            String line;
            if ((line = reader.readLine()) != null) {
                sb.append(line);
                while ((line = reader.readLine()) != null) {
                    sb.append(LINE_SEP).append(line);
                }
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeIO(reader);
        }
    }

    public static String readFileFromAsset(Context context, String fileName) {
        if (context == null || fileName == null) {
            LogUtils.w(TAG, "Context or file name can't be NULL");
        }

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            String result = readFileFromInputStream(inputStream);
            inputStream.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFileFromInputStream(InputStream inputStream) {
        if (inputStream == null) {
            LogUtils.w(TAG, "InputStream can't be NULL");
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder result = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException exception) {
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e2) {
            }
            try {
                inputStreamReader.close();
            } catch (IOException e2) {
            }
        }
        return null;
    }

    public static void closeIO(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
