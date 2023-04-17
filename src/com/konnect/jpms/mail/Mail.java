package com.konnect.jpms.mail;

import java.sql.Timestamp;

public class Mail {

	String mail_from;
	String mail_subject;
	String mail_id;
	Timestamp timestamp;
	String mail_body;
	String mail_drafts; 
	String mail_to = "";
	String mail_trash;
	String read_unread;

	public Mail(String mail_from, String mail_subject, Timestamp timestamp) {
		this.mail_from = mail_from;
		this.mail_subject = mail_subject;
		this.timestamp = timestamp;
	}

	public Mail(String mail_to) {
		this.mail_to = mail_to;
	}

	public Mail(String mail_id, String mail_from, String mail_subject, Timestamp timestamp, String mail_body, String mail_drafts, String mail_trash, String read_unread) {
		this.mail_from = mail_from;
		this.mail_subject = mail_subject;
		this.mail_id = mail_id;
		this.timestamp = timestamp;
		this.mail_body = mail_body;
		this.mail_drafts = mail_drafts;
		this.mail_trash = mail_trash;
		this.read_unread = read_unread;
	}

	public Mail(String mail_id, String mail_from, String mail_to, String mail_subject, Timestamp timestamp, String mail_body, String mail_drafts, String mail_trash) {
		this.mail_from = mail_from;
		this.mail_subject = mail_subject;
		this.mail_id = mail_id;
		this.timestamp = timestamp;
		this.mail_body = mail_body;
		this.mail_drafts = mail_drafts;
		this.mail_trash = mail_trash;
		this.mail_to = mail_to;
	}

	public Mail(String mail_id, String mail_from, String receivers, String mail_subject, Timestamp timestamp, String mail_body, String mail_drafts) {
		this.mail_from = mail_from;
		this.mail_subject = mail_subject;
		this.mail_id = mail_id;
		this.timestamp = timestamp;
		this.mail_body = mail_body;
		this.mail_drafts = mail_drafts;
		this.mail_to = receivers;
	}

	public String getMail_from() {
		return mail_from;
	}

	public String getMail_drafts() {
		return mail_drafts;
	}

	public String getMail_to() {
		return mail_to;
	}

	public void setMail_to(String mail_to) {
		this.mail_to = mail_to;
	}

	public void setMail_drafts(String mail_drafts) {
		this.mail_drafts = mail_drafts;
	}

	public void setMail_trash(String mail_trash) {
		this.mail_trash = mail_trash;
	}

	public String getMail_trash() {
		return mail_trash;
	}

	public String getMail_body() {
		return mail_body;
	}

	public String getMail_subject() {
		return mail_subject;
	}

	public String getMail_id() {
		return mail_id;
	}

	public Timestamp getTimestamp() {

		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setMail_from(String mail_from) {
		this.mail_from = mail_from;
	}

	public void setMail_subject(String mail_subject) {
		this.mail_subject = mail_subject;
	}

	public void setMail_body(String mail_body) {
		this.mail_body = mail_body;
	}

	public void setMail_id(String mail_id) {
		this.mail_id = mail_id;
	}

	public void setRead_unread(String read_unread) {
		this.read_unread = read_unread;
	}

	public String getRead_unread() {
		return read_unread;
	}

}
