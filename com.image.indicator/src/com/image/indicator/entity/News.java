package com.image.indicator.entity;

/**
 * ����ʵ����
 * @Description: ����ʵ����

 * @File: News.java

 * @Package com.image.indicator.entity

 * @Author Hanyonglu

 * @Date 2012-6-18 ����02:25:02

 * @Version V1.0
 */
public class News {
	// ID
	private int id;
	// ��Ҫ����
	private String simpleTitle;
	// ��������
	private String fullTitle;
	// ���ӵ�ַ
	private String newsUrl;
	// ��������
	private String newsContent;
	// �鿴����
	private int viewCount;
	// ���۴���
	private int commentCount;
	// �Ƿ񱻶���
	private boolean isReaded;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getSimpleTitle() {
		return simpleTitle;
	}
	
	public void setSimpleTitle(String simpleTitle) {
		this.simpleTitle = simpleTitle;
	}
	
	public String getFullTitle() {
		return fullTitle;
	}
	
	public void setFullTitle(String fullTitle) {
		this.fullTitle = fullTitle;
	}
	
	public String getNewsUrl() {
		return newsUrl;
	}
	
	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}
	
	public String getNewsContent() {
		return newsContent;
	}
	
	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}
	
	public int getViewCount() {
		return viewCount;
	}
	
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	
	public int getCommentCount() {
		return commentCount;
	}
	
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	
	public boolean isReaded() {
		return isReaded;
	}
	
	public void setReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}
}
