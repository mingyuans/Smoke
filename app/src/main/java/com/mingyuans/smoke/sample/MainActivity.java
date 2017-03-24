package com.mingyuans.smoke.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import com.mingyuans.smoke.Smoke;
import com.mingyuans.smoke.SubSmoke;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yanxq on 2017/2/19.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Smoke.install(this,"SmokeSample");
        Smoke.info("hello");

        Smoke.verbose(new String[]{"one","two"});

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add("array_one");
        arrayList.add("array_two");
        ArrayList<String> childArrayList = new ArrayList<>();
        childArrayList.add("array_child_one");
        arrayList.add(childArrayList);
        Smoke.verbose(arrayList);

        Smoke.verbose("hello");

        Smoke.error(arrayList);

        Smoke.error(new Throwable());

        SubSmoke subSmoke = Smoke.newSub("subMain");
        subSmoke.verbose("sub hello.");
        subSmoke.error(new Exception("sub error."));

        Smoke.info("native allocated: {0}, free: {1}, size{2}",Debug.getNativeHeapAllocatedSize(),Debug.getNativeHeapFreeSize(),
                Debug.getNativeHeapSize());

        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder()
                .url("https://github.com/mingyuans/Smoke")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseMessage = response.body().string();
                response.close();
                Smoke.info("response size : {0}",responseMessage.length());
                Smoke.info(responseMessage);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
