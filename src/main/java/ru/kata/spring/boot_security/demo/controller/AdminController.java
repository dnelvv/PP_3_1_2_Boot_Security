package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;

@RequestMapping("/")
@Controller
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService){
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public String adminPage(Model model){
        model.addAttribute("users", userService.listUsers());
        return "adminPage";
    }

    @GetMapping("/admin/add")
    public String addUser(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "addPage";
    }


    @PostMapping("/admin/add")
    public String saveUser(@ModelAttribute @Valid User user, BindingResult result,
                           @RequestParam(value = "checked", required = false) Long[] checked){
        if(result.hasErrors()){
            return "addPage";
        }
        if (checked == null) {
            user.setOneRole(roleService.getRoleByRole("ROLE_USER"));
            userService.add(user);
        } else {
            for (Long aLong : checked) {
                if (aLong != null) {
                    user.setOneRole(roleService.getRoleByID(aLong));
                    userService.add(user);
                }
            }
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        return "editPage";
    }

    @PostMapping("/admin/edit/{id}")
    public String saveEditUser(@PathVariable("id") Long id, @Valid User user, BindingResult result,
                               @RequestParam(value = "checked", required = false ) Long[] checked){
        if (result.hasErrors()) {
            user.setId(id);
            return "editPage";
        }
        if (checked == null) {
            user.setOneRole(roleService.getRoleByRole("ROLE_USER"));
            userService.update(user);
        } else {
            for (Long aLong : checked) {
                if (aLong != null) {
                    user.setOneRole(roleService.getRoleByID(aLong));
                    userService.update(user);
                }
            }
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
