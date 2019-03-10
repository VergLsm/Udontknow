package verg.udontknow.provider;

import android.net.Uri;

/**
 * 数据提供接口
 * Created by verg on 16/4/30.
 */
public interface IProvider {
    String PROVIDER_NAME = "verg.udontknow.customer";

    /**
     * A uri to do operations on cust_master table. A content provider is identified by its uri
     */
    Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/customers");

    String PWD = "pwd";
}
