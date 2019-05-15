package com.shashank.platform.loginui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    Button confirm;
    ImageView captchaImage;
    Handler handler = new Handler();
    EditText editCaptcha;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private OkHttpClient okHttpClient=new OkHttpClient.Builder().cookieJar(new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            cookieStore.put(httpUrl.host(), list);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
            List<Cookie> cookies = cookieStore.get(httpUrl.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    }).build();

    int count = 0;

    public void switchUI(){
        if (count == 0) {
            imageView.setImageResource(R.drawable.good_night_img);
            textView.setText(" | 注册");
            confirm.setText("我要注册");

            count = 1;
        } else {
            imageView.setImageResource(R.drawable.good_morning_img);
            textView.setText(" | 登陆");
            confirm.setText("我要登陆");
            count = 0;
        }
    }

    private void loadCaptchaImage() {
        String url = "http://jw.glut.edu.cn/academic/getCaptcha.do";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                System.out.println("test:"+bitmap.toString());
                handler.post(() -> captchaImage.setImageBitmap(bitmap));

            }
        });
    }

    private void postRequest(HashMap<String,String> paramsMap, String url) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            builder.add(key, paramsMap.get(key));
        }
        RequestBody formBody=builder.build();
        Request request=new Request.Builder().url(url).post(formBody).build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", e.toString());
                Log.e("MainActivity", "Login failed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("MainActivity", "Login successfully");
                System.out.println(cookieStore);
                getRequest("http://jw.glut.edu.cn/academic/index_new.jsp");//"http://jw.glut.edu.cn/academic/student/studentinfo/studentInfoModifyIndex.do?frombase=0&wantTag=0&groupId=&moduleId=2060");
            }
        });

    }

    public void getRequest(String url) {
        new Thread(() -> {
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                Call call=okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e("MainActivity", "P");
                        System.out.println(response.body().toString());
                        //"http://jw.glut.edu.cn/academic/student/studentinfo/studentInfoModifyIndex.do?frombase=0&wantTag=0&groupId=&moduleId=2060");
                    }
                });
                String responseResult = response.body().string();
                System.out.println(responseResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        confirm = findViewById(R.id.confirm);
        captchaImage = findViewById(R.id.captchaImage);
        editCaptcha = findViewById(R.id.editCaptcha);

        loadCaptchaImage();
        captchaImage.setOnClickListener(v -> loadCaptchaImage());
        textView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {}
            public void onSwipeRight() {switchUI();}
            public void onSwipeLeft() {switchUI();}
            public void onSwipeBottom() {}

        });
        confirm.setOnClickListener(v -> {
            HashMap<String,String> paramsMap=new HashMap<>();
            paramsMap.put("j_username","3162052051639");
            paramsMap.put("j_password","1005783621758yy");
            paramsMap.put("j_captcha",editCaptcha.getText().toString());
            postRequest(paramsMap,"http://jw.glut.edu.cn/academic/j_acegi_security_check");
        });
    }
}
