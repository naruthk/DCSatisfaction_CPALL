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

    public SharedManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // ถ้าหากว่ารหัสไม่เคยถูกเก็บไว้บนตัวเครื่อง ระบบจะให้ผูใช้กลับไปหน้าป้อนรหัสอีกครั้ง
        // Session Authorization
        session = new SharedManagement(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();

        // แสดงผลรหัสร้านบนหน้าจอ
        String storeID = user.get(SharedManagement.KEY_STORE_ID);
        TextView storeID_top = (TextView) findViewById(R.id.global_textView_storeID);
        storeID_top.setText("รหัสร้าน: " + storeID);

        // Start button
        // เช็คดูว่ามีสัญญานอินเตอร์เน็ตหรือไม่ ถ้ามีก็ไปหน้าต่อไป หากไม่มีสัญญานก็ต้องเริ่มต้นใหม่อีกครั้ง
        Button btn_home_submit = (Button) findViewById(R.id.btn_home_start);
        btn_home_submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Connect to CheckNetwork.class เพื่อดูว่าเชิ่อมต่อกับอินเตอร์เน็ตหรือไม่
                if (CheckNetwork.isInternetAvailable(HomeScreen.this)) {
                    startActivity(new Intent(HomeScreen.this, ScanQR.class));
                } else {
                    Intent openConfirmationDialogue = new Intent(HomeScreen.this, NoNetwork.class);
                    startActivity(openConfirmationDialogue);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {}
}
