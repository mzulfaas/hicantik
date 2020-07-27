package hi.beauty.haicantik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import de.lmu.ifi.dbs.utilities.Arrays2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Dialog myDialog;
    GLCM glcm=new GLCM();
    public static ArrayList<Bitmap> bm_jerawat;
    public static ArrayList<Bitmap> bm_flek;
    public static ArrayList<Bitmap> bm_kering;
    public static ArrayList<Bitmap> bm_sensitif;
    public static ArrayList<Model_predikis> data_set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_recommends, R.id.navigation_skin_information, R.id.navigation_product, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        bm_jerawat = new ArrayList<>();
        bm_flek = new ArrayList<>();
        bm_kering = new ArrayList<>();
        bm_sensitif = new ArrayList<>();
        data_set = new ArrayList<>();

        Utils.requestAllPermission(MainActivity.this,0);

        String [] list;
        try {
            list = getAssets().list("JERAWAT");
            if (list.length > 0) {
                for (String file : list) {
                    System.out.println(file);
                    bm_jerawat.add(getBitmapFromAsset(this,"JERAWAT/"+file));
                }
            }


            list = getAssets().list("FLEK");
            if (list.length > 0) {
                for (String file : list) {
                    System.out.println(file);
                    bm_flek.add(getBitmapFromAsset(this,"FLEK/"+file));
                }
            }

            list = getAssets().list("KERING");
            if (list.length > 0) {
                for (String file : list) {
                    System.out.println(file);
                    bm_kering.add(getBitmapFromAsset(this,"KERING/"+file));
                }
            }

            list = getAssets().list("SENSITIF");
            if (list.length > 0) {
                for (String file : list) {
                    System.out.println(file);
                    bm_sensitif.add(getBitmapFromAsset(this,"SENSITIF/"+file));
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        //udah bisa listing
        //tinggal extract data dan simpan dalam bentuk csv
        //atau dalam database


        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Folder");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        System.out.println("" + var);


        final String filename = folder.toString() + "/" + "training.csv";




        // show waiting screen
        CharSequence contentTitle = getString(R.string.app_name);
        final ProgressDialog progDailog = ProgressDialog.show(
                MainActivity.this, contentTitle, "Proses Training",
                true);//please wait
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {




            }
        };

        new Thread() {
            public void run() {
                try {

                    FileWriter fw = new FileWriter(filename);

                    for (int i = 0 ; i < bm_jerawat.size() ; i++ ){
                        Bitmap bm = bm_jerawat.get(i);
                        isi_fitur("JERAWAT",fw,bm);

                    }
                    for (int i = 0 ; i < bm_flek.size() ; i++ ){
                        Bitmap bm = bm_flek.get(i);
                        isi_fitur("FLEK",fw,bm);

                    }
                    for (int i = 0 ; i < bm_kering.size() ; i++ ){
                        Bitmap bm = bm_kering.get(i);
                        isi_fitur("KERING",fw,bm);

                    }
                    for (int i = 0 ; i < bm_sensitif.size() ; i++ ){
                        Bitmap bm = bm_sensitif.get(i);
                        isi_fitur("SENSITIF",fw,bm);

                    }

                    // fw.flush();
                    fw.close();

                } catch (Exception e) {
                }
                handler.sendEmptyMessage(0);
                progDailog.dismiss();
                startActivity(new Intent(MainActivity.this,Deteksi.class));
            }
        }.start();

    }

    public void ShowPopup(View v) {
        TextView tv_closeff;
        Button btn_facialfoam;
        myDialog.setContentView(R.layout.facial_foam);
        tv_closeff = (TextView) myDialog.findViewById(R.id.tv_closeff);
//        btn_facialfoam = (Button) myDialog.findViewById(R.id.btn_facialfoam);
        tv_closeff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }



    void isi_fitur(String masalah,FileWriter fw,Bitmap bm){
        try {
            GLCMFeatureExtraction glcmfe = new GLCMFeatureExtraction(bm, 15);
            glcmfe.extract();
            fw.append(masalah);
            fw.append(",");
            fw.append(""+glcmfe.getContrast());
            fw.append(",");
            fw.append(""+glcmfe.getHomogenity());
            fw.append(",");
            fw.append(""+glcmfe.getEntropy());
            fw.append(",");
            fw.append(""+glcmfe.getEnergy());
            fw.append(",");
            fw.append(""+glcmfe.getDissimilarity());
            fw.append('\n');

            data_set.add(new Model_predikis(masalah,glcmfe.getContrast(),glcmfe.getHomogenity(),glcmfe.getEntropy(),glcmfe.getEnergy(),glcmfe.getDissimilarity()));


            System.out.println("Contrast: "+glcmfe.getContrast());
            System.out.println("Homogenity: "+glcmfe.getHomogenity());
            System.out.println("Entropy: "+glcmfe.getEntropy());
            System.out.println("Energy: "+glcmfe.getEnergy());
            System.out.println("Dissimilarity: "+glcmfe.getDissimilarity());
        } catch (IOException ex) {
            Log.e("Error", "onCreate: "+ex.getMessage() );
        }
    }



    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }


    public void haralickFeatures(Bitmap b) throws IOException {
        glcm.haralickDist=1;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream); // what 90 does ??
        GLCM.imageArray=new byte[]{};
        GLCM.imageArray = stream.toByteArray();
        glcm.process(b);
        glcm.data = new ArrayList<>(1);
        glcm.addData(glcm.features);
        List<double[]> featuresHar=glcm.getFeatures();

        String featureString="";
        for (double[] feature : featuresHar) {
            featureString= Arrays2.join(feature, ",", "%.5f");
        }
        String[] featureStr=featureString.split(Pattern.quote(","));
        float[] featureFlot = new float[featureStr.length];
        for (int i=0;i<featureStr.length;i++){
            featureFlot[i]=Float.parseFloat(featureStr[i]);
            System.out.println(featureFlot[i]);
        }
        //featureFlot is array that contain all 14 haralick features

    }

}
