package com.ec.pojo.vo;

import java.util.List;

/** 分页结果（替代 Spring Data Page，统一 MyBatis 架构） */
public class PageResult<T> {
    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResult(List<T> content, int page, int size, long total) {
        this.content = content;
        this.number = page;
        this.size = size;
        this.totalElements = total;
        this.totalPages = size <= 0 ? 0 : (int) Math.ceil((double) total / size);
    }

    public List<T> getContent() { return content; }
    public int getNumber() { return number; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}
