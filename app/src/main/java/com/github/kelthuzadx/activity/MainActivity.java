package com.github.kelthuzadx.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kelthuzadx.R;
import com.github.kelthuzadx.network.Network;
import com.github.kelthuzadx.network.UrlConstants;

import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    Button confirm;
    ImageView captchaImage;
    Handler handler = new Handler();

    EditText studentId;
    EditText password;
    EditText editCaptcha;

    int count = 0;

    public void switchUI(){
        if (count == 0) {
            imageView.setImageResource(R.drawable.good_night_img);
            textView.setText(" | 注册");
            captchaImage.setVisibility(View.INVISIBLE);
            editCaptcha.setVisibility(View.INVISIBLE);
            confirm.setClickable(false);
            confirm.setText("教务暂不支持注册");

            count = 1;
        } else {
            imageView.setImageResource(R.drawable.good_morning_img);
            captchaImage.setVisibility(View.VISIBLE);
            editCaptcha.setVisibility(View.VISIBLE);
            confirm.setClickable(true);
            textView.setText(" | 登陆");
            confirm.setText("我要登陆");
            count = 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadCaptchaImage() {
        Network.getInstance().asyncGet(UrlConstants.URL_CAPTCHA_IMG, (call, response) -> {
            InputStream inputStream = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            handler.post(() -> captchaImage.setImageBitmap(bitmap));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        studentId = findViewById(R.id.student_id);
        password = findViewById(R.id.password);
        editCaptcha = findViewById(R.id.editCaptcha);

        // Load captcha image
        loadCaptchaImage();
        captchaImage.setOnClickListener(v -> loadCaptchaImage());

        textView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {}
            public void onSwipeRight() {switchUI();}
            public void onSwipeLeft() {switchUI();}
            public void onSwipeBottom() {}

        });


        confirm.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(),"正在登陆...",Toast.LENGTH_LONG).show();
            HashMap<String,String> paramsMap=new HashMap<>();
//            paramsMap.put("j_username",studentId.getText().toString());
//            paramsMap.put("j_password",password.getText().toString());
            paramsMap.put("j_username","3162052051639");
            paramsMap.put("j_password","1005783621758yy");
            paramsMap.put("j_captcha",editCaptcha.getText().toString());
            Network.getInstance().asyncPostForm(paramsMap, UrlConstants.URL_LOGIN,
                    (call, response) ->{
                        System.out.println("Login successfully");
                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));

                    });
        });
    }
}
