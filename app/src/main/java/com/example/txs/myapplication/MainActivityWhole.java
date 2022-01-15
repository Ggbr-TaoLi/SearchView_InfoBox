package com.example.txs.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivityWhole extends AppCompatActivity {
    /**
     * 搜索框
     */
    private EditText mEdtSearch;
    /**
     * 删除按钮
     */
    private ImageView mImgvDelete;
    /**
     * recyclerview
     */
    private RecyclerView mRcSearch;
    /**
     * 全部匹配的适配器
     */
    private RcAdapterWholeChange adapter;
    /**
     * 所有数据 可以是联网或者file获取 如果有需要可以将其储存在数据库中 ，json解析出来的数据
     */
    private List<String> wholeList;
    /**
     * 此list用来保存符合我们规则的数据
     */
    private List<String> list;
    /**
     * 此String用来保存单个指定的完整数据
     */
    private String str = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_whole);
        initView();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refreshUI();
        setListener();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        //edittext的监听
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //每次edittext内容改变时执行 控制删除按钮的显示隐藏
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    mImgvDelete.setVisibility(View.GONE);
                } else {
                    mImgvDelete.setVisibility(View.VISIBLE);
                }
                //匹配文字 变色
                doChangeColor(editable.toString().trim());
            }
        });
        //recyclerview的点击监听
        adapter.setOnItemClickListener(new RcAdapterWholeChange.onItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                //获取选择的数据
                str = wholeList.get(pos);
//                Toast.makeText(MainActivityWhole.this, "妹子 pos== " + pos, Toast.LENGTH_SHORT).show();
                //页面跳转
                Intent in = new Intent(MainActivityWhole.this , infoIndividualActivity.class);//创建意图对象
                //设置传递键值对
                in.putExtra("data",str);
                //激活意图
                startActivity(in);
            }
        });
        //删除按钮的监听
        mImgvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEdtSearch.setText("");
            }
        });
    }

    /**
     * 字体匹配方法
     */
    private void doChangeColor(String text) {
        //clear是必须的 不然只要改变edittext数据，list会一直add数据进来
        list.clear();
        //不需要匹配 把所有数据都传进来 不需要变色
        if (text.equals("")) {
            list.addAll(wholeList);
            //防止匹配过文字之后点击删除按钮 字体仍然变色的问题
            adapter.setText(null);
            refreshUI();
        } else {
            //如果edittext里面有数据 则根据edittext里面的数据进行匹配 用contains判断是否包含该条数据 包含的话则加入到list中
            for (String i : wholeList) {
                if (i.contains(text)) {
                    list.add(i);
                }
            }
            //设置要变色的关键字
            adapter.setText(text);
            refreshUI();
        }
    }

    //加载数据
    private void initData() throws JSONException {
        //从网络上获取数据
        final String http = "https://belarusbank.by/api/infobox?city=%D0%9C%D0%B8%D0%BD%D1%81%D0%BA";
//         wholeList  = doGet(http);
//        new Thread() {
//            @Override
//            public void run() {
                try {
                    wholeList  = doGet(http);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
//            }
//        }.start();
        //从文件获取json数据并解析
        //载入数据
//        wholeList = doReader();
        list = new ArrayList<>();
        //初次进入程序时 展示全部数据
        list.addAll(wholeList);
    }

    /**
     * 刷新UI
     */
    private void refreshUI() {
        if (adapter == null) {
            adapter = new RcAdapterWholeChange(this, list);
            mRcSearch.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        mEdtSearch = (EditText) findViewById(R.id.edt_search);
        mImgvDelete = (ImageView) findViewById(R.id.imgv_delete);
        mRcSearch = (RecyclerView) findViewById(R.id.rc_search);
        //Recyclerview的配置
        mRcSearch.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 从文本读取数据
     * Android Studio自带org.json解析。
     * 优点：方便
     * 缺点：慢
     * 其它的解析方法：Jackson解析和Gson解析。Jackson最快
     */
    public ArrayList<String> doReader() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            String url = "assets/datas.json";
            InputStreamReader isr = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(url), "UTF-8");
            //从assets获取json文件，和eclipse里面的方法不同，我搞了好久才发现
            BufferedReader bfr = new BufferedReader(isr);//字节流转字符流
            String line ;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bfr.readLine())!=null){
                stringBuilder.append(line);
            }//将JSON数据转化为字符串
            JSONObject root = new JSONObject(stringBuilder.toString());
