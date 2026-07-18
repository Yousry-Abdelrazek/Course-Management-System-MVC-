package com.codewithyousry.coursemanagementsystemmvc.controller;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.EnrollmentCreateRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.request.EnrollmentUpdateRequest;
import com.codewithyousry.coursemanagementsystemmvc.entity.EnrollmentStatus;
import com.codewithyousry.coursemanagementsystemmvc.service.CourseService;
import com.codewithyousry.coursemanagementsystemmvc.service.EnrollmentService;
import com.codewithyousry.coursemanagementsystemmvc.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentController(EnrollmentService enrollmentService,
                               StudentService studentService,
                               CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "enrolledOn", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {
        model.addAttribute("page", enrollmentService.findAll(pageable));
        model.addAttribute("activePage", "enrollments");
        return "enrollments/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("enrollment", enrollmentService.getById(id));
        model.addAttribute("activePage", "enrollments");
        return "enrollments/details";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("enrollment", new EnrollmentCreateRequest());
        addOptions(model);
        model.addAttribute("activePage", "enrollments");
        return "enrollments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("enrollment") EnrollmentCreateRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addOptions(model);
            model.addAttribute("activePage", "enrollments");
            return "enrollments/form";
        }
        enrollmentService.create(request);
        redirectAttributes.addFlashAttribute("successMessage", "Enrollment created successfully.");
        return "redirect:/enrollments";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("enrollment", enrollmentService.getUpdateRequestById(id));
        model.addAttribute("detail", enrollmentService.getById(id));
        model.addAttribute("statuses", EnrollmentStatus.values());
        model.addAttribute("activePage", "enrollments");
        return "enrollments/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("enrollment") EnrollmentUpdateRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("detail", enrollmentService.getById(id));
            model.addAttribute("statuses", EnrollmentStatus.values());
            model.addAttribute("activePage", "enrollments");
            return "enrollments/edit";
        }
        enrollmentService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Enrollment updated successfully.");
        return "redirect:/enrollments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        enrollmentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Enrollment deleted successfully.");
        return "redirect:/enrollments";
    }

    private void addOptions(Model model) {
        Pageable all = PageRequest.of(0, 1000, Sort.by("name"));
        model.addAttribute("students", studentService.search(null, all).getContent());
        model.addAttribute("courses", courseService.search(null,
                PageRequest.of(0, 1000, Sort.by("title"))).getContent());
    }
}
