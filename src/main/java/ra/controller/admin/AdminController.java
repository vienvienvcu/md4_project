package ra.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ra.exception.CustomException;
import ra.model.dto.response.DataResponse;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Users;
import ra.service.IRoleService;
import ra.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    @Autowired
    private IRoleService roleService;


    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok().body("Welcome to admin");
    }

    @GetMapping("/getAllRole")
    public ResponseEntity<?> getAllRole() {
       return  ResponseEntity.ok().body(new SimpleResponse(roleService.findAll(), HttpStatus.OK));
    }




}
