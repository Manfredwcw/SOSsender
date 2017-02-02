package de.luh.hci.sossender;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private Button modul;
    private Button herzschlag;
    private Button kontakt;
    private Button losgehen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();



        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
//        Button btn_modul = (Button) findViewById(R.id.modul);
//        btn_modul.setOnClickListener(new );
        modul = (Button)findViewById(R.id.modul);
        herzschlag = (Button)findViewById(R.id.herzschlag) ;
        kontakt = (Button)findViewById(R.id.kontakt);
        losgehen = (Button)findViewById(R.id.los);

        losgehen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, losgehen.class);
                startActivity(i);
            }
        });
        modul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,modul.class);
                startActivity(i);
            }
        });
        herzschlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,herzschlag_setting.class);
                startActivity(i);
            }
        });
        kontakt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,kontakt.class);
                startActivity(i);
            }
        });



//        mTextView = (TextView) findViewById(R.id.text);
//        mClockView = (TextView) findViewById(R.id.clock);
    }

//    @Override
//    public void onEnterAmbient(Bundle ambientDetails) {
//        super.onEnterAmbient(ambientDetails);
//        updateDisplay();
//    }

//    @Override
//    public void onUpdateAmbient() {
//        super.onUpdateAmbient();
//        updateDisplay();
//    }
//
//    @Override
//    public void onExitAmbient() {
//        updateDisplay();
//        super.onExitAmbient();
//    }

//    private void updateDisplay() {
//        if (isAmbient()) {
//            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
//            mTextView.setTextColor(getResources().getColor(android.R.color.white));
//            mClockView.setVisibility(View.VISIBLE);
//
//            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
//        } else {
//            mContainerView.setBackground(null);
//            mTextView.setTextColor(getResources().getColor(android.R.color.black));
//            mClockView.setVisibility(View.GONE);
//        }
//    }
}
