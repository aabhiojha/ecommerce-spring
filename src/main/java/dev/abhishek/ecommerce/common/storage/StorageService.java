package dev.abhishek.ecommerce.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Stores the file under the given key prefix and returns the stored object key.
     */
    String upload(String keyPrefix, MultipartFile file);

    void delete(String key);

    byte[] download(String key);

    /**
     * Publicly reachable URL for an object key.
     */
    String downloadUrl(String key);
}
