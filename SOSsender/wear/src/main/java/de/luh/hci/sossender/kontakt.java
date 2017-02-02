package de.luh.hci.sossender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by weijt606 on 2016/11/30.
 */

public class kontakt extends Activity {

    private Button familie;
    private Button arzt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kontakt);

        familie = (Button)findViewById(R.id.familie);
        arzt = (Button)findViewById(R.id.arzt);


        familie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(kontakt.this,familie.class);
                startActivity(i);
            }
        });
        arzt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(kontakt.this,arzt.class);
                startActivity(i);
            }
        });



    }
}
