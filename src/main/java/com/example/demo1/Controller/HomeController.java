package com.example.demo1.Controller;

import com.example.demo1.Image;
import com.example.demo1.Repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Image> images;
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            // User is logged in, show only their images
            String username = authentication.getName();
            images = imageRepository.findByUsername(username);
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", username);
        } else {
            // User is not logged in, don't show any images
            images = List.of();
            model.addAttribute("isAuthenticated", false);
        }

        model.addAttribute("images", images);
        return "index";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "upload";
    }

    @GetMapping("/view/{id}")
    public String viewImage(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Image> imageOptional = imageRepository.findById(id);

        if (imageOptional.isPresent()) {
            model.addAttribute("image", imageOptional.get());
            return "view";
        } else {
            redirectAttributes.addFlashAttribute("message", "Image not found");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/";
        }
    }
}
