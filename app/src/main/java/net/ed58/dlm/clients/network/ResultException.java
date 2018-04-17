package net.ed58.dlm.clients.network;

/**
 * Created by sunpeng on 17/3/21.
 * 定义的返回体异常
 */

public class ResultException extends RuntimeException {

    private int errCode = 0;

    public ResultException(int errCode, String msg) {
        super(msg);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }
}
