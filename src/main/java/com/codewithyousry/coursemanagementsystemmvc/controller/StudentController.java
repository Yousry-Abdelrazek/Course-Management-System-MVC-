package com.codewithyousry.coursemanagementsystemmvc.controller;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.StudentRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.StudentResponse;
import com.codewithyousry.coursemanagementsystemmvc.service.EnrollmentService;
import com.codewithyousry.coursemanagementsystemmvc.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    public StudentController(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String query,
                       @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        Page<StudentResponse> page = studentService.search(query, pageable);
        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("activePage", "students");
        return "students/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getById(id));
        model.addAttribute("activePage", "students");
        return "students/details";
    }

    @GetMapping("/{id}/enrollments")
    public String enrollments(@PathVariable Long id,
                              @PageableDefault(size = 10, sort = "enrolledOn", direction = Sort.Direction.DESC) Pageable pageable,
                              Model model) {
        model.addAttribute("student", studentService.getById(id));
        model.addAttribute("page", enrollmentService.findByStudent(id, pageable));
        model.addAttribute("activePage", "students");
        return "students/enrollments";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("student", new StudentRequest());
        model.addAttribute("activePage", "students");
        return "students/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("student") StudentRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "students");
            return "students/form";
        }
        StudentResponse saved = studentService.create(request);
        redirectAttributes.addFlashAttribute("successMessage",
                "Student '" + saved.getName() + "' created successfully.");
        return "redirect:/students";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getRequestById(id));
        model.addAttribute("activePage", "students");
        return "students/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("student") StudentRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "students");
            return "students/form";
        }
        studentService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully.");
        return "redirect:/students";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully.");
        return "redirect:/students";
    }
}
