package cpall.DCSatisfaction_CPALL;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.widget.ImageButton;
import java.util.HashMap;

public class Rating extends Activity {

    SharedManagement session;

    TextView txtview_plateNumber, txtview_name, txtview_supplierName, storeID_top;
    String plateNumber, name, supplierName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        //        HashMap<String, String> user = session.getUserDetails();
        //        String storeID = user.get(SharedManagement.KEY_STORE_ID);
        //        storeID_top = (TextView) findViewById(R.id.global_textView_storeID);
        //        String storeTag = "รหัสร้าน: " + storeID;
        //        storeID_top.setText(storeTag);

        Bundle bundle = getIntent().getExtras();
        String information = bundle.getString("ScanResult");

        // แยก String ออกโดย แยกออกทุกครั้งที่เจอ #
        String[] parts = information.split("#");
        plateNumber = parts[0];
        name = parts[1];
        supplierName = parts[2];

        txtview_plateNumber = (TextView) findViewById(R.id.rating_result_plateNumber);
        txtview_name = (TextView) findViewById(R.id.rating_result_name);
        txtview_supplierName = (TextView) findViewById(R.id.rating_result_supplierName);

        txtview_plateNumber.setText(plateNumber);
        txtview_name.setText(name);
        txtview_supplierName.setText(supplierName);

        ImageButton selected_happy = (ImageButton) findViewById(R.id.imgbtn_rating_happy);    // Happy face
        ImageButton selected_okay = (ImageButton) findViewById(R.id.imgbtn_rating_okay);      // Okay face
        ImageButton selected_mad = (ImageButton) findViewById(R.id.imgbtn_rating_mad);        // Mad face

        TextView happyTxt = (TextView) findViewById(R.id.txtview_label_happy);
        TextView okayTxt = (TextView) findViewById(R.id.txtview_label_okay);
        TextView madTxt = (TextView) findViewById(R.id.txtview_label_mad);

        // ถ้ากดยิ้ม จะได้คะแนน 3 คะแนน
        // เมธอดจะส่งข้อมูลไปที่ fireActivity()

        // ถ้าผู้ใข้งานเลือก "เยี่ยม"
        selected_happy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fireActivity("3", plateNumber);
            }
        });
        happyTxt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fireActivity("3", plateNumber);
            }
        });

        // ถ้าผู้ใข้งานเลือก "เฉยๆ"
        selected_okay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fireActivity("2", plateNumber);
            }
        });
        okayTxt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fireActivity("2", plateNumber);
            }
        });

        // ถ้าผู้ใข้งานเลือก "พอใช้"
        selected_mad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fireActivity("1", plateNumber);
            }
        });
        // If Mad face is selected,
        madTxt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fireActivity("1", plateNumber);
            }
        });

    }

    // ถ้ากดย้อนหลัง โปรแกรมจะนำผู้ใช้งานไปสู่หน้าสแกน QR โค้ดอีกครั้ง
    @Override
    public void onBackPressed() {
        Intent back = new Intent(Rating.this, ScanQR.class);
        startActivity(back);
    }

    // เมธอดนี้รวบรวมข้อมูลทั้งหมด อาธิ ชื่อ นามกสุล และผลโหวท เพื่อส่งข้อมูลทั้งหมด
    // ต่อไปยังคลาส ConfirmNoQuestion.class
    public void fireActivity(String emotionStatus, String plateNumber) {
        // เปลี่ยนเป็น ConfirmWithQuestions.class เมื่อโปรแกรมต้องการให้ผู้ใช้ตอบคำถามเพิ่มเติม

        // ถ้าพบว่าสัญญานอินเตอร์เน็ตนั้นถูกเชื่อมต่อไว้เรียบร้อยแล้ว
        if (CheckNetwork.isInternetAvailable(Rating.this)) //returns true if internet available
        {
            Intent i = new Intent(Rating.this, ConfirmNoQuestion.class);
            Bundle b = new Bundle();
            b.putString("plateNumber", plateNumber);
            b.putString("choice", emotionStatus);
            i.putExtras(b);
            startActivity(i);
        } else {
            Intent openConfirmationDialogue = new Intent(Rating.this, NoNetwork.class);
            startActivity(openConfirmationDialogue);
        }
    }
}