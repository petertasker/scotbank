package uk.co.asepstrath.bank;

public class Business {

    private String id;
    private String name;
    private String category;
    private boolean sanctioned;

    public Business(String id, String name, String category, boolean sanctioned) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.sanctioned = sanctioned;
    }

    public String getID() {
        return id;
    }

    public String getName(){
        return name;
    }

    public String getCategory(){
        return category;
    }

    public boolean isSanctioned(){
        return sanctioned;
    }
}
