package cpall.DCSatisfaction_CPALL;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;
import java.util.HashMap;
import android.content.Intent;
import android.widget.Toast;
import android.widget.TextView;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;

// Start of Z-bar QR scanner import
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;
// End of Z-bar QR Scanner import

public class ScanQR extends Activity {

    SharedManagement session;

    // อย่าแก้ไขส่วนของ Z-bar
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;

    // ตัวเรียก Z-bar สแกนเนอร์
    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr_code);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // ให้แอปนอนตะแคงไม่ได้

        // ดึงข้อมูลและแสดงผล
        session = new SharedManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String storeID = user.get(SharedManagement.KEY_STORE_ID);
        TextView storeID_top = (TextView) findViewById(R.id.global_textView_storeID);
        String storeTag = "รหัสร้าน: " + storeID;
        storeID_top.setText(storeTag);

        // โค้ดของ Z-bar
        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        if (barcodeScanned) {
            barcodeScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    // เมื่อโค้ดถูกถ่ายเรียบร้อยแล้ว โปรแกรมจะเช็คดูว่ารหัสร้านในโค้ดนั้นมีตัวอักษร "#" 2 ตัวหรือไม่
    // ถ้ามีครบทุกประการ ก็จะเก็บข้อมูลไว้แล้วไปอีก Activity นึง
    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();
            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);
            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                String receiveInformation = "";

                //รวบรวมข้อมูลจาก QR โค้ดและเก็บไว้ใน String receiveInformation
                for (Symbol sym : syms) {
                    receiveInformation += sym.getData();
                    barcodeScanned = true;
                }

                // ตรวจสอบว่ามีตัวอักษา # กี่ตัว
                int countHash = 0;
                for (int i = 0; i < receiveInformation.length(); i++) {
                    if (receiveInformation.charAt(i) == '#') {
                        countHash++;
                    }
                }

                // แสดงข้อความว่าผิดพลาดถ้าไม่ครบ 2 ตัวอักษร
                // แต่ถ้าครบก็ไปหน้าต่อไป
                if (countHash < 2) {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                    Intent backHome = new Intent(ScanQR.this, ScanQR.class);
                    startActivity(backHome);
                } else {
                    Intent i = new Intent(ScanQR.this, Rating.class);
                    Bundle b = new Bundle();
                    b.putString("ScanResult", receiveInformation);
                    i.putExtras(b);
                    startActivity(i); // Fire up the next activity immediately
                }
            }
        }
    };

    // แสดงข้อความ Error
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1)
                Toast.makeText(getApplicationContext(), "ข้อมูลไม่ถูกต้อง กรุณา Scan ใหม่อีกครั้ง", Toast.LENGTH_LONG).show();
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    // ถ้ากด ย้อนกลับ โปรแกรมจะกลับไปหน้าหลักอีกครั้ง
    @Override
    public void onBackPressed() {
        Intent openConfirmationDialogue = new Intent(ScanQR.this, HomeScreen.class);
        startActivity(openConfirmationDialogue);
    }

}