//            System.out.println("root:"+root.getString("cat"));//根据键名获取键值信息
            JSONArray array = root.getJSONArray("infoBox");
            for (int i = 0;i < array.length();i++) {
                JSONObject stud = array.getJSONObject(i);//用于获取解析数据
                StringBuffer st = new StringBuffer(); //用于拼接字符串
                st.append("info_id : "+stud.getString("info_id")+ "\n");
                st.append("area : "+stud.getString("area")+ "");
                st.append("city_type : "+stud.getString("city_type")+ "");
                st.append("city : "+stud.getString("city")+ "\n");
                st.append("address_type : "+stud.getString("address_type")+ "");
                st.append("address : "+stud.getString("address")+ "\n");
                st.append("house : "+stud.getString("house")+ "");
                st.append("install_place : "+stud.getString("install_place")+ "\n");
                st.append("location_name_desc : "+stud.getString("location_name_desc")+ "\n");
                st.append("work_time : "+stud.getString("work_time")+ "\n");
                st.append("time_long : "+stud.getString("time_long")+ "\n");
                st.append("gps_x : "+stud.getString("gps_x")+ "");
                st.append("gps_y : "+stud.getString("gps_y")+ "\n");
                st.append("currency : "+stud.getString("currency")+ "");
                st.append("inf_type : "+stud.getString("inf_type")+ "\n");
                st.append("cash_in_exist : "+stud.getString("cash_in_exist")+ "");
                st.append("cash_in : "+stud.getString("cash_in")+ "\n");
                st.append("type_cash_in : "+stud.getString("type_cash_in")+ "");
                st.append("inf_printer : "+stud.getString("inf_printer")+ "\n");
                st.append("region_platej : "+stud.getString("region_platej")+ "\n");
                st.append("popolnenie_platej : "+stud.getString("popolnenie_platej")+ ",");
                st.append("inf_status : "+stud.getString("inf_status"));
//                String temp = st.toString();
//                temp = temp.replace(',', '\n');
                arrayList.add(st.toString());
                bfr.close();
                isr.close();
//                is.close();//依次关闭流
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * 返回该链接地址的html数据
     *
     * @param urlStr
     * @return
     * @throws
     */
    public  ArrayList<String> doGet(String urlStr) throws JSONException, IOException {
        ArrayList<String> arrayList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
//        OkHttpClient client = new OkHttpClient();
//        final Request request = new Request.Builder().url(urlStr).build();
//        Call call = client.newCall(request);
//        Response response = call.execute();
//        String content = response.body().string();
//        System.out.println(content);
        try {
            URL url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                int len = 0;
                char[] buf = new char[1024];

                while ((len = isr.read(buf)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
                is.close();
                isr.close();
            } else {
                System.out.println("请求url失败！");
            }

        } catch (Exception e) {
            System.out.println("!!!!!!"+e.toString());
        }
//        System.out.println(sb.toString());
        //将JSON数据转化为字符串
//        JSONObject root = new JSONObject(sb.toString());
        JSONArray array = new JSONArray(sb.toString());
//            System.out.println("root:"+root.getString("cat"));//根据键名获取键值信息
//        JSONArray array = root.getJSONArray("infoBox");

        for (int i = 0; i < array.length(); i++) {
            JSONObject stud = array.getJSONObject(i);//用于获取解析数据
            StringBuffer st = new StringBuffer(); //用于拼接字符串
            st.append("info_id : " + stud.getString("info_id") + "\n");
            st.append("area : " + stud.getString("area") + "");
            st.append("city_type : " + stud.getString("city_type") + "");
            st.append("city : " + stud.getString("city") + "\n");
            st.append("address_type : " + stud.getString("address_type") + "");
            st.append("address : " + stud.getString("address") + "\n");
            st.append("house : " + stud.getString("house") + "");
            st.append("install_place : " + stud.getString("install_place") + "\n");
            st.append("location_name_desc : " + stud.getString("location_name_desc") + "\n");
            st.append("work_time : " + stud.getString("work_time") + "\n");
            st.append("time_long : " + stud.getString("time_long") + "\n");
            st.append("gps_x : " + stud.getString("gps_x") + "");
            st.append("gps_y : " + stud.getString("gps_y") + "\n");
            st.append("currency : " + stud.getString("currency") + "");
            st.append("inf_type : " + stud.getString("inf_type") + "\n");
            st.append("cash_in_exist : " + stud.getString("cash_in_exist") + "");
            st.append("cash_in : " + stud.getString("cash_in") + "\n");
            st.append("type_cash_in : " + stud.getString("type_cash_in") + "");
            st.append("inf_printer : " + stud.getString("inf_printer") + "\n");
            st.append("region_platej : " + stud.getString("region_platej") + "\n");
            st.append("popolnenie_platej : " + stud.getString("popolnenie_platej") + ",");
            st.append("inf_status : " + stud.getString("inf_status"));
            arrayList.add(st.toString());
        }
        return arrayList;
    }
}
