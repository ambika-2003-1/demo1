package com.example.demo1;

import com.example.demo1.Repository.FileBasedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
 private FileBasedUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This method is primarily used by Spring Security's authentication provider.
        // We'll keep it finding by username for backward compatibility or other potential uses,
        // but the main authentication by email will be handled in the login process.
 return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User registerNewUser(String username, String password, String email) {
 if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Username or Email already exists");
        }

        User user = new User(username, passwordEncoder.encode(password), email, "USER");
 userRepository.save(user);
 return user;
    }

    // This method might need adjustment if you change how images are associated with users
    // based on email login. For now, it remains tied to username.
    public void addImageToUser(String username, String imageId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        user.addImageId(imageId);
        userRepository.save(user);
    }
}