package com.ljtao3.beans;

public class PageQuery {
    private int pageSize=10;
    private int pageNo=1;
    private int offset;
    public int getOffset() {
        return offset;
    }

    public void setOffset() {
        this.offset = (pageNo-1) * pageSize;
    }
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }


}
