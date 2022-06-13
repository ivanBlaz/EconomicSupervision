package com.devivan.economicsupervision.System;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.paypal.android.sdk.ca;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class SystemAuth {

    public PhoneAuthProvider.ForceResendingToken forceResendingToken;

    public String verificationId;

    private final FirebaseAuth firebaseAuth;

    // Constructor variables
    String phoneNumber;
    Activity activity;
    Callable<Void> onSuccess;
    Callable<Void> onFailure;
    /////////////////////////

    public SystemAuth(String phoneNumber, Activity activity, Callable<Void> onSuccess, Callable<Void> onFailure) {
        this.phoneNumber = phoneNumber;
        this.activity = activity;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void sendVerificationCode(PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void resendVerificationCode(PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(callbacks)
                        .setForceResendingToken(forceResendingToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyPhoneNumberWithVerificationCode(String verificationCode) {
        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationId, verificationCode));
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> { try { onSuccess.call(); } catch (Exception ignored) { } })
                .addOnFailureListener(e -> { try { onFailure.call(); } catch (Exception ignored) { } });
    }
}
