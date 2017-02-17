package cpall.DCSatisfaction_CPALL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Gravity;

public class FirstTimeVerify extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_verify);

        // ปุ่มกดเพื่อไปหน้าต่อไป
        Button btn_firstTime_continue = (Button) findViewById(R.id.btn_firstTime_continue);

        // ถ้าเกิดทำงานแล้ว โปรแกรมจะเช็คว่าขนาดของรหัสพนักงานถูกต้องหรือไม่ และ หากถูกต้องก็จะส่ง
        // ผู้ใช้งานไปหน้าต่อไป
        btn_firstTime_continue.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // รับข้อความเป็นคัวเลข
                EditText firstTime_input = (EditText) findViewById(R.id.firstTime_input);
                firstTime_input.setGravity(Gravity.CENTER);
                String storeID = firstTime_input.getText().toString();

                // เช็คว่ามี 5 จำนวน ถ้าใช้ไป Activity ต่อไป หากไม่ใช่จะโชว์ Error message
                if (storeID.trim().length() == 5) {

                    Intent openConfirmationDialogue = new Intent(FirstTimeVerify.this, SecondTimeVerify.class);
                    Bundle b = new Bundle();
                    b.putString("storeID", storeID);
                    openConfirmationDialogue.putExtras(b);
                    startActivity(openConfirmationDialogue);

                } else {

                    Message msg = handler.obtainMessage();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);

                }

            }
        });
    }

    // ปิดกั้นไม่ให้ผู้ใช้กดปุ่ม Back บนมือถือได้
    @Override
    public void onBackPressed() {}

    // แสดข้อความ Error
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String error = "กรุณากรอกรหัสร้านเป็นตัวเลข 5 หลัก";
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }
    };
}