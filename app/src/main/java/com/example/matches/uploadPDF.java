package com.example.matches;

/**
 * Class uploadPDF
 */
public class uploadPDF {
    public String name, url;

    /**
     * Constructeur de la classe
     *
     * @param name
     * @param url
     */
    public uploadPDF(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * @return le nom du pdf
     */
    public String getName() {
        return name;
    }
}
