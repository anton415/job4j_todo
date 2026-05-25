package ru.job4j.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.TaskService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping({"/", "/tasks"})
    public String listAll(Model model) {
        return list(model, taskService.findAll(), "Все задания", "all");
    }

    @GetMapping("/tasks/done")
    public String listDone(Model model) {
        return list(model, taskService.findCompleted(), "Выполненные задания", "done");
    }

    @GetMapping("/tasks/new")
    public String listNew(Model model) {
        return list(model, taskService.findNew(), "Новые задания", "new");
    }

    @GetMapping("/tasks/create")
    public String createPage() {
        return "tasks/create";
    }

    @PostMapping("/tasks")
    public String create(@RequestParam String title,
                         @RequestParam String description,
                         @SessionAttribute("user") User user) {
        taskService.create(title, description, user);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/{id}")
    public String detail(@PathVariable int id, Model model) {
        model.addAttribute("task", getTaskOrThrow(id));
        return "tasks/detail";
    }

    @PostMapping("/tasks/{id}/done")
    public String setDone(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (!taskService.setDone(id)) {
            redirectAttributes.addFlashAttribute("error", "Не удалось обновить задание. Возможно, оно уже удалено.");
            return "redirect:/tasks";
        }
        return "redirect:/tasks/" + id;
    }

    @GetMapping("/tasks/{id}/edit")
    public String editPage(@PathVariable int id, Model model) {
        model.addAttribute("task", getTaskOrThrow(id));
        return "tasks/edit";
    }

    @PostMapping("/tasks/{id}/update")
    public String update(@PathVariable int id,
                         @RequestParam String title,
                         @RequestParam String description,
                         @RequestParam(defaultValue = "false") boolean done,
                         RedirectAttributes redirectAttributes) {
        if (!taskService.update(id, title, description, done)) {
            redirectAttributes.addFlashAttribute("error", "Не удалось обновить задание. Возможно, оно уже удалено.");
            return "redirect:/tasks";
        }
        return "redirect:/tasks/" + id;
    }

    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (!taskService.delete(id)) {
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить задание. Возможно, оно уже удалено.");
        }
        return "redirect:/tasks";
    }

    private String list(Model model, List<Task> tasks, String title, String activeFilter) {
        model.addAttribute("tasks", tasks);
        model.addAttribute("title", title);
        model.addAttribute("activeFilter", activeFilter);
        return "tasks/list";
    }

    private Task getTaskOrThrow(int id) {
        return taskService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }
}
