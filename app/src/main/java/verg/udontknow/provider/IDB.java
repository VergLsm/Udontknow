package verg.udontknow.provider;

/**
 * 数据库接口
 * Created by verg on 16/4/30.
 */
public interface IDB {

    String[] ROWS_NAME = {
            "_id",
            "application",
            "userID",
            "userName",
            "passwd",
            "email",
            "phone",
            "binding",
            "webside",
            "remark"
    };

    int DBID = 0;
    int LABEL = 1;
    int USERID = 2;
    int USERNAME = 3;
    int PASSWORD = 4;
    int EMAIL = 5;
    int PHONE = 6;
    int BINDING = 7;
    int WEBSITE = 8;
    int REMARK = 9;


}
