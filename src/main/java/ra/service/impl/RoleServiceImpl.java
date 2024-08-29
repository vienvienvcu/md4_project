package ra.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.model.entity.Roles;
import ra.repository.IRoleRepository;
import ra.service.IRoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired
    private IRoleRepository roleRepository;

    public List<Roles> findAll() {
        return roleRepository.findAll();
    }
}
