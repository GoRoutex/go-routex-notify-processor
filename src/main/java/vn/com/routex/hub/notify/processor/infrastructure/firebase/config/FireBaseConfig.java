package vn.com.routex.hub.notify.processor.infrastructure.firebase.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FireBaseConfig {

    @Bean
    public FirebaseApp firebaseApp(
            @Value("${spring.fcm.service-account-path}") String serviceAccountPath,
            @Value("${spring.fcm.project-id}") String projectId
    ) throws IOException {

        GoogleCredentials googleCredentials;
        try (FileInputStream fileInputStream = new FileInputStream(serviceAccountPath)) {
            googleCredentials = GoogleCredentials.fromStream(fileInputStream);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .setProjectId(projectId)
                .build();

        if(FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
