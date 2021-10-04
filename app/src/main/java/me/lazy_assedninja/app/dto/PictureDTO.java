package me.lazy_assedninja.app.dto;

import okhttp3.MultipartBody;

public class PictureDTO {

    private MultipartBody.Part file;

    public PictureDTO(MultipartBody.Part file) {
        this.file = file;
    }

    public MultipartBody.Part getFile() {
        return file;
    }

    public void setFile(MultipartBody.Part file) {
        this.file = file;
    }
}