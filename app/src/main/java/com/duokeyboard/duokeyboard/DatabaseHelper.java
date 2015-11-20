package com.duokeyboard.duokeyboard;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //
    private Preference mSubtypeEnablerPreference;

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.duokeyboard.duokeyboard/databases/";
    private static String DB_NAME = "DuoKeyboard.db";

    public static String TABLE_ENGLISH_TO_FOREIGN = "Kannada";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_MEANING = "meaning";
    public static final String COLUMN_FAMILIAR_SENTENCE = "familiar_sentence";
    public static final String COLUMN_UNKNOWN_SENTENCE = "unknown_sentence";
    private String[] allColumns = { DatabaseHelper.COLUMN_WORD,
            DatabaseHelper.COLUMN_MEANING,
            DatabaseHelper.COLUMN_FAMILIAR_SENTENCE,
            DatabaseHelper.COLUMN_UNKNOWN_SENTENCE
    };

    private SQLiteDatabase myDataBase;

    private Context myContext;

    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;

        DB_PATH = context.getFilesDir().getPath();
        Log.e("DUOKEYB", DB_PATH);

        String currentLanguage;
        mSubtypeEnablerPreference = new Preference(context);
        if(mSubtypeEnablerPreference.getSharedPreferences() != null)
        {
            currentLanguage = mSubtypeEnablerPreference.getSharedPreferences().getString("current_language", "Kannada");
        }
        else
        {
            currentLanguage = "Kannada";
        }

        TABLE_ENGLISH_TO_FOREIGN = currentLanguage;
        Log.e(TABLE_ENGLISH_TO_FOREIGN, currentLanguage);
    }

    private static InputMethodInfo getMyImi(Context context, InputMethodManager imm) {
        final List<InputMethodInfo> imis = imm.getInputMethodList();
        for (int i = 0; i < imis.size(); ++i) {
            final InputMethodInfo imi = imis.get(i);
            if (imis.get(i).getPackageName().equals(context.getPackageName())) {
                return imi;
            }
        }
        return null;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
            //ToDo This is temporary. Remove this later
            copyDataBase();
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database does't exist yet.
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public WordModel getForeignWord(String word) {
        WordModel foreignWord = null;
        String kword = word.toLowerCase();
        Cursor cursor = myDataBase.query(DatabaseHelper.TABLE_ENGLISH_TO_FOREIGN,
                allColumns, DatabaseHelper.COLUMN_WORD + " = " + "\"" + kword + "\"", null,
                null, null, null);
        cursor.moveToFirst();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            foreignWord = new WordModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return foreignWord;
    }

}