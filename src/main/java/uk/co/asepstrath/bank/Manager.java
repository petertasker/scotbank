package uk.co.asepstrath.bank;

public class Manager {

    private String managerID;
    private String name;

    public Manager(String managerID, String name) {
        this.managerID = managerID;
        this.name = name;
    }

    public String getManagerID() {
        return managerID;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return String.format("id: %s%nname: %s",
                getManagerID(), getName());
    }
}
