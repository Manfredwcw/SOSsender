package de.luh.hci.sossender;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class modul extends Activity {

    private Switch wach;
    private Switch schlaf;
//    private herzschlag_setting.hsdata hs;
    private SharedPreferences hs;
    private SharedPreferences data_m;
    private Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modul);
        data_m = getSharedPreferences("data_m",MODE_PRIVATE);
        final SharedPreferences.Editor editor = data_m.edit();
        hs = getSharedPreferences("data_hs", MODE_WORLD_READABLE);

//        m_data = getSharedPreferences("data_hs",MODE_PRIVATE);
//        try {
//            mcontext = createPackageContext("com.example.zr.sos_sender",CONTEXT_IGNORE_SECURITY);
//            m_data = mcontext.getSharedPreferences("data_hs",MODE_WORLD_READABLE);
//            Log.d("", m_data.getInt("low",40)+"------------------------------low------------");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        Toast.makeText(getApplicationContext(),"setted low value"+hs.getInt("low",40),Toast.LENGTH_SHORT).show();
        wach = (Switch) findViewById(R.id.switch_wach);
        schlaf = (Switch) findViewById(R.id.switch_schlaf);


        wach.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(wach.isChecked()&&schlaf.isChecked()){
                    Toast.makeText(getApplicationContext(),"Bitte waehlen Sie nur ein Modul",Toast.LENGTH_SHORT).show();
                    wach.setChecked(false);
                    schlaf.setChecked(false);
                }

                if(isChecked){
                    editor.putBoolean("wach",true);
                }else {
                    editor.putBoolean("wach",false);
                }
                editor.commit();

            }

        });
        schlaf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(wach.isChecked()&&schlaf.isChecked()){
                    Toast.makeText(getApplicationContext(),"Bitte waehlen Sie nur ein Modul",Toast.LENGTH_SHORT).show();
                    wach.setChecked(false);
                    schlaf.setChecked(false);
                }
                if(isChecked){
                    editor.putBoolean("schlaf",true);
                }else {
                    editor.putBoolean("schlaf",false);
                }
                editor.commit();
            }

        });

        wach.setChecked(data_m.getBoolean("wach",false));
        schlaf.setChecked(data_m.getBoolean("schlaf",false));

    }
    
}
