package verg.udontknow.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 账号实体
 * Created by verg on 15/11/21.
 */
public class AccountEntity implements Parcelable {

    public static final String TAG = "AccountEntity";
    public final int _id;

    public @interface StringType {
        int LABEL = 0;
        int USERID = 1;
        int USERNAME = 2;
        int PASSWORD = 3;
        int EMAIL = 4;
        int PHONE = 5;
        int BINDING = 6;
        int WEBSITE = 7;
        int REMARK = 8;
    }

    private final String[] mFields = new String[9];

    public AccountEntity(int id) {
        _id = id;
    }

    protected AccountEntity(Parcel in) {
        _id = in.readInt();
        for (int i = 0; i < mFields.length; i++) {
            mFields[i] = in.readString();
        }
    }

    public static final Creator<AccountEntity> CREATOR = new Creator<AccountEntity>() {
        @Override
        public AccountEntity createFromParcel(Parcel in) {
            return new AccountEntity(in);
        }

        @Override
        public AccountEntity[] newArray(int size) {
            return new AccountEntity[size];
        }
    };

    public String get(@StringType int type) {
        return mFields[type] != null ? mFields[type] : "";
    }

    public void set(@StringType int type, String string) {
        mFields[type] = string;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        for (String s : mFields) {
            dest.writeString(s);
        }
    }

}
