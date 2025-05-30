package com.example.demo1.Controller;

import com.example.demo1.Image;
import com.example.demo1.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import com.example.demo1.Service.ImageService;

import jakarta.annotation.PostConstruct;

@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @PostConstruct
    public void init() {
        nu.pattern.OpenCV.loadLocally();
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            // Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/upload";
            }

            // Check file size (server-side validation)
            long maxSizeBytes = 10 * 1024 * 1024; // 10MB in bytes
            if (file.getSize() > maxSizeBytes) {
                redirectAttributes.addFlashAttribute("message", "File size exceeds the maximum limit of 10MB");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/upload";
            }

            Image image = new Image();
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setData(file.getBytes());
            image.setUsername(currentUsername); // Set the username of the uploader

            Mat mat = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_UNCHANGED);
            String analysisResult = "";
            if (!mat.empty()) {
                analysisResult = "Width: " + mat.width() + ", Height: " + mat.height();
            } else {
                analysisResult = "Could not analyze image.";
            }
            image.setAnalysisResult(analysisResult);

            image.setId(UUID.randomUUID().toString()); // Generate a unique ID for the image
            imageService.saveImage(image);

            // Associate the image with the user
            userService.addImageToUser(currentUsername, savedImage.getId());

            redirectAttributes.addFlashAttribute("message", "Image uploaded successfully with ID: " + savedImage.getId());
            redirectAttributes.addFlashAttribute("messageType", "success");

            return "redirect:/view/" + savedImage.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload image: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/upload";
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        Optional<Image> imageOptional = imageService.getImageById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .body(image.getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(MultipartException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "File upload error: The file size exceeds the maximum allowed limit of 10MB");
        redirectAttributes.addFlashAttribute("messageType", "danger");
        return "redirect:/upload";
    }
}
