package com.bhxx.xs_family.beans;

import java.io.Serializable;
import java.util.List;

public class CommonListBean<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<T> rows;
    /**
     * 成功与否
     */
    private Boolean success;

    /**
     * 消息
     */
    private String message;

    private String total;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
