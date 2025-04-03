package fr.ul.spotview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SingletonOpenFolder{

    static final SingletonOpenFolder instance = new SingletonOpenFolder();

    private SingletonOpenFolder(){

    }

    public static SingletonOpenFolder getInstance() {
        return instance;
    }

    public void openFolder(Context context, String modeleName){
        // Par exemple, on reconstruit le nom du dossier à partir du nom du modèle et de la date utilisée lors de la sauvegarde
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String folderName = modeleName + "_" + currentDate;
        File folder = new File(context.getExternalFilesDir(null), folderName);

        if (folder.exists()) {
            // Créez l'URI à partir du dossier. Attention : pour Android N et plus, il est recommandé d'utiliser un FileProvider pour éviter FileUriExposedException.
            Uri folderUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    folder
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(folderUri, "resource/folder");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Aucun explorateur de fichiers trouvé", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Le dossier sauvegardé n'existe pas", Toast.LENGTH_SHORT).show();
        }
    }
}
