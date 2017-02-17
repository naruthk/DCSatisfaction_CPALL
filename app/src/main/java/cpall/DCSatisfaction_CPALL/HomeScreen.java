// CP All Corporation
// DC Satisfaction Application - QR Reader
// โปรแกรมนี้รับข้อมูลของบุคคลจากโค้ด QR
// และส่งข้อมูลขึ้น server ของ isscloud โดยตรง

package cpall.DCSatisfaction_CPALL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.HashMap;

public class HomeScreen extends Activity {

    SharedManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        session = new SharedManagement(getApplicationContext());
        session.checkLogin(); // ถ้าหากว่ารหัสไม่เคยถูกเก็บไว้บนตัวเครื่อง ระบบจะให้ผูใช้กลับไปหน้าป้อนรหัสอีกครั้ง

        // เรียกข้อมูลรหัสร้าน
        HashMap<String, String> user = session.getUserDetails();

        // แสดงผลรหัสร้านบนหน้าจอ
        String storeID = user.get(SharedManagement.KEY_STORE_ID);
        TextView storeID_top = (TextView) findViewById(R.id.global_textView_storeID);
        String storeTag = "รหัสร้าน: " + storeID;
        storeID_top.setText(storeTag);

        // Start button
        Button btn_home_submit = (Button) findViewById(R.id.btn_home_start);

        // เช็คดูว่ามีสัญญานอินเตอร์เน็ตหรือไม่ ถ้ามีก็ไปหน้าต่อไป หากไม่มีสัญญานก็ต้องเริ่มต้นใหม่อีกครั้ง
        btn_home_submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Connect to CheckNetwork.class เพื่อดูว่าเชิ่อมต่อกับอินเตอร์เน็ตหรือไม่
                if (CheckNetwork.isInternetAvailable(HomeScreen.this)) //returns true if internet available
                {
                    startActivity(new Intent(HomeScreen.this, ScanQR.class));
                } else {
                    Intent openConfirmationDialogue = new Intent(HomeScreen.this, NoNetwork.class);
                    startActivity(openConfirmationDialogue);
                }
            }
        });
    }

    // ปิดกั้นไม่ให้ผู้ใช้กดปุ่ม Back บนมือถือได้
    @Override
    public void onBackPressed() {}
}
