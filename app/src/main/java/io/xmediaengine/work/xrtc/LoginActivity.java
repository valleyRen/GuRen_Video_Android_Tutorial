package io.xmediaengine.work.xrtc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzj on 2018/3/13.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText mEditText;
    private int     mVideoPicType  = 0 ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEditText = (EditText) findViewById(R.id.room_id);

        List<String> list = new ArrayList<String>();
        list.add("90*160") ;
        list.add("180*320") ;
        list.add("270*480") ;
        list.add("360*640") ;
        list.add("450*800") ;
        list.add("540*960") ;
        list.add("720*1280") ;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Spinner sp = (Spinner) findViewById(R.id.spinner);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                mVideoPicType = position ;
                parent.setVisibility( View.VISIBLE ) ;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void loginMulti(View view){
        if (TextUtils.isEmpty(mEditText.getText())){
            Toast.makeText(this,"请输入roomid",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("room_id",mEditText.getText().toString());
        intent.putExtra("video_type", mVideoPicType ) ;
        startActivity(intent);
    }

    public void loginSingle(View view){
        if (TextUtils.isEmpty(mEditText.getText())){
            Toast.makeText(this,"请输入roomid",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this,MainSingleActivity.class);
        intent.putExtra("room_id",mEditText.getText().toString());
        intent.putExtra("video_type", mVideoPicType ) ;
        startActivity(intent);
    }
}
