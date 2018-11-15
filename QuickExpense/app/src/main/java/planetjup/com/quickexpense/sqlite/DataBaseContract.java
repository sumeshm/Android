package planetjup.com.quickexpense.sqlite;

import android.provider.BaseColumns;

public class DataBaseContract {

    public static class UserDetails implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT" +
                " )";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class ExpenseDetails implements BaseColumns {
        public static final String TABLE_NAME = "expense";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PAYER_ID = "payer_id";
        public static final String COLUMN_PAYEE_ID = "payee_id";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PAYER_ID + " INTEGER, " +
                COLUMN_PAYEE_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_PAYER_ID + ") REFERENCES " + UserDetails.TABLE_NAME + "(" + UserDetails._ID + "), " +
                "FOREIGN KEY(" + COLUMN_PAYEE_ID + ") REFERENCES " + UserDetails.TABLE_NAME + "(" + UserDetails._ID + ") " +
                " )";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class TripDetails implements BaseColumns {
        public static final String TABLE_NAME = "expense";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EXPENSE_ID = "expense_id";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_EXPENSE_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_EXPENSE_ID + ") REFERENCES " + ExpenseDetails.TABLE_NAME + "(" + ExpenseDetails._ID + ") " +
                " )";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
