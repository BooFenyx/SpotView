package fr.ul.spotview.donnees;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {

    public enum Orientation {
        NORD,
        SUD,
        EST,
        OUEST;
    }

    private ArrayList<Acces> listeAcces;    //La liste des accès de la photo
    private String cheminImage;             //Le chemin de l'image
    private Orientation orientation;        //L'orientation de l'image

    // Constructeur par défaut pour Gson
    public Photo() {
        this.listeAcces = new ArrayList<>();
        this.cheminImage = "";
        this.orientation = Orientation.NORD; // valeur par défaut
    }

    /**
     * Constructeur de la photo
     * @param o L'orientation
     * @param image Le chemin de l'image
     */
    public Photo(Orientation o, String image) {
        this();
        this.orientation = o;
        this.cheminImage = image;
    }

    /**
     * Fonction retournant le chemin de l'image
     * @return Le chemin de l'image
     */
    public String getCheminImage() {
        return cheminImage;
    }

    /**
     * Fonction modifiant le chemin de l'image
     * @param chemin
     */
    public void setCheminImage(String chemin){
        this.cheminImage = chemin;
    }

    /**
     * Fonction retournant l'orientation de l'image
     * @return L'orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Fonction modifiant l'orientation de la photo
     * @param ori
     */
    public void setOrientation(Photo.Orientation ori){
        this.orientation = ori;
    }

    /**
     * Fonction qui modifie la liste d'accès
     * @param l
     */
    public void setListeAcces(ArrayList<Acces> l){
        this.listeAcces = l;
    }

    /**
     * Fonction retournant la liste d'accès
     * @return
     */
    public ArrayList<Acces> getListeAcces(){
        return listeAcces;
    }
}
