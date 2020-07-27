package hi.beauty.haicantik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class Deteksi extends AppCompatActivity {
    GLCM glcm=new GLCM();
    Button btnAmbil;
    Button btnPrediksi;
    ImageView imgGambar;
    Bitmap gambar_ori;
    int RESULT_LOAD_IMAGE = 100;
    public static final String EXTRA_FILE_TAG = "ENCODED FILE";

    private static final String LOG_TAG = Deteksi.class.getSimpleName();
    private File baseImage;   String extension="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deteksi);

        btnAmbil = findViewById(R.id.btnAmbil);
        imgGambar = findViewById(R.id.imGambar);

        btnAmbil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(Deteksi.this);
            }
        });

        btnPrediksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GLCMFeatureExtraction glcmfe = new GLCMFeatureExtraction(gambar_ori, 15);
                    Model_predikis md = new Model_predikis("",glcmfe.getContrast(),glcmfe.getHomogenity(),glcmfe.getEntropy(),glcmfe.getEnergy(),glcmfe.getDissimilarity());

                    String masalah="" ;
                    double jarak_terpendek = Double.MAX_VALUE;
                    for (int i = 0 ; i < MainActivity.data_set.size();i++){
                        double jarak_saat_ini = hitung_jarak(md,MainActivity.data_set.get(i));
                        if (jarak_saat_ini < jarak_terpendek){
                            jarak_terpendek = jarak_saat_ini;
                            masalah = MainActivity.data_set.get(i).penyakit;
                        }
                    }
                    Toast.makeText(Deteksi.this,masalah,Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    double hitung_jarak(Model_predikis a, Model_predikis b){
        double jarak = 0;
        jarak = jarak + (Math.abs(a.fitur1) - Math.abs(b.fitur1));
        jarak = jarak + (Math.abs(a.fitur2) - Math.abs(b.fitur2));
        jarak = jarak + (Math.abs(a.fitur3) - Math.abs(b.fitur3));
        jarak = jarak + (Math.abs(a.fitur4) - Math.abs(b.fitur4));
        jarak = jarak + (Math.abs(a.fitur5) - Math.abs(b.fitur5));

        return  jarak;
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo","Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);


                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        gambar_ori = selectedImage;
                        imgGambar.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();

                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = Deteksi.this.getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                baseImage = new File(picturePath);
                                String nama_file = baseImage.getName();
                                extension = nama_file.split("\\.")[1];
                                gambar_ori = BitmapFactory.decodeFile(picturePath);
                                imgGambar.setImageBitmap(BitmapFactory.decodeFile(picturePath));


                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }


}
