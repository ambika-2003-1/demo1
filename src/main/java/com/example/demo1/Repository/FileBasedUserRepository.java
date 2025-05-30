package com.example.demo1.data;

import com.example.demo1.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonUserRepository {

    private static final String DATA_FILE = "./data/users.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<User> users;

    public JsonUserRepository() {
        loadUsers();
    }

    private void loadUsers() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists() && file.length() > 0) {
                users = objectMapper.readValue(file, new TypeReference<List<User>>() {});
            } else {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try {
            objectMapper.writeValue(new File(DATA_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail() != null && user.getEmail().equals(email))
                .findFirst();
    }

    public User save(User user) {
        // In a real application, you'd handle ID generation and updates properly.
        // For simplicity, this assumes new users or updates replace existing ones based on a key
        // (which is not ideal for a list-based storage).
        // A proper implementation would manage IDs and either add a new user or update an existing one.
        Optional<User> existingUser = users.stream()
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findFirst();

        if (existingUser.isPresent()) {
            int index = users.indexOf(existingUser.get());
            users.set(index, user);
        } else {
            users.add(user);
        }
        saveUsers();
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    // Add other methods as needed, similar to UserRepository
}