package com.junwoo.hamkke.domain.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
@Slf4j
@Profile("!test")
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            InputStream inputStream;

            if (firebaseConfigPath.startsWith("classpath:")) {
                // dev 환경
                String path = firebaseConfigPath.replace("classpath:", "");
                inputStream = new ClassPathResource(path).getInputStream();
                log.info("[FirebaseConfig] classpath에서 Firebase 파일 로드");
            } else {
                // prod 환경
                inputStream = new FileInputStream(firebaseConfigPath);
                log.info("[FirebaseConfig] 외부 파일 시스템에서 Firebase 파일 로드");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("[FirebaseConfig] Firebase 초기화 완료");

        } catch (Exception e) {
            log.error("[FirebaseConfig] Firebase 초기화 실패", e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }
}
