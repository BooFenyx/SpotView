package fr.ul.spotview;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.ul.spotview.donnees.Modele;
import fr.ul.spotview.donnees.Photo;
import fr.ul.spotview.donnees.Piece;

public class GestionPhoto extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private ArrayList<Modele> listeModele = new ArrayList<>();  //La liste des modèle
    private Photo.Orientation ori;      //L'orientation de la photo en cours
    private Modele modele;              //Le modèle
    private int posM;                   //La position du modèle
    private Piece pSelect;              //La pièce
    private ActivityResultLauncher<Intent> launcher;
    private Uri uriImage;
    private ImageView gph_image;
    private File imageFile;


    /* Gestion de la bousolle */
    private ImageView gpp_boussole;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    // Variables pour stocker les données des capteurs
    private float[] gravity;
    private float[] geomagnetic;
    private float currentDegree = 0f;
    private final float ALPHA = 0.2f; // Coefficient de lissage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Gestion des variables utiles
        gph_image = findViewById(R.id.gph_image);

        //Gestion des écouteurs
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.gph_takeB).setOnClickListener(this);


        //Récupération de la liste de modèle et de la position
        Bundle extras = getIntent().getExtras();
        this.listeModele = (ArrayList<Modele>) extras.get("list");
        this.posM = (int) extras.get("pos");
        this.modele = this.listeModele.get(posM);
        this.pSelect = (Piece) extras.get("piece");
        this.ori = (Photo.Orientation) extras.get("ori");
        setTextOri(ori);

        /* Gestion de la boussole */

        this.gpp_boussole = findViewById(R.id.gpp_boussole);

        // Obtenir le SensorManager et les capteurs
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enregistrer les écouteurs des capteurs
        if(accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        if(magnetometer != null)
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Désenregistrer les écouteurs pour économiser la batterie
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            Intent intentGPP = new Intent(this, GestionPhotosPiece.class);
            this.listeModele.set(this.posM, this.modele);
            intentGPP.putExtra("list", this.listeModele);
            intentGPP.putExtra("pos", this.posM);
            intentGPP.putExtra("piece", this.pSelect);
            startActivity(intentGPP);
        }
        if(v.getId() == R.id.gph_takeB){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        }
    }

    public void setTextOri(Photo.Orientation ori){
        TextView textViewOri = findViewById(R.id.textViewOri);
        switch (ori){
            case NORD:
                textViewOri.setText("N");
                break;
            case SUD:
                textViewOri.setText("S");
                break;
            case EST:
                textViewOri.setText("E");
                break;
            case OUEST:
                textViewOri.setText("W");
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = lowPassFilter(event.values.clone(), gravity);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = lowPassFilter(event.values.clone(), geomagnetic);
        }
        if (gravity != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Orientation[0] correspond à l'azimut en radians
                float azimuthInRadians = orientation[0];
                float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
                azimuthInDegrees = (azimuthInDegrees + 360) % 360;

                // Animation de rotation pour l'imageView
                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        -azimuthInDegrees,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                ra.setDuration(250);
                ra.setFillAfter(true);
                gpp_boussole.startAnimation(ra);
                currentDegree = -azimuthInDegrees;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Méthode de filtre passe-bas pour lisser les données du capteur
    private float[] lowPassFilter(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    private void checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                imageFile = createImageFile();
                if (imageFile != null) {
                    uriImage = FileProvider.getUriForFile(this, "com.example.cameracapture.provider", imageFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
                    cameraLauncher.launch(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return new File(storageDir, imageFileName);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && uriImage != null) {
                    displayImage();
                }
            }
    );

    private void displayImage() {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uriImage);
            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
            gph_image.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }
}