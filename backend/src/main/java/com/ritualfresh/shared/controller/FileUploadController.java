package com.ritualfresh.shared.controller;

import com.ritualfresh.shared.service.StorageService;
import com.ritualfresh.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {
    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

    private final StorageService storageService;

    @PostMapping
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Debe seleccionar una imagen.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new BusinessRuleException("La imagen no debe superar los 5 MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessRuleException("Solo se permiten imagenes JPG, PNG o WEBP.");
        }

        if (!hasAllowedExtension(file.getOriginalFilename())) {
            throw new BusinessRuleException("Solo se permiten imagenes JPG, PNG o WEBP.");
        }

        String filename = storageService.store(file);
        String url = "/uploads/" + filename;
        return Map.of("url", url);
    }

    private boolean hasAllowedExtension(String filename) {
        if (filename == null || filename.isBlank() || !filename.contains(".")) {
            return false;
        }

        String extension = filename.substring(filename.lastIndexOf('.')).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }
}
