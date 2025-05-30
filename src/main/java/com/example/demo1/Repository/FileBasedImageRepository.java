package com.example.demo1.Repository;

import com.example.demo1.Image;
import com.example.demo1.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FileBasedImageRepository {

    private static final String DATA_FILE = "./data/images.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Image> images;

    public FileBasedImageRepository() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostConstruct
    private void loadImages() {
        File file = new File(DATA_FILE);
        if (file.exists() && file.length() > 0) {
            try {
                CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Image.class);
                images = objectMapper.readValue(file, listType);
            } catch (IOException e) {
                e.printStackTrace();
                images = new ArrayList<>();
            }
        } else {
            images = new ArrayList<>();
        }
    }

    private void saveImages() {
        try {
            objectMapper.writeValue(new File(DATA_FILE), images);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Image> findAll() {
        return new ArrayList<>(images);
    }

    public Optional<Image> findById(Long id) {
        return images.stream()
                .filter(image -> image.getId().equals(id))
                .findFirst();
    }

    public List<Image> findByUser(User user) {
        return images.stream()
                .filter(image -> image.getUser() != null && image.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public Image save(Image image) {
        if (image.getId() == null) {
            // Assign a simple auto-incrementing ID for demonstration
            Long newId = images.stream()
                    .mapToLong(Image::getId)
                    .max()
                    .orElse(0L) + 1;
            image.setId(newId);
            images.add(image);
        } else {
            images.stream()
                    .filter(img -> img.getId().equals(image.getId()))
                    .findFirst()
                    .ifPresent(existingImage -> {
                        int index = images.indexOf(existingImage);
                        images.set(index, image);
                    });
        }
        saveImages();
        return image;
    }

    public void deleteById(Long id) {
        images.removeIf(image -> image.getId().equals(id));
        saveImages();
    }
}