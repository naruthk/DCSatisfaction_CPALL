package cpall.DCSatisfaction_CPALL;

// For android
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.widget.Toast;

// For server
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

// For java
import java.io.IOException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;

public class ConfirmNoQuestion extends Activity {

    // Session Manager Class
    SharedManagement session;
    String storeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_no_question);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setFinishOnTouchOutside(false);
        }

        session = new SharedManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        storeID = user.get(SharedManagement.KEY_STORE_ID);
        TextView storeID_top = (TextView) findViewById(R.id.global_textView_storeID);
        String storeTag = "รหัสร้าน: " + storeID;
        storeID_top.setText(storeTag);

        Bundle bundle = getIntent().getExtras();
        String choiceSelectedPreviously = bundle.getString("choice");

        ImageButton selectedEmotion = (ImageButton) findViewById(R.id.imgbtn_confirm_emotionSelected);
        TextView describeSelectedEmotion = (TextView) findViewById(R.id.txtview_confirm_emotionText);

        // Set text to display "Happy"
        if (choiceSelectedPreviously.equals("3")) {
            selectedEmotion.setBackgroundResource(R.drawable.emotion_happy);
            describeSelectedEmotion.setText("เยี่ยม");
        }

        // Set text to display "Okay"
        if (choiceSelectedPreviously.equals("2")) {
            selectedEmotion.setBackgroundResource(R.drawable.emotion_okay);
            describeSelectedEmotion.setText("เฉยๆ");
        }

        // Set text to display "Mad"
        if (choiceSelectedPreviously.equals("1")) {
            selectedEmotion.setBackgroundResource(R.drawable.emotion_mad);
            describeSelectedEmotion.setText("ไม่พอใจ");
        }

        Button backButton = (Button) findViewById(R.id.buttonBack);     // Back button
        Button submitButton = (Button) findViewById(R.id.buttonSubmit); // Submit button

        // กดปุ่มเพื่อย้อนกลับไปหน้าก่อนหน้านี้
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                onBackPressed();
            }
        });

        // ถ้าคลิกยืนยันแล้ว โปรแกรมจะเชื่อมต่อเข้าสู่เซิรวเวอร์ของ isscloud และเก็บข้อมูลทันที
        // หากการเชื่อมต่อผิดพลาดจะมีการแสดง Error ขึ้นมาให้ผู้ใช้รับรู้
        submitButton.setOnClickListener(new View.OnClickListener() {

            // แปลง Stream ของ text เป็นรูปแบบของ String
            private String convertStreamToString(InputStream is) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return sb.toString();
            }

            // แปลงตัวอักษรไทยที่ได้มาจาก QR โค้ดให้เป็นชนิดที่ทาง server อ่านออก
            public String encodeUnicode(String str) {
                StringBuilder sb = new StringBuilder();
                char[] chars = str.toCharArray();
                for (char ch : chars) {
                    if (' ' <= ch && ch <= '\u007E')
                        sb.append(ch);
                    else
                        sb.append(String.format("\\u%04x", ch & 0xFFFF));
                }
                return sb.toString();
            }

            // ขั้นตอนเบื้องค้น
            // 1. เช็ตดูว่าเชื่อมต่อกับอินเตอร์เน็ตหรือไม่
            // 2. ดึงข้อมูล ทะเบียนรถ และ คะแนน มาจัดเก็บในรูปแบบของ JSON
            // 3. เชิ่อมต่อไปยังไฟล์ PHP บน server ของ isscloud
            // 4. หากผิดพลาดโปรแกรมจะเก็บ log เอาไว้ พร้อมแสดง Error ในรูปแบบที่ผู้ใช้งานสาม่ารถเข้าใจได้
            // 5. ถ้าสำเร็จ โปรแกรมจะแสดงหน้าจอ ขอบคุณ
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(ConfirmNoQuestion.this)) //returns true if internet available
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // ดึงข้อมูล ทะเบียนรถ และ คะแนน มาจัดเก็บในรูปแบบของ JSON
                                Bundle bundle = getIntent().getExtras();
                                String plateNumber = bundle.getString("plateNumber");
                                String voteResult = bundle.getString("choice");

                                // ไฟล์บน server ที่รอรับข้อมูล
                                String urlPath = "http://isscloud.cpall.co.th/qrvote/add_noQuestion.php";

                                // เชื่อมต่อกับ server
                                HttpClient client = new DefaultHttpClient();
                                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
                                HttpResponse response;

                                // Create a JSON object
                                JSONObject json = new JSONObject();
                                try {
                                    HttpPost post = new HttpPost(urlPath); // Use POST instead of GET

                                    // Store objects in JSON
                                    json.put("Plate Number", plateNumber);
                                    json.put("Vote Result", voteResult);
                                    json.put("Store ID", storeID);

                                    Log.i("jason Object", json.toString()); // Output JSON

                                    // We use the name "json" to let PHP know where to look for the JSON object
                                    post.setHeader("json", json.toString());

                                    // แปลงให้ข้อความเป็น UTF-8 จะได้ตรงกับค่าที่เซ็ตว่าในฐานข้อมูล
                                    String reFormatted = encodeUnicode(json.toString());
                                    StringEntity se = new StringEntity(reFormatted);
                                    se.setContentType("application/json;charset=UTF-8");
                                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                                    post.setEntity(se);
                                    response = client.execute(post); // Send the JSON to PHP file

                                    // ถ้ามีการตอบรับจาก server แล้ว ทางระบบจะแสดงผลออกมใน log
                                    if (response != null) {
                                        InputStream in = response.getEntity().getContent();
                                        String a = convertStreamToString(in);
                                        Log.i("Message from server: ", a);
                                        Intent openPrintSuccess = new Intent(ConfirmNoQuestion.this, PrintSuccess.class);
                                        startActivity(openPrintSuccess);
                                    } else {
                                        Intent openConfirmationDialogue = new Intent(ConfirmNoQuestion.this, NoNetwork.class);
                                        startActivity(openConfirmationDialogue);
                                    }

                                } catch (Exception e) {
                                    //   errorDisplay();
                                    e.printStackTrace();
                                    Intent openConfirmationDialogue = new Intent(ConfirmNoQuestion.this, NoNetwork.class);
                                    startActivity(openConfirmationDialogue);
                                }
                                //   }
                            } catch (Exception ex) {
                                // errorDisplay();
                                System.out.println("Failed to contact and obtain response from the server.");
                                System.out.println("Please try again!");
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    // ในกรณีที่ Error
                    Intent openConfirmationDialogue = new Intent(ConfirmNoQuestion.this, NoNetwork.class);
                    startActivity(openConfirmationDialogue);
                }
            }
        });
    }

}
