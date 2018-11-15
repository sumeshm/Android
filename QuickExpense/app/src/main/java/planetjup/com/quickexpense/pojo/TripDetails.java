package planetjup.com.quickexpense.pojo;

import java.util.ArrayList;

/**
 * This class represents one trip
 * Created by Sumesh Mani on 10/14/18.
 */

public class TripDetails {
    private final String tripName;
    private ArrayList<ExpenseDetails> expenseList = new ArrayList<>();

    public TripDetails(String tripName) {
        this.tripName = tripName;
    }

    public String getTripName() {
        return tripName;
    }

    public void addExpense(ExpenseDetails expense) {
        expenseList.add(expense);
    }
    public ArrayList<ExpenseDetails> getExpenseList() {
        return expenseList;
    }
}
