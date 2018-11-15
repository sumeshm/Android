package planetjup.com.quickexpense.pojo;

/**
 * This class represents expense payer's details
 * Created by Sumesh Mani on 10/14/18.
 */

public class PayeeDetails extends UserDetails {

    private final float shareFactor;

    public PayeeDetails(String expenseName, String lastName, float shareFactor) {
        super(expenseName, lastName);
        this.shareFactor = shareFactor;
    }

    public float getShareFactor() {
        return shareFactor;
    }
}
