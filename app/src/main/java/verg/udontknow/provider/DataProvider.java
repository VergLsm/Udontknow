package verg.udontknow.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import verg.udontknow.R;

/**
 * Created by verg on 15/8/23.
 * Email:Vision.lsm.2012@gmail.com
 */
public class DataProvider extends ContentProvider {

    /**
     * Constants to identify the requested operation
     */
    private static final int CUSTOMERS = 1;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(IProvider.PROVIDER_NAME, "customers", CUSTOMERS);
    }

    /**
     * This content provider does the database operations by this object
     */
    DBHelper mDBHelper;
    private String TAG = this.getClass().getName();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        assert context != null;
        mDBHelper = new DBHelper(getContext(), context.getString(R.string.app_name));
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == CUSTOMERS) {
            SQLiteDatabase db = mDBHelper.getReadableDatabase();
            return db.query(DBHelper.TABLENAME, projection, selection, selectionArgs, null, null, sortOrder);
        } else {
            return null;
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "Provider->insert:" + values.get(IDB.ROWS_NAME[IDB.LABEL]));
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.insert(DBHelper.TABLENAME, null, values);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "Provider->delete:" + selectionArgs[0]);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        return db.delete(DBHelper.TABLENAME, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "Provider->update:" + values.get(IDB.ROWS_NAME[IDB.LABEL]));
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        return db.update(DBHelper.TABLENAME, values, selection, selectionArgs);
    }
}
