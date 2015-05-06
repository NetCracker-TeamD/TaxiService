package com.teamd.taxi.models;

public class PagingLink {
    private int pageNum;
    private String content;
    private String href;
    private boolean active;
    private boolean disabled;

    public PagingLink() {
    }

    public PagingLink(String content, String href, boolean active, boolean disabled, int pageNum) {
        this.content = content;
        this.href = href;
        this.active = active;
        this.disabled = disabled;
        this.pageNum = pageNum;
    }

    public String getContent() {
        return content;
    }

    public String getHref() {
        return href;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getPageNum() {
        return pageNum;
    }

    @Override
    public String toString() {
        return "PagingLink{" +
                "content='" + content + '\'' +
                ", href='" + href + '\'' +
                ", active=" + active +
                ", disabled=" + disabled +
                '}';
    }
}
