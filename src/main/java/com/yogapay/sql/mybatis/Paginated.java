package com.yogapay.sql.mybatis;

import java.io.Serializable;

/**
 * 分页用对象.
 *
 * @version 1.00 2011-6-01
 */
public class Paginated<T> implements Serializable {

    /**
     * 序列号 *
     */
    private static final long serialVersionUID = 20110620L;
    /**
     * 页数 *
     */
    private int page;
    /**
     * 每页数量 *
     */
    private int eachPageNum;
    /**
     * 总数量 *
     */
    private int count;
    /**
     * 具体数据 *
     */
    private T data;

    public Paginated() {
    }

    public Paginated(int page, int eachPageNum) {
        this.page = page;
        this.eachPageNum = eachPageNum;
    }

    public void input(int page, int eachPageNum, int count) {
        this.count = count;
        this.eachPageNum = eachPageNum;
        if (eachPageNum > 0) {
            int max = (count + eachPageNum - 1) / eachPageNum;
            page = page < 1 ? 1 : page;
            page = page > max ? max : page;
            this.page = page;
        }
    }

    public void input(int maxNum) {
        this.maxNum = maxNum;
    }

    public boolean isEmpty() {
        return page <= 0;
    }

    public boolean isHasNext() {
        return page < getTotalPage();
    }

    public boolean isHasPrevious() {
        return page > 1;
    }
    private int maxNum = -1;

    public int getTotalPage() {
        int a = (count + eachPageNum - 1) / eachPageNum;
        if (maxNum <= 0) {
            return a;
        }
        int b = (maxNum + eachPageNum - 1) / eachPageNum;
        return a <= b ? a : b;
    }

    public int getCount() {
        return count;
    }

    public T getData() {
        return data;
    }

    public int getEachPageNum() {
        return eachPageNum;
    }

    public int getPage() {
        return page;
    }

    public void setData(T data) {
        this.data = data;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Paginated [page=" + page + ", eachPageNum=" + eachPageNum + ", count=" + count + ", data=" + data + "]";
    }
}
