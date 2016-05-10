package verg.udontknow.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import verg.udontknow.R;
import verg.udontknow.entity.AccountEntity;
import verg.udontknow.provider.IDB;
import verg.udontknow.provider.IProvider;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = this.getClass().getName();
    private AccountEntity mAccountEntity;
    private TextInputEditText[] mTIETs = new TextInputEditText[9];

    private static int[] IDS = {R.id.edit_label,
            R.id.edit_id, R.id.edit_username, R.id.edit_password, R.id.edit_email,
            R.id.edit_phone, R.id.edit_binding, R.id.edit_website, R.id.edit_remark
    };
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        assert mFab != null;
        mFab.setOnClickListener(this);

        for (int i = 0; i < IDS.length; i++) {
            mTIETs[i] = (TextInputEditText) findViewById(IDS[i]);
        }

        mAccountEntity = getIntent().getParcelableExtra(AccountEntity.TAG);
        if (mAccountEntity == null) {
            setTitle("新建记录");
        } else {
            setTitle("编辑 " + mAccountEntity.get(AccountEntity.StringType.LABEL));
            for (int i = 0; i < mTIETs.length; i++) {
                mTIETs[i].setText(mAccountEntity.get(i));
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (checkHasInput()) {
            Snackbar.make(mFab, "是否丢弃修改？", Snackbar.LENGTH_LONG)
                    .setAction("是", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (checkHasInput()) {
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < mTIETs.length; i++) {
                contentValues.put(IDB.ROWS_NAME[i + 1], mTIETs[i].getText().toString());
            }

            Intent intent = new Intent();
            if (null == mAccountEntity) {
                //新建
                this.getContentResolver().insert(IProvider.CONTENT_URI, contentValues);
            } else {
                //修改
                // Defines selection criteria for the rows you want to update
                String mSelectionClause = IDB.ROWS_NAME[IDB.DBID] + "= ?";
                String[] mSelectionArgs = {String.valueOf(mAccountEntity._id)};

                int mRowsUpdated = getContentResolver().update(
                        IProvider.CONTENT_URI,   // the user dictionary content URI
                        contentValues,                       // the columns to update
                        mSelectionClause,                    // the column to select on
                        mSelectionArgs                      // the value to compare to
                );
                Log.d(TAG, "RowsUpdated:" + mRowsUpdated);
                for (int i = 0; i < mTIETs.length; i++) {
                    mAccountEntity.set(i, mTIETs[i].getText().toString());
                }
                intent.putExtra(AccountEntity.TAG, mAccountEntity);
            }
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    private boolean checkHasInput() {
        if (mAccountEntity == null) {
            //没有实体，新建模式
            for (TextInputEditText tiet : mTIETs) {
                if (!TextUtils.isEmpty(tiet.getText())) {
                    //如果有不为空的输入
                    return true;
                }
            }
        } else {
            //修改模式
            for (int i = 0; i < mTIETs.length; i++) {
                if (!TextUtils.equals(mTIETs[i].getText(), mAccountEntity.get(i))) {
                    //如果有不相同的
                    return true;
                }
            }
        }
        return false;
    }

}
