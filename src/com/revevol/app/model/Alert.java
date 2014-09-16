package com.revevol.app.model;

import java.util.ArrayList;
import java.util.Date;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindex;

@Entity
public class Alert {
	@Id Long id;
	
    String title;
    String description;
    @Index Long creationDate;
    @Index String creationUser;
    Double latitude;
    Double longitude;
    @Index String state;
    @Index String adminRisk;
    @Index String photo;
    /**
     * This is the photo after the final solution made
     */
    String photoAfter;
    @Index boolean active;
    @Index String address;
    @Index String moreInformations;
    
    /**
     * Operation by the administrator
     */
    @Index boolean deleted;
    @Index boolean enabled;
    @Index boolean importedFromExcel;
    String userInformationsImported;

	public Alert() {}
    
    public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAdminRisk() {
		return adminRisk;
	}

	public void setAdminRisk(String adminRisk) {
		this.adminRisk = adminRisk;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMoreInformations() {
		return moreInformations;
	}

	public void setMoreInformations(String moreInformations) {
		this.moreInformations = moreInformations;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isImportedFromExcel() {
		return importedFromExcel;
	}

	public void setImportedFromExcel(boolean importedFromExcel) {
		this.importedFromExcel = importedFromExcel;
	}

	public String getUserInformationsImported() {
		return userInformationsImported;
	}

	public void setUserInformationsImported(String userInformationsImported) {
		this.userInformationsImported = userInformationsImported;
	}

	public String getPhotoAfter() {
		return photoAfter;
	}

	public void setPhotoAfter(String photoAfter) {
		this.photoAfter = photoAfter;
	}
	
	
	
}