package planetjup.com.quickexpense.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReadWriteUtil extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TAG = ReadWriteUtil.class.getSimpleName();

    private static final String DATABASE_NAME = "quickexpense_db";

    public ReadWriteUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(TAG, "onCreate()");
        sqLiteDatabase.execSQL(DataBaseContract.UserDetails.CREATE_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.ExpenseDetails.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.v(TAG, "onUpgrade()");

        sqLiteDatabase.execSQL(DataBaseContract.UserDetails.DROP_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.ExpenseDetails.DROP_TABLE);

        onCreate(sqLiteDatabase);
    }


//    private void saveToDB() {
//        SQLiteDatabase database = new SampleDBSQLiteHelper(this).getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(SampleDBContract.Employer.COLUMN_NAME, binding.nameEditText.getText().toString());
//        values.put(SampleDBContract.Employer.COLUMN_DESCRIPTION, binding.descEditText.getText().toString());
//
//        try {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
//                    binding.foundedEditText.getText().toString()));
//            long date = calendar.getTimeInMillis();
//            values.put(SampleDBContract.Employer.COLUMN_FOUNDED_DATE, date);
//        }
//        catch (Exception e) {
//            Log.e(TAG, "Error", e);
//            Toast.makeText(this, "Date is in the wrong format", Toast.LENGTH_LONG).show();
//            return;
//        }
//        long newRowId = database.insert(SampleDBContract.Employer.TABLE_NAME, null, values);
//
//        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
//    }
//
//    private void readFromDB() {
//        String name = binding.nameEditText.getText().toString();
//        String desc = binding.descEditText.getText().toString();
//        long date = 0;
//
//        try {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
//                    binding.foundedEditText.getText().toString()));
//            date = calendar.getTimeInMillis();
//        }
//        catch (Exception e) {}
//
//        SQLiteDatabase database = new SampleDBSQLiteHelper(this).getReadableDatabase();
//
//        String[] projection = {
//                SampleDBContract.Employer._ID,
//                SampleDBContract.Employer.COLUMN_NAME,
//                SampleDBContract.Employer.COLUMN_DESCRIPTION,
//                SampleDBContract.Employer.COLUMN_FOUNDED_DATE
//        };
//
//        String selection =
//                SampleDBContract.Employer.COLUMN_NAME + " like ? and " +
//                        SampleDBContract.Employer.COLUMN_FOUNDED_DATE + " > ? and " +
//                        SampleDBContract.Employer.COLUMN_DESCRIPTION + " like ?";
//
//        String[] selectionArgs = {"%" + name + "%", date + "", "%" + desc + "%"};
//
//        Cursor cursor = database.query(
//                SampleDBContract.Employer.TABLE_NAME,     // The table to query
//                projection,                               // The columns to return
//                selection,                                // The columns for the WHERE clause
//                selectionArgs,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                null                                      // don't sort
//        );
//
//        Log.d(TAG, "The total cursor count is " + cursor.getCount());
//        binding.recycleView.setAdapter(new SampleRecyclerViewCursorAdapter(this, cursor));
//    }
}
