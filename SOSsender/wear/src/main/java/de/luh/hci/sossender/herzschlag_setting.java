package de.luh.hci.sossender;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zr on 2016/12/1.
 */
public class herzschlag_setting extends WearableActivity implements NumberPicker.OnValueChangeListener,NumberPicker.OnScrollListener {

    private BoxInsetLayout mContainerView;
    private NumberPicker h_low;
    private NumberPicker h_high;
    private Button h_ok;
    private hsdata hs;
//    private Context mContext;
    private int low,high;
    Map<String,Integer> data;
    private SharedPreferences data_hs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herzschlag_setting);
        Log.d("", "-----create-----");
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
//        mContext.getApplicationContext();
//        hs = new hsdata(mContext);

        data_hs = getSharedPreferences("data_hs", MODE_WORLD_READABLE);
        final SharedPreferences.Editor editor = data_hs.edit();

        h_low = (NumberPicker) findViewById(R.id.herzschlag_low);
        h_high = (NumberPicker) findViewById(R.id.herzschlag_high);

        h_ok = (Button) findViewById(R.id.h_ok);
        h_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Die Begrenzung von Herzschlag : " + h_low.getValue() + " - " + h_high.getValue(), Toast.LENGTH_SHORT).show();
                editor.putInt("low", h_low.getValue());
                editor.putInt("high", h_high.getValue());
                editor.commit();
                low = h_low.getValue();
                high = h_high.getValue();
//                hs.save(low,high);
                Log.d("", String.valueOf(low) + "low: ------------------------");
                Log.d("", String.valueOf(high) + "high: ------------------------");
            }
        });
        init();
    }

    private void init() {
//        data = hs.read();
        h_low.setMinValue(0);
        h_low.setMaxValue(80);
        h_low.setValue(data_hs.getInt("low",40));
        h_low.setEnabled(true);
        h_low.setOnScrollListener(this);
        h_low.setOnValueChangedListener(this);

        h_high.setMinValue(60);
        h_high.setMaxValue(160);
        h_high.setValue(data_hs.getInt("high",100));
        h_high.setEnabled(true);
        h_high.setOnValueChangedListener(this);
        h_high.setOnScrollListener(this);

    }

    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                break;
            case SCROLL_STATE_IDLE:
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public class hsdata{

        public  hsdata(){}

        public void save(int low, int high){
            SharedPreferences data_hs = getSharedPreferences("data_hs",MODE_PRIVATE);
            SharedPreferences.Editor editor = data_hs.edit();
            editor.putInt("low", low);
            editor.putInt("high",high);
            editor.commit();
        }

        public Map<String,Integer> read(){
            Map<String,Integer> data = new HashMap<String,Integer>();
            SharedPreferences data_hs = getSharedPreferences("data_hs",MODE_PRIVATE);
            data.put("low",data_hs.getInt("low",40));
            data.put("high",data_hs.getInt("high",100));
            return data;
        }
    }

}
