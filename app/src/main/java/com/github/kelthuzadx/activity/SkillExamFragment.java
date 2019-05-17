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

public class SkillExamFragment extends Fragment {
    Handler handler = new Handler();
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




        Network.getInstance().asyncGet(UrlConstants.URL_SKILLEXAM,((call, response) -> {
            try {
                ListView listView = v.findViewById(R.id.infoList);

                String line = response.body().string();
                List<HashMap<String, String>> data = new ArrayList<>();
                ArrayList<String[]> res = Util.findFromSkillExamPage(line);
                for(int i=0;i<res.size();i++){
                    HashMap<String, String> m = new HashMap<>();
                    m.put("attr",res.get(i)[0]);
                    m.put("val",res.get(i)[1]+"\t"+res.get(i)[2]);
                    data.add(m);
                }

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
