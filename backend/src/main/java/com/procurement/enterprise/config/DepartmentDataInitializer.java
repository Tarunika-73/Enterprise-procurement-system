package com.procurement.enterprise.config;

import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.Product;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.ProductRepository;
import com.procurement.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ensures required enterprise departments exist and backfills product department ownership.
 */
@Component
@RequiredArgsConstructor
public class DepartmentDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DepartmentDataInitializer.class);

    private static final Map<String, String> REQUIRED_DEPARTMENTS = new LinkedHashMap<>();

    static {
        REQUIRED_DEPARTMENTS.put("IT", "Information Technology");
        REQUIRED_DEPARTMENTS.put("HR", "Human Resources");
        REQUIRED_DEPARTMENTS.put("FIN", "Finance");
        REQUIRED_DEPARTMENTS.put("MKT", "Marketing");
        REQUIRED_DEPARTMENTS.put("SAL", "Sales");
        REQUIRED_DEPARTMENTS.put("PRC", "Procurement");
        REQUIRED_DEPARTMENTS.put("ADM", "Administration");
    }

    private final DepartmentRepository departmentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        REQUIRED_DEPARTMENTS.forEach((code, name) -> {
            departmentRepository.findByCodeAndIsDeletedFalse(code).orElseGet(() ->
                    departmentRepository.findByNameAndIsDeletedFalse(name).orElseGet(() -> {
                        Department created = departmentRepository.save(Department.builder()
                                .code(code)
                                .name(name)
                                .isDeleted(false)
                                .build());
                        log.info("Created department {} ({})", name, code);
                        return created;
                    }));
        });

        List<Department> departments = departmentRepository.findAllByIsDeletedFalse();
        for (Department department : departments) {
            if (department.getManager() == null) {
                List<User> managers = userRepository.findActiveManagersByDepartmentId(department.getId());
                if (!managers.isEmpty()) {
                    department.setManager(managers.get(0));
                    departmentRepository.save(department);
                    log.info("Linked manager {} to department {}", managers.get(0).getId(), department.getCode());
                }
            }
        }

        if (departments.isEmpty()) {
            return;
        }

        Department fallback = departments.get(0);
        List<Product> unassigned = productRepository.findByDepartmentIsNullAndIsDeletedFalse();
        if (unassigned.isEmpty()) {
            return;
        }

        int index = 0;
        for (Product product : unassigned) {
            Department assigned = departments.get(index % departments.size());
            product.setDepartment(assigned != null ? assigned : fallback);
            index++;
        }
        productRepository.saveAll(unassigned);
        log.info("Assigned department to {} products without department ownership", unassigned.size());
    }
}
