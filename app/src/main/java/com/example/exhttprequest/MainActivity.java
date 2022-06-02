package com.example.exhttprequest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/*
 참고 출처 : https://jeongminhee99.tistory.com/75
 입력 : https://jsonplaceholder.typicode.com/users
*/

public class MainActivity extends AppCompatActivity {

    // https://noembed.com/embed?url=https://www.youtube.com/watch?v=qKkp-47stm0

    EditText et;
    Button btn;
    TextView tv, tvYoutubeName, tvYoutubeTitle;

    String youtubeURL="https://noembed.com/embed?url=https://www.youtube.com/watch?v=";
    String jsonStr="";
    String youtubeName, youtubeTitle;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText)findViewById(R.id.et1);
        btn = (Button)findViewById(R.id.btn1);
        tv = (TextView) findViewById(R.id.tv1);
        tvYoutubeName = (TextView) findViewById(R.id.youtubeName);
        tvYoutubeTitle = (TextView) findViewById(R.id.youtubeTitle);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String youtubeID = et.getText().toString();
                final String url = youtubeURL+youtubeID;


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        request(url);
                    }
                }).start();
            }
        });

    }

    public void request(String urlStr) {
        StringBuilder output = new StringBuilder();
        /*
        String + String = 메모리 할당, 해제를 발생시키면서 성능 저하.
        => StringBuilder 활용. StringBuilder에 append 하면서 문자열 추가.
        인스턴스를 toString()한 것을 출력.
         */

        try{
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn!=null){
                conn.setConnectTimeout(10000); // 연결 대기 시간
                conn.setRequestMethod("GET");
                conn.setDoInput(true);  // 객체의 입력이 가능하도록

                /*
                  응답코드가 HTTP_OK 인 경우 : 정상적인 응답이 온 것이므로 응답으로 들어온 스트림을 문자열로 반환하여 출력
                  요청한 주소의 페이지가 없는 경우 : HTTP_NOT_FOUND 코드가 반환.
                 */

                int resCode = conn.getResponseCode();

                // 입력 데이터를 받기 위한 Reader 객체 생성
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

                while (true) {
                    line = reader.readLine();
                    if (line == null){
                        break;
                    }
                    output.append(line + "\n");
                }

                reader.close();
                conn.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            println("예외 발생 :" + e.toString());
        }

        //println("응답 -> " + output.toString());
        jsonStr = output.toString();
        println(jsonStr);
        try {
            JSONObject jObject = new JSONObject(jsonStr);
            youtubeTitle = jObject.getString("title");
            youtubeName = jObject.getString("author_name");
            tvYoutubeTitle.setText(youtubeTitle);
            tvYoutubeName.setText(youtubeName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void println(final String data){
        handler.post(new Runnable() {
            @Override
            public void run() {
                tv.append(data+"\n");
            }
        });
    }
}