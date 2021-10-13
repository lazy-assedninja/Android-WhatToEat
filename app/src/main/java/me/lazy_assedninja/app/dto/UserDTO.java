package me.lazy_assedninja.app.dto;

public class UserDTO {

    private String email;
    private String password;
    private String googleID;
    private String oldPassword;
    private String newPassword;
    private String headPortrait;
    private String verificationCode;

    private boolean isGoogleLogin;

    public UserDTO(String email) {
        this.email = email;
    }

    public UserDTO(String emailOrGoogleID, String password, boolean isGoogleLogin) {
        if (isGoogleLogin) {
            this.googleID = emailOrGoogleID;
        } else {
            this.email = emailOrGoogleID;
        }
        this.password = password;
        this.isGoogleLogin = isGoogleLogin;
    }

    public UserDTO(String email, String oldPasswordOrVerificationCode, String newPassword, boolean isReset) {
        this.email = email;
        if (isReset) {
            this.oldPassword = oldPasswordOrVerificationCode;
        } else {
            this.verificationCode = oldPasswordOrVerificationCode;
        }
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public boolean isGoogleLogin() {
        return isGoogleLogin;
    }

    public void setGoogleLogin(boolean googleLogin) {
        isGoogleLogin = googleLogin;
    }
}