package com.junwoo.hamkke.domain.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 13.
 */
@Slf4j
@Profile("prod")
@Configuration
public class FirebaseProdConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                InputStream is = new FileInputStream(firebaseConfigPath);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(is))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("[FirebaseProdConfig] Firebase 초기화 완료 (prod)");

            }
        } catch (IOException e) {
            throw new RuntimeException("Firebase 초기화 실패 (prod)", e);
        }
    }
}
