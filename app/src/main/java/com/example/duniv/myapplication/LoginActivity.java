package com.example.duniv.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by duniv on 2016-11-25.
 */

public class LoginActivity extends AppCompatActivity {
    String jsonStr;
    TextView textView;
    EditText id, pw;
    boolean ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText)this.findViewById(R.id.id_editText);
        pw = (EditText)this.findViewById(R.id.pw_editText);
        textView = (TextView) this.findViewById(R.id.login_ok);
        Button login = (Button) this.findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        try {
                            URL url = new URL("http://192.168.2.6:8082/JavaWeb/mobile/getInfoByName.jsp");

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            Log.i("쓰레드", "접속시도");
                            if (conn != null) {
                                Log.i("쓰레드", "접속성공");
                                conn.setUseCaches(false);
                                conn.setConnectTimeout(10000);
                                conn.setRequestMethod("POST");
                                conn.setDoInput(true);  // 서버로부터 입력을 받도록 설정
                                conn.setDoOutput(true);  // 서버로 출력할 수 있게 설정, 자동으로 POST 방식이 됨
                                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                                OutputStream out = conn.getOutputStream();
                                String str = "id=" + id.getText() + "&pw=" + pw.getText();
                                out.write(str.getBytes("utf-8"));
                                out.flush();

                                int resCode = conn.getResponseCode();
                                Log.i("응답코드", ""+resCode);
                                if (resCode == HttpURLConnection.HTTP_OK) {
                                    Log.i("쓰레드", "응답수신");
                                    InputStream is = conn.getInputStream();
                                    InputStreamReader isr = new InputStreamReader(is);
                                    BufferedReader br = new BufferedReader(isr);
                                    StringBuilder strBuilder = new StringBuilder();
                                    String line = null;
                                    while ((line = br.readLine()) != null) {
                                        strBuilder.append(line + "\n");
                                    }

                                    jsonStr = strBuilder.toString();
                                    br.close();
                                    conn.disconnect();
                                }
                                Log.i("쓰레드", "응답처리완료");
                            }
                        }catch (Exception ex){
                            Log.e("접속요류", ""+ex);
                        }
                    }
                }.start();

                while(jsonStr==null) {
                    try {
                        Thread.sleep(500);
                    }catch (InterruptedException ie){
                    }
                }
                // UI 쓰레드에게 실행할 코드를 전달한다
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.valueOf(ok));
                    }
                });
            }
        });
    }

    private void processJSON(String jsonStr) {
        try {
            JSONObject jsObj = new JSONObject(jsonStr);
            boolean ok = jsObj.getBoolean("ok");
            this.ok = ok;
        }catch(Exception ex){
            Log.e("JSON", ""+ex);
        }
    }
}