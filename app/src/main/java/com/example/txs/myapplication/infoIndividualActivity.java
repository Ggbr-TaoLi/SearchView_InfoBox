package com.example.txs.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

/**
 * 单条数据的详细信息
 */
public class infoIndividualActivity extends AppCompatActivity {
    //声明TextView对象
    private TextView textview;
    private MainActivityWhole mainActivityWhole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_individual);
       // 获取意图对象
        Intent intent = getIntent();
        //获取传递的值
        String str = intent.getStringExtra("data");
        if (str != null) {
            str = str.replace(',', '\n');
            textview = (TextView) this.findViewById(R.id.tv_textInd);
            textview.setText(str);
        }
    }

}