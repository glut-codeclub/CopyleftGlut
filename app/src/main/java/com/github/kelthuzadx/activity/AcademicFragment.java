package com.github.kelthuzadx.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.github.kelthuzadx.R;
import com.github.kelthuzadx.network.Network;
import com.github.kelthuzadx.network.UrlConstants;
import com.github.kelthuzadx.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AcademicFragment extends Fragment {
    Handler handler = new Handler();
    List<HashMap<String, String>> data = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_simple_card, null);


        Network.getInstance().asyncGet(UrlConstants.URL_AVATAR, (call, response) -> {
            InputStream inputStream = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ImageView avatar = v.findViewById(R.id.imageView2);
            handler.post(() -> avatar.setImageBitmap(bitmap));
        });




        Network.getInstance().asyncGet(UrlConstants.URL_SUB_ACADEMIC_INFO,((call, response) -> {
            try {
                ListView listView = v.findViewById(R.id.infoList);

                String line = response.body().string();
                HashMap<String, String> m = new HashMap<>();

                m.put("attr","学号");
                m.put("val",Util.findFromAcademicPage(line,"username"));
                data.add(m);

                m = new HashMap<>();
                m.put("attr","姓名");
                m.put("val",Util.findFromAcademicPage(line,"realname"));
                data.add(m);

                m = new HashMap<>();
                m.put("attr","性别");
                m.put("val",Util.findFromAcademicPage(line,"gender"));
                data.add(m);

                m = new HashMap<>();
                m.put("attr","出生日期");
                m.put("val",Util.findFromAcademicPage(line,"birthday"));
                data.add(m);

                m = new HashMap<>();
                m.put("attr","籍贯");
                m.put("val",Util.findFromAcademicPage(line,"nativePlace"));
                data.add(m);

                m = new HashMap<>();
                m.put("attr","证件号码");
                m.put("val",Util.findFromAcademicPage(line,"idno"));
                data.add(m);

                m = new HashMap<>();
                m.put("attr","高考考号");
                m.put("val",Util.findFromAcademicPage(line,"examStuNo"));
                data.add(m);

                m = new HashMap<>();
                m.put("attr","中学名字");
                m.put("val",Util.findFromAcademicPage(line,"highschool"));
                data.add(m);

                m = new HashMap<>();
                m.put("attr","毕业日期");
                m.put("val",Util.findFromAcademicPage(line,"graduateDate"));
                data.add(m);


                System.out.println(data);
                handler.post(()->{
                    SimpleAdapter adapter = new SimpleAdapter(getContext(),data,R.layout.list_item,
                        new String[]{"attr", "val"}, new int[]{R.id.attr, R.id.val});
                    listView.setAdapter(adapter);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }));


        return v;
    }
}
