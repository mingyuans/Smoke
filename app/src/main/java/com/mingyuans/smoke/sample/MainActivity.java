package com.mingyuans.smoke.sample;

import android.app.Activity;
import android.os.Bundle;

import com.mingyuans.smoke.Smoke;
import com.mingyuans.smoke.SubSmoke;

import java.util.ArrayList;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
