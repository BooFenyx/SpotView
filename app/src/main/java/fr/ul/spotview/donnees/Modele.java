package fr.ul.spotview.donnees;

import java.util.ArrayList;

public class Modele {

    private ArrayList<Piece> listePiece;        //Une liste de pièce qui compose un modèle
    private String nom;                         //Le nom du modèle
    private String imagePath;                   //L'image qui illustre le modèle
    private String descriptif;                  //Le descriptif du modèle

    /**
     * Constructeur d'un modèle
     * @param nom Le nom du modèle
     * @param image Le chemin d'accès de l'image d'illustration
     * @param descriptif Le descriptif du modèle
     */
    public Modele(String nom, String image, String descriptif){
        this.listePiece = new ArrayList<>();
        this.nom = nom;
        this.imagePath = image;
        this.descriptif = descriptif;
    }

    /**
     * Fonction retournant le nom du modèle
     * @return Le nom du modèle
     */
    public String getNom() {
        return nom;
    }

    /**
     * Fonction modifiant le nom du modèle
     * @param nom Le nom du modèle
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Fonction retournant le chemin de l'image du modèle
     * @return Le chemin de l'image du modèle
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Fonction modifiant le chemin de l'image du modèle
     * @param imagePath Le chelin de l'image du modèle
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Fonction retournant le descriptif du modèle
     * @return Le descriptif
     */
    public String getDescriptif() {
        return descriptif;
    }

    /**
     * Fonction modifiant le descriptif du modèle
     * @param descriptif Le descriptif
     */
    public void setDescriptif(String descriptif) {
        this.descriptif = descriptif;
    }
}
