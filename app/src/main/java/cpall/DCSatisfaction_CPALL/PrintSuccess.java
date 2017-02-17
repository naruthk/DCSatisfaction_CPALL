package cpall.DCSatisfaction_CPALL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class PrintSuccess extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_print_success);

        Button btn_printSuccess_returnHome = (Button) findViewById(R.id.btn_printSuccess_returnHome);
        btn_printSuccess_returnHome.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//            Intent openConfirmationDialogue = new Intent(PrintSuccess.this, HomeScreen.class);
//            startActivity(openConfirmationDialogue);
            }
        });
    }

    // ปิดกั้นไม่ให้ผู้ใช้กดปุ่ม Back บนมือถือได้
    @Override
    public void onBackPressed() {}
}
