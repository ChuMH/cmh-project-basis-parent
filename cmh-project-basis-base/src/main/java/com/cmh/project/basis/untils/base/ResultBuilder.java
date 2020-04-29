package com.cmh.project.basis.untils.base;

import com.cmh.project.basis.untils.base.constant.ResultCode;
import com.cmh.project.basis.untils.base.constant.SysResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @param <T>
 * @ClassName: ResultBuilder
 * @Description: 结果集构建类
 * @author: 初明昊
 * @date: 2020年04月18日
 */
@Data
@NoArgsConstructor //自动生成无参数构造器
@AllArgsConstructor //自动生成全参数构造器
public class ResultBuilder<T> implements Serializable {
    private static final long serialVersionUID = 7453965263707534560L;

    private boolean success;
    private Integer code;
    private String msg;
    private T data;

    private static <T> ResultBuilder<T> build(boolean success, int code, String msg, T data) {
        return new ResultBuilder<T>(success, code, msg, data);
    }

    public static <T> ResultBuilder<T> success(String msg, T data) {
        return build(true, SysResultCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ResultBuilder<T> success(T data) {
        return build(true, SysResultCode.SUCCESS.getCode(), SysResultCode.SUCCESS.getMsg(), data);
    }

    public static <T> ResultBuilder<T> success() {
        return success(null);
    }

    public static <T> ResultBuilder<T> failure(int code, String msg, T data) {
        return build(false, code, msg, data);
    }

    public static <T> ResultBuilder<T> failure(int code, String msg) {
        return build(false, code, msg, null);
    }

    public static <T> ResultBuilder<T> failure(String msg, T data) {
        return build(false, SysResultCode.FAIL.getCode(), msg, data);
    }

    public static <T> ResultBuilder<T> failure(T data) {
        return build(false, SysResultCode.FAIL.getCode(), SysResultCode.FAIL.getMsg(), data);
    }

    public static <T> ResultBuilder<T> failure() {
        return failure(SysResultCode.FAIL,null);
    }

    public static <T> ResultBuilder<T> failure(ResultCode resultCode, T data) {
        return build(false, resultCode.getCode(), resultCode.getMsg(), data);
    }

    public static <T> ResultBuilder<T> failure(ResultCode resultCode) {
        return build(false, resultCode.getCode(), resultCode.getMsg(), null);
    }
}
