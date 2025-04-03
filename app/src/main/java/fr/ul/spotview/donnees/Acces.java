package fr.ul.spotview.donnees;

import java.io.Serializable;

public class Acces implements Serializable {

    private Piece destination;      //La pièce de destination de l'accès
    private float x;                //La position X
    private float y;                //La position Y
    private float largeur;          //La largeur de l'accès
    private float hauteur;         //La longueur de l'accès


    // Constructeur par défaut pour Gson
    public Acces() {
    }

    /**
     * Constructeur de l'accès
     * @param destination La destination
     * @param x La position X
     * @param y La position Y
     * @param la La largeur
     * @param ha La hauteur
     */
    public Acces(Piece destination, float x, float y, float la, float ha){
        this.destination = destination;
        this.x = x;
        this.y = y;
        this.largeur = la;
        this.hauteur = ha;
    }

    /**
     * Fonction retournant la position X
     * @return X
     */
    public float getX() {
        return x;
    }

    /**
     * Fonction modifiant la position x
     * @param x
     */
    public void setX(float x){
        this.x = x;
    }

    /**
     * Fonction retournant la position Y
     * @return Y
     */
    public float getY() {
        return y;
    }

    /**
     * FOncion modifiant la position y
     * @param y
     */
    public void setY(float y){
        this.y = y;
    }

    /**
     * Fonction retournant la largeur
     * @return La largeur
     */
    public float getLargeur() {
        return largeur;
    }

    /**
     * Fonction modifiant la largeur
     * @param largeur
     */
    public void setLargeur(float largeur) {
        this.largeur = largeur;
    }

    /**
     * Fonction retournant la hauteur
     * @return La hauteur
     */
    public float getHauteur() {
        return hauteur;
    }

    /**
     * Fonction modifiant la hauteur
     * @param hauteur
     */
    public void setHauteur(float hauteur) {
        this.hauteur = hauteur;
    }

    /**
     * Fonction qui mets en place la pièce de destination
     * @param destination
     */
    public void setDestination(Piece destination){
        this.destination = destination;
    }

    /**
     * Fonction retournant la pièce de destination
     * @return La destination
     */
    public Piece getDestination(){
        return this.destination;
    }

    public String toString(){
        return this.destination.toString();
    }
}
