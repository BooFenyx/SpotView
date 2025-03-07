package fr.ul.spotview.donnees;

public class Acces {

    private Piece destination;      //La pièce de destination de l'accès
    private Photo photo;            //La photo associé à l'accès
    private float x;                //La position X
    private float y;                //La position Y
    private float largeur;          //La largeur de l'accès
    private float longueur;         //La longueur de l'accès

    /**
     * Constructeur de l'accès
     * @param photo La photo associé
     * @param destination La destination
     * @param x La position X
     * @param y La position Y
     * @param la La largeur
     * @param lo La longueur
     */
    public Acces(Photo photo, Piece destination, float x, float y, float la, float lo){
        this.photo = photo;
        this.destination = destination;
        this.x = x;
        this.y = y;
        this.largeur = la;
        this.longueur = lo;
    }

    /**
     * Fonction retournant la position X
     * @return X
     */
    public float getX() {
        return x;
    }

    /**
     * Fonction retournant la position Y
     * @return Y
     */
    public float getY() {
        return y;
    }

    /**
     * Fonction retournant la largeur
     * @return La largeur
     */
    public float getLargeur() {
        return largeur;
    }

    /**
     * Fonction retournant la hauteur
     * @return La hauteur
     */
    public float getLongueur() {
        return longueur;
    }
}
