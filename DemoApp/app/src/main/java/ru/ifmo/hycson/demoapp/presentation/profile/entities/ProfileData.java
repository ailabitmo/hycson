package ru.ifmo.hycson.demoapp.presentation.profile.entities;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.DisplayAppLink;

public class ProfileData {
    private String mId;
    private String mGivenName;
    private String mFamilyName;
    private String mImage;

    private List<AppLink> mAppLinks;
    private List<DisplayAppLink> mDisplayAppLinks;

    public String getId() {
        return mId;
    }

    public String getGivenName() {
        return mGivenName;
    }

    public String getFamilyName() {
        return mFamilyName;
    }

    public String getImage() {
        return mImage;
    }

    public List<AppLink> getAppLinks() {
        return mAppLinks;
    }

    public List<DisplayAppLink> getDisplayAppLinks() {
        return mDisplayAppLinks;
    }

    public ProfileData(ProfileData.Builder profileBuilder) {
        mId = profileBuilder.mId;
        mGivenName = profileBuilder.mGivenName;
        mFamilyName = profileBuilder.mFamilyName;
        mImage = profileBuilder.mImage;
        mAppLinks = profileBuilder.mAppLinks;
        mDisplayAppLinks = profileBuilder.mDisplayAppLinks;
    }

    public static final class Builder {
        private String mId;
        private String mGivenName;
        private String mFamilyName;
        private String mImage;

        private List<AppLink> mAppLinks;
        private List<DisplayAppLink> mDisplayAppLinks;

        public Builder setId(String id) {
            mId = id;
            return this;
        }

        public Builder setGivenName(String givenName) {
            mGivenName = givenName;
            return this;
        }

        public Builder setFamilyName(String familyName) {
            mFamilyName = familyName;
            return this;
        }

        public Builder setImage(String image) {
            mImage = image;
            return this;
        }

        public Builder setAppLinks(List<AppLink> appLinks) {
            mAppLinks = appLinks;
            return this;
        }

        public Builder setDisplayAppLinks(List<DisplayAppLink> displayAppLinks) {
            mDisplayAppLinks = displayAppLinks;
            return this;
        }

        public ProfileData build() {
            if (TextUtils.isEmpty(mId)) {
                throw new IllegalArgumentException("Person profile must have id ");
            }

            if (TextUtils.isEmpty(mGivenName)) {
                throw new IllegalArgumentException("Person profile must have givenName ");
            }

            if (TextUtils.isEmpty(mFamilyName)) {
                mFamilyName = "";
            }

            if (mAppLinks == null) {
                mAppLinks = new ArrayList<>();
            }

            if (mDisplayAppLinks == null) {
                mDisplayAppLinks = new ArrayList<>();
            }

            return new ProfileData(this);
        }
    }
}