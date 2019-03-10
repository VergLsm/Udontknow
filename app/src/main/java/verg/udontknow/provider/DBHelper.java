package verg.udontknow.provider;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by verg on 15/8/23.
 * Email:Vision.lsm.2012@gmail.com
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath();

    /**
     * Database name
     */
    public static String DBNAME = DATABASE_PATH + File.separator + "%1$s" + File.separator + "%1$s" + ".db";

    public static final String TABLENAME = "UP";

    /**
     * Version number of the database
     */
    private static int VERSION = 1;

    private String TAG = this.getClass().getName();

    public DBHelper(Context context, String appName) {
        super(context, DBNAME = String.format(DBNAME, appName), null, VERSION);
        Log.d(TAG, DBNAME);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "onCreate()");
        Log.d(TAG, DBNAME);
        db.execSQL("create table " + TABLENAME + " (" +
                "_id integer primary key autoincrement, " +
                "application text, " +
                "userID test, " +
                "userName text, " +
                "passwd text, " +
                "email text, " +
                "phone text, " +
                "binding text, " +
                "webside text, " +
                "remark text" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("DBHelper", "onUpgrade()");
    }

}
