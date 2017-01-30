/*
 * Copyright (c) 2016. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20.model;

import java.io.Serializable;

/**
 * Created by sazal on 2016-12-22.
 */

public class User implements Serializable {
    private static String id,userName,unique_id,emailAddress,createdOn,updatedOn,FirstName,LastName,FullAddress,bio,FavPet,FavMusic,ImageLink;
    private static  int loginType;

    public static int getLoginType() {
        return loginType;
    }

    public static void setLoginType(int loginType) {
        User.loginType = loginType;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        User.id = id;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        User.userName = userName;
    }

    public static String getUnique_id() {
        return unique_id;
    }

    public static void setUnique_id(String unique_id) {
        User.unique_id = unique_id;
    }

    public static String getEmailAddress() {
        return emailAddress;
    }

    public static void setEmailAddress(String emailAddress) {
        User.emailAddress = emailAddress;
    }

    public static String getCreatedOn() {
        return createdOn;
    }

    public static void setCreatedOn(String createdOn) {
        User.createdOn = createdOn;
    }

    public static String getUpdatedOn() {
        return updatedOn;
    }

    public static void setUpdatedOn(String updatedOn) {
        User.updatedOn = updatedOn;
    }

    public static String getFirstName() {
        return FirstName;
    }

    public static void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public static String getLastName() {
        return LastName;
    }

    public static void setLastName(String lastName) {
        LastName = lastName;
    }

    public static String getFullAddress() {
        return FullAddress;
    }

    public static void setFullAddress(String fullAddress) {
        FullAddress = fullAddress;
    }

    public static String getBio() {
        return bio;
    }

    public static void setBio(String bio) {
        User.bio = bio;
    }

    public static String getFavPet() {
        return FavPet;
    }

    public static void setFavPet(String favPet) {
        FavPet = favPet;
    }

    public static String getFavMusic() {
        return FavMusic;
    }

    public static void setFavMusic(String favMusic) {
        FavMusic = favMusic;
    }

    public static String getImageLink() {
        return ImageLink;
    }

    public static void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }
}
