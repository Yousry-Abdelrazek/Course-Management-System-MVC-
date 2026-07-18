package com.codewithyousry.coursemanagementsystemmvc.controller;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.CourseRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.CourseResponse;
import com.codewithyousry.coursemanagementsystemmvc.service.CourseService;
import com.codewithyousry.coursemanagementsystemmvc.service.InstructorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final InstructorService instructorService;

    public CourseController(CourseService courseService, InstructorService instructorService) {
        this.courseService = courseService;
        this.instructorService = instructorService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String query,
                       @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        Page<CourseResponse> page = courseService.search(query, pageable);
        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("activePage", "courses");
        return "courses/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getById(id));
        model.addAttribute("activePage", "courses");
        return "courses/details";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("course", new CourseRequest());
        addInstructorOptions(model);
        model.addAttribute("activePage", "courses");
        return "courses/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("course") CourseRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addInstructorOptions(model);
            model.addAttribute("activePage", "courses");
            return "courses/form";
        }
        CourseResponse saved = courseService.create(request);
        redirectAttributes.addFlashAttribute("successMessage",
                "Course '" + saved.getTitle() + "' created successfully.");
        return "redirect:/courses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getRequestById(id));
        addInstructorOptions(model);
        model.addAttribute("activePage", "courses");
        return "courses/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("course") CourseRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addInstructorOptions(model);
            model.addAttribute("activePage", "courses");
            return "courses/form";
        }
        courseService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        return "redirect:/courses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseService.softDelete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        return "redirect:/courses";
    }

    private void addInstructorOptions(Model model) {
        Pageable all = PageRequest.of(0, 1000, Sort.by("name"));
        model.addAttribute("instructors", instructorService.search(null, all).getContent());
    }
}
