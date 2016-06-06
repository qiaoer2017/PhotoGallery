package org.qiaoer.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FlickerFetcher {
    byte[] getUrlBytes(String urlSpec) throws IOException {
        HttpURLConnection connection = null;
        ByteArrayOutputStream out = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();

            out = new ByteArrayOutputStream();
            inputStream = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int hasRead = 0;
            byte[] buffer = new byte[1024];
            while ((hasRead = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, hasRead);
            }

            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
            try {
                if (out != null) {
                    out.close();
                }

                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
