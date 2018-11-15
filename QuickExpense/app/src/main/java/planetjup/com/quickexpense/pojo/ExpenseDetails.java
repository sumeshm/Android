package planetjup.com.quickexpense.pojo;

import java.util.ArrayList;

/**
 * This class represents one trip expense
 * Created by Sumesh Mani on 10/14/18.
 */

public class ExpenseDetails {
    private final String expenseName;
    private final PayerDetails payerDetails;
    private final ArrayList<PayeeDetails> payeeList;

    public ExpenseDetails(String expenseName, PayerDetails payerDetails, ArrayList<PayeeDetails> payeeList) {
        this.expenseName = expenseName;
        this.payerDetails = payerDetails;
        this.payeeList = payeeList;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public PayerDetails getPayerDetails() {
        return payerDetails;
    }

    public ArrayList<PayeeDetails> getPayeeDetails() {
        return payeeList;
    }
}
