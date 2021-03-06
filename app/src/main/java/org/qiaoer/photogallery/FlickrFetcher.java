package org.qiaoer.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class FlickrFetcher {
    public static final String TAG = "FlickrFetcher";

    public static final String PREF_SEARCH_QUERY = "searchQuery";
    public static final String PREF_LAST_RESULT_ID = "lastResultId";

    private static final String ENDPOINT = "https://pixabay.com/api/";
    private static final String KEY = "2701371-6dd40f7d949e87e4267ed4e1b";
    //    private static final String QUERY_WORDS = "yellow+flowers";
    private static final String EDITORS_CHOICE = "true";
    private static final String IMAGE_TYPE = "photo";
    private static final String PER_PAGE = "200";
    private static final String PRETTY = "true";

    public ArrayList<GalleryItem> downloadGalleryItems(String url) {
        ArrayList<GalleryItem> items = new ArrayList<>();

        try {
            String jsonString = getUrl(url);
            Log.i(TAG, "Received xml: " + jsonString);

            parseItems(jsonString, items);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }
        return items;

    }


    public ArrayList<GalleryItem> fetchItems() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("key", KEY)
                .appendQueryParameter("editors_choice", EDITORS_CHOICE)
                .appendQueryParameter("image_type", IMAGE_TYPE)
                .appendQueryParameter("per_page", PER_PAGE)
                .appendQueryParameter("pretty", PRETTY)
                .build().toString();
        return downloadGalleryItems(url);
    }

    public ArrayList<GalleryItem> search(String query) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("key", KEY)
                .appendQueryParameter("q", query)
                .appendQueryParameter("image_type", IMAGE_TYPE)
                .appendQueryParameter("per_page", PER_PAGE)
                .appendQueryParameter("pretty", PRETTY)
                .build().toString();
        return downloadGalleryItems(url);
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoInput(true);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int hasRead;
            byte[] buffer = new byte[1024];
            while ((hasRead = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, hasRead);
            }

            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }


    private void parseItems(String jsonString, ArrayList<GalleryItem> items) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("hits");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pictureItem = jsonArray.getJSONObject(i);
                String caption = pictureItem.getString("tags");
                String url = pictureItem.getString("previewURL");
                String id = pictureItem.getString("id");

                GalleryItem item = new GalleryItem();
                item.setId(id);
                item.setUrl(url);
                item.setCaption(caption);

                items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*byte[] getUrlBytes(String urlSpec) throws IOException {
        ByteArrayOutputStream out = null;
        InputStream inputStream = null;
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            out = new ByteArrayOutputStream();
            inputStream = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int hasRead;
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

    public ArrayList<GalleryItem> fetchItems() {
        ArrayList<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_GET_RECENT)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                    .build().toString();

            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));

            parseItems(items, parser);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to parse items", e);
        }
        return items;
    }

    void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG
                    && XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);

                GalleryItem item = new GalleryItem();
                item.setId(id);
                item.setCaption(caption);
                item.setUrl(smallUrl);
                items.add(item);
            }

            eventType = parser.next();
        }
    }*/

}
