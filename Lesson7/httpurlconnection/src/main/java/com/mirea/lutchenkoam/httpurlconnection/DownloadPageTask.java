package com.mirea.lutchenkoam.httpurlconnection;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.mirea.lutchenkoam.httpurlconnection.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadPageTask extends AsyncTask<String, Void, String> {
    private final ActivityMainBinding binding;

    public DownloadPageTask(ActivityMainBinding binding) {
        this.binding = binding;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadIpInfo(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    @Override
    protected void onPostExecute(String result) {
        Log.d(MainActivity.class.getSimpleName(), result);
        try {
            JSONObject responseJson = new JSONObject(result);

            String city = responseJson.getString("city");
            TextView textViewCity = binding.city;
            textViewCity.setText("Город: " + city);

            String region = responseJson.getString("region");
            TextView textViewRegion = binding.region;
            textViewRegion.setText("Регион: " + region);

            String country = responseJson.getString("country");
            TextView textViewCountry = binding.country;
            textViewCountry.setText("Страна: " + country);

            String loc = responseJson.getString("loc");
            String[] parts = loc.split(",");
            String latitude = parts[0];
            String longitude = parts[1];

            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";

            WebView webView = binding.webView;
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webView.loadUrl(weatherUrl);


            Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
            String ip = responseJson.getString("ip");
            Log.d(MainActivity.class.getSimpleName(), "IP: " + ip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }
    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read); }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage()+". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}
