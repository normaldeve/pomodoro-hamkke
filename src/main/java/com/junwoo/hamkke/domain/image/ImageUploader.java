package com.junwoo.hamkke.domain.image;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 21.
 */
public interface ImageUploader {

    String upload(MultipartFile file, ImageDirectory directory);

    void delete(String fileUrl);
}
