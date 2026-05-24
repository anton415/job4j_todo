package ru.job4j.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        return isAuthenticated(session) ? "redirect:/tasks" : "users/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        var userOptional = userService.findByLoginAndPassword(login, password);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Неверные логин или пароль.");
            model.addAttribute("login", login);
            return "users/login";
        }
        session.setAttribute("user", toSessionUser(userOptional.get()));
        return "redirect:/tasks";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        return isAuthenticated(session) ? "redirect:/tasks" : "users/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String login,
                           @RequestParam String password,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        var userOptional = userService.create(name, login, password);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Не удалось зарегистрировать пользователя. Проверьте данные или выберите другой логин.");
            model.addAttribute("name", name);
            model.addAttribute("login", login);
            return "users/register";
        }
        redirectAttributes.addFlashAttribute("success", "Регистрация успешна. Войдите в систему.");
        redirectAttributes.addFlashAttribute("login", userOptional.get().getLogin());
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    private User toSessionUser(User user) {
        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .login(user.getLogin())
                .build();
    }
}
