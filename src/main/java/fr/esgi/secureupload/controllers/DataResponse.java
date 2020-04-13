package fr.esgi.secureupload.controllers;

import lombok.Data;

@Data
public class DataResponse<T> {
    private T data;
    public DataResponse (T data){
        this.data = data;
    }
}
