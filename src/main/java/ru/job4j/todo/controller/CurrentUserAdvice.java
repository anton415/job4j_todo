package ru.job4j.todo.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

@ControllerAdvice
public class CurrentUserAdvice {

    @ModelAttribute
    public void addCurrentUser(Model model, HttpSession session) {
        model.addAttribute("currentUser", session.getAttribute("user"));
    }
}
