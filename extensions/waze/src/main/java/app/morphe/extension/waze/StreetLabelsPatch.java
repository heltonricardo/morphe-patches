package app.morphe.extension.waze;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class StreetLabelsPatch {
    private static final String TAG = "StreetLabels";
    private static final String[] POSITIONS = {"top_left", "top_center", "top_right", "bottom_left", "bottom_right"};
    private static final String[] KINDS = {"bg", "shadow"};
    private static final String[] DENSITIES = {"", "@2x", "@3x"};
    private static final String SENTINEL = "skin_structure.main.lua";
    private static final long TIMEOUT_MS = 180000L;
    private static final long POLL_MS = 500L;
    private static final long SETTLE_MS = 2000L;

    private StreetLabelsPatch() {
    }

    public static void init(Application app) {
        try {
            final File dir = new File(app.getApplicationInfo().dataDir, "waze/skins/default");
            if (ready(dir)) {
                apply(dir);
                return;
            }
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    awaitCopy(dir);
                }
            }, "street-labels");
            t.setDaemon(true);
            t.start();
        } catch (Throwable t) {
            Log.w(TAG, "init", t);
        }
    }

    private static void awaitCopy(File dir) {
        try {
            long end = System.currentTimeMillis() + TIMEOUT_MS;
            while (System.currentTimeMillis() < end) {
                if (ready(dir)) {
                    Thread.sleep(SETTLE_MS);
                    apply(dir);
                    return;
                }
                Thread.sleep(POLL_MS);
            }
        } catch (Throwable t) {
            Log.w(TAG, "await", t);
        }
    }

    private static boolean ready(File dir) {
        if (!new File(dir, SENTINEL).isFile()) return false;
        for (String name : names()) {
            if (!new File(dir, name).isFile()) return false;
        }
        return true;
    }

    private static String[] names() {
        String[] out = new String[POSITIONS.length * KINDS.length * DENSITIES.length];
        int i = 0;
        for (String p : POSITIONS) {
            for (String k : KINDS) {
                for (String d : DENSITIES) {
                    out[i++] = "street_label_" + p + "_" + k + d + ".png";
                }
            }
        }
        return out;
    }

    private static synchronized void apply(File dir) {
        Map<String, byte[]> cache = new HashMap<>();
        int written = 0;
        for (String name : names()) {
            try {
                File file = new File(dir, name);
                if (!file.isFile()) continue;
                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), bounds);
                int w = bounds.outWidth;
                int h = bounds.outHeight;
                if (w <= 0 || h <= 0) continue;
                String key = w + "x" + h;
                byte[] blank = cache.get(key);
                if (blank == null) {
                    blank = transparentPng(w, h);
                    cache.put(key, blank);
                }
                if (Arrays.equals(blank, readAll(file))) continue;
                File tmp = new File(dir, name + ".tmp");
                try (FileOutputStream out = new FileOutputStream(tmp)) {
                    out.write(blank);
                }
                if (tmp.renameTo(file)) {
                    written++;
                } else {
                    tmp.delete();
                }
            } catch (Throwable t) {
                Log.w(TAG, name, t);
            }
        }
        Log.i(TAG, "street label files written: " + written);
    }

    private static byte[] transparentPng(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        bitmap.recycle();
        return out.toByteArray();
    }

    private static byte[] readAll(File file) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        try (FileInputStream in = new FileInputStream(file)) {
            int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        }
        return out.toByteArray();
    }
}
