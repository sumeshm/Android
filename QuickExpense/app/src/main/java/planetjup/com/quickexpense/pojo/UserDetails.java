package planetjup.com.quickexpense.pojo;

/**
 * This class represents one user
 * Created by Sumesh Mani on 10/14/18.
 */

public class UserDetails {
    private final String firstName;
    private final String lastName;
    private double amount;

    public UserDetails(String expenseName, String lastName) {
        this.firstName = expenseName;
        this.lastName = lastName;
    }

    public String getFullName() { return firstName + " " + lastName; }

    public String getFirstName() { return firstName; }

    public String getLastName() {
        return lastName;
    }

    public void updateAmount(double amount) {
        this.amount += amount;
    }

    public double getAmount() {
        return amount;
    }
}
