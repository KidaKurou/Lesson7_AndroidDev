package com.mirea.lutchenkoam.lesson7;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private final String host = "time.nist.gov"; // или time-a.nist.gov
    private final int port = 13;
    TextView twTime, twDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        twDate = findViewById(R.id.twDate);
        twTime = findViewById(R.id.twTime);
        Button button = findViewById(R.id.btnFill);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTimeTask timeTask = new GetTimeTask();
                timeTask.execute();
            }
        });
    }

    private class GetTimeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String timeResult = "";
            try {
                Socket socket = new Socket(host, port);
                BufferedReader reader = SocketUtils.getReader(socket);
                reader.readLine(); // игнорируем первую строку
                timeResult = reader.readLine(); // считываем вторую строку
                Log.d(TAG,timeResult);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return timeResult;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String time = parseTime(result);
            String date = parseDate(result);
            twDate.setText(date);
            twTime.setText(time);
        }
        public String parseTime(String timeString) {
            // Разбиваем строку по пробелам
            String[] parts = timeString.split("\\s+");
            if (parts.length >= 3) {
                // Время находится в третьей части
                return parts[2];
            } else {
                return "Invalid time format";
            }
        }

        public String parseDate(String timeString) {
            // Разбиваем строку по пробелам
            String[] parts = timeString.split("\\s+");
            if (parts.length >= 2) {
                // Дата находится во второй части
                return parts[1];
            } else {
                return "Invalid date format";
            }
        }
    }
}