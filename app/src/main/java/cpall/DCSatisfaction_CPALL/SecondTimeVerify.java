package cpall.DCSatisfaction_CPALL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

// คลาสนี้จะทำการยืนยันและบันทึกรหัสพนักงานที่ถูกป้อนเข้ามาในระบบ
// โดยจะใช้ SharedManagement.class ร่วมด้วย

public class SecondTimeVerify extends Activity {

    SharedManagement session;   // ทำหน้าที่สร้าง Session ใหม่ขึ้นมาเพื่อเก็บข้อมูล
    String storeID;             // เก็บบันทึกรหัสร้าน

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_second_time_verify);

        session = new SharedManagement(getApplicationContext());

        // ใช้ Bundle เพื่อดึงข้อมูลมาจาก Activity ก่อนหน้านี้
        Bundle bundle = getIntent().getExtras();
        storeID = bundle.getString("storeID"); // เก็บไว้ในสตริง storeID

        TextView input = (TextView) findViewById(R.id.secondTime_input);
        input.setText("รหัสร้าน: " + storeID); // แสดงให้ผู้ใช้งานเห็นว่ารหัสคืออะไร

        Button backButton = (Button) findViewById(R.id.secondTime_btn_back);    // กลับหน้าก่อนหน้านี้
        Button submitButton = (Button) findViewById(R.id.secondTime_btn_go);    // ไปต่อ

        // กลับไปหน้าเริ่มต้นเพื่อกรอกข้อมุลใหม่
        backButton.setOnClickListener(new View.OnClickListener() {
            // Go to the "FirstTimeVerify" class
            public void onClick(View v) {
                startActivity(new Intent(SecondTimeVerify.this, FirstTimeVerify.class));
            }
        });

        // ไปหน้าหลักจริงๆ และเก็บข้อมูลรหัสร้านไว้ในมือถือเลย
        submitButton.setOnClickListener(new View.OnClickListener() {
            // Go to the "ScanQR" class
            public void onClick(View v) {

                session.createLoginSession(storeID);

                Intent i = new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(i);
                finish();

            }
        });
    }
}