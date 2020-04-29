package com.cmh.project.basis.untils.base.constant;

/**
 * @author 初明昊
 * @date 2020/04/18
 */
public interface ResultCode {

    /**
     * 默认成功状态码
     */
    Integer SUCCESS_CODE = new Integer(0);

    /**
     * 默认成功状态码描述
     */
    String SUCCESS_MSG = "成功";

    /**
     * 默认失败状态码
     */
    Integer FAIL_CODE = new Integer(-1);

    /**
     * 默认失败状态码描述
     */
    String FAIL_MSG = "失败";

    /**
     * 获取状态码
     *
     * @return
     */
    Integer getCode();

    /**
     * 获取状态码描述
     *
     * @return
     */
    String getMsg();
}
