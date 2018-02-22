package dangxia.com.utils.http;

import java.io.IOException;

/**
 * Created by zhuang_ge on 2017/11/13.
 */

public class ServerException extends IOException {
    public ServerException(String message) {
        super(message);
    }
}
