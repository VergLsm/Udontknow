package verg.udontknow.activities;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import verg.udontknow.R;
import verg.udontknow.entity.AccountEntity;

public class DetailActivity extends AppCompatActivity {

    public static final int REQUESTCODE = 12301;
    private AccountEntity mAccountEntity;
    private TextView[] mTVs = new TextView[8];
    private static int[] IDS = {
            R.id.detail_id, R.id.detail_username, R.id.detail_password, R.id.detail_email,
            R.id.detail_phone, R.id.detail_binding, R.id.detail_website, R.id.detail_remark
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
                intent.putExtra(AccountEntity.TAG, mAccountEntity);
                startActivityForResult(intent, REQUESTCODE);
            }
        });

        mAccountEntity = getIntent().getParcelableExtra(AccountEntity.TAG);

        setTitle(mAccountEntity.get(AccountEntity.StringType.LABEL));

        for (int i = 0; i < mTVs.length; i++) {
            mTVs[i] = (TextView) findViewById(IDS[i]);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE && resultCode == RESULT_OK && data != null) {
            mAccountEntity = data.getParcelableExtra(AccountEntity.TAG);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (int i = 0; i < mTVs.length; i++) {
            mTVs[i].setText(mAccountEntity.get(i + 1));
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
}
