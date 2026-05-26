package ru.job4j.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String listAll(Model model, @SessionAttribute("user") User user) {
        return list(model, taskService.findAll(user), "Все задания", "all");
    }

    @GetMapping("/tasks/done")
    public String listDone(Model model, @SessionAttribute("user") User user) {
        return list(model, taskService.findCompleted(user), "Выполненные задания", "done");
    }

    @GetMapping("/tasks/new")
    public String listNew(Model model, @SessionAttribute("user") User user) {
        return list(model, taskService.findNew(user), "Новые задания", "new");
    }

    @GetMapping("/tasks/create")
    public String createPage(Model model) {
        model.addAttribute("categories", taskService.findAllCategories());
        model.addAttribute("priorities", taskService.findAllPriorities());
        return "tasks/create";
    }

    @PostMapping("/tasks")
    public String create(@ModelAttribute Task task,
                         @RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
                         @SessionAttribute("user") User user) {
        taskService.create(task, user, categoryIds);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/{id}")
    public String detail(@PathVariable int id,
                         Model model,
                         @SessionAttribute("user") User user) {
        model.addAttribute("task", getTaskOrThrow(id, user));
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
    public String editPage(@PathVariable int id,
                           Model model,
                           @SessionAttribute("user") User user) {
        model.addAttribute("task", getTaskOrThrow(id, user));
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

    private Task getTaskOrThrow(int id, User user) {
        return taskService.findById(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }
}
