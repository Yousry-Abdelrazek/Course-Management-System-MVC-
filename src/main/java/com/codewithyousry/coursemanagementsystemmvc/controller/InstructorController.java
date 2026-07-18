package com.codewithyousry.coursemanagementsystemmvc.controller;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.InstructorRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.InstructorResponse;
import com.codewithyousry.coursemanagementsystemmvc.service.InstructorService;
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
@RequestMapping("/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String query,
                       @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        Page<InstructorResponse> page = instructorService.search(query, pageable);
        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("activePage", "instructors");
        return "instructors/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("instructor", instructorService.getById(id));
        model.addAttribute("activePage", "instructors");
        return "instructors/details";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("instructor", new InstructorRequest());
        model.addAttribute("activePage", "instructors");
        return "instructors/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("instructor") InstructorRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "instructors");
            return "instructors/form";
        }
        InstructorResponse saved = instructorService.create(request);
        redirectAttributes.addFlashAttribute("successMessage",
                "Instructor '" + saved.getName() + "' created successfully.");
        return "redirect:/instructors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("instructor", instructorService.getRequestById(id));
        model.addAttribute("activePage", "instructors");
        return "instructors/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("instructor") InstructorRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "instructors");
            return "instructors/form";
        }
        instructorService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Instructor updated successfully.");
        return "redirect:/instructors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        instructorService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Instructor deleted successfully.");
        return "redirect:/instructors";
    }
}
