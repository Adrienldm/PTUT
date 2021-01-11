package com.example.matches;

public class ChatObject {
    private String message;
    private Boolean currentUser;

    /**
     * C'est la structure qui contient le message ainsi que son destinataire.
     */
    public ChatObject(String message, Boolean currentUser) {
        this.message = message;
        this.currentUser = currentUser;
    }

    public String getMessage() {
        return message;
    }


    public Boolean getCurrentUser() {
        return currentUser;
    }

}