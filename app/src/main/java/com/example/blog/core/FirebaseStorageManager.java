package com.example.blog.core;

import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.UUID;

public class FirebaseStorageManager {

    public static void uploadImageToFirebaseStorage(Uri imageUri,
                                                    Map<String, Object> postMap,
                                                    StorageReference storageReference,
                                                    String folderPath,
                                                    String fileName,
                                                    OnSuccessListener onSuccessListener,
                                                    OnFailureListener onFailureListener) {
        if (storageReference == null) {
            storageReference = FirebaseStorage.getInstance().getReference();
        }

        StorageReference finalStorageReference = storageReference;
        storageReference.child(folderPath).child(fileName).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    finalStorageReference.child(folderPath).child(fileName).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                postMap.put("urlImage", uri.toString());
                                onSuccessListener.onSuccess();
                            });
                })
                .addOnFailureListener(e -> {
                    onFailureListener.onFailure();
                });
    }

    public interface OnSuccessListener {
        void onSuccess();
    }

    public interface OnFailureListener {
        void onFailure();
    }
}
