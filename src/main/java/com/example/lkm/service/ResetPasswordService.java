package com.example.lkm.service;

public interface ResetPasswordService {


    public void sendResetEmail(String email);

    public void updatePassword(String token, String newPassword);

    public void validateResetToken(String token);


}
