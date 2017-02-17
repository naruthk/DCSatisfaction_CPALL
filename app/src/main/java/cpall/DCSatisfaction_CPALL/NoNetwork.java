package cpall.DCSatisfaction_CPALL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class NoNetwork extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_no_network);

    }
    public void onClick(View v) {
        onBackPressed();
        //Intent backToHome = new Intent(NoNetwork.this, HomeScreen.class);
        //startActivity(backToHome);
    }
}
