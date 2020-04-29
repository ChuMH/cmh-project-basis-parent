package com.cmh.project.basis.untils.base.constant;

/**
 * 系统级别的异常码表
 * 以"SYS_"开头
 *
 * @author 初明昊
 */
public enum SysResultCode implements ResultCode {
    // 成功
    SUCCESS(0, "成功"),
    // 失败
    FAIL(-1, "失败"),
    // 系统错误
    SYS_SERVER_ERROR(1000500, "服务器内部错误"),
    // 参数错误
    SYS_BAD_REQUEST(1000400, "请求格式错误,参数错误"),
    // 未授权
    SYS_UNAUTHORIZED(1000401, "未授权"),
    // 未找到
    SYS_NOT_FOUND(1000404, "请求的资源不存在,数据不存在"),
    // 资源冲突
    SYS_CONFLICT(1000409, "资源存在冲突,数据已存在");

    private Integer code;
    private String msg;

    SysResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    public static SysResultCode valueOf(Integer value) {
        for (SysResultCode obj : values()) {
            if (value.equals(obj.getCode())) {
                return obj;
            }
        }
        throw new RuntimeException("Undefined type " + value);
    }
}
