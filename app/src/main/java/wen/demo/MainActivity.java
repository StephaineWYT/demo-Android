package wen.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wen.demo.entity.Student;

public class MainActivity extends AppCompatActivity {

    private Student student = new Student();
    private List<Student> studentList = new ArrayList<>();
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_json);

        new Thread(runnable).start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("请求结果: ", val);
        }
    };


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO:http request.
            try {
                // 创建 OKHttp 实例，设置请求超时
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS).build();

                // 请求
                Request request = new Request.Builder()
                        .url("http://192.168.0.100:8080/hello")
                        .build();

                // 获取服务器返回的数据
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                // 解析数据
                JSONArray jsonArray = new JSONArray(responseData);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String age = jsonObject.getString("age");

                    student = new Student(id, name, age);
                    studentList.add(student);

                }

                for (Student student :
                        studentList) {
                    System.out.println(student.toString());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("");
                        for (Student student :
                                studentList) {
                            textView.append(student.toString());
                            textView.append("\n");
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
}
