package entity;

public class Client {
    private int id;
    private String firstName;
    private String lastName;
    private String personalNumber;

    public Client(int id, String firstName, String lastName, String personalNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalNumber = personalNumber;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }
}
