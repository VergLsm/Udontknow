package verg.udontknow.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import verg.udontknow.entity.AccountEntity;
import verg.udontknow.provider.IDB;

/**
 * 列表加载器
 * Created by verg on 15/11/21.
 */
public class AccountListLoader extends AsyncTaskLoader<List<AccountEntity>> {

    private Uri mUri;
    private String[] mProjection;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mSortOrder;

    List<AccountEntity> mAccountEntities;

    public AccountListLoader(Context context, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
        super(context);
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }

    @Override
    public List<AccountEntity> loadInBackground() {
        Cursor cursor = getContext().getContentResolver().query(mUri, mProjection, mSelection,
                mSelectionArgs, mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            mAccountEntities = new ArrayList<>(cursor.getCount());
            AccountEntity accountEntity;
            do {
                accountEntity = new AccountEntity(cursor.getInt(cursor.getColumnIndex(IDB.ROWS_NAME[IDB.DBID])));
                for (int i = 1; i < IDB.ROWS_NAME.length; i++) {
                    accountEntity.set(i - 1, cursor.getString(cursor.getColumnIndex(IDB.ROWS_NAME[i])));
                }
                mAccountEntities.add(accountEntity);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return mAccountEntities;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
