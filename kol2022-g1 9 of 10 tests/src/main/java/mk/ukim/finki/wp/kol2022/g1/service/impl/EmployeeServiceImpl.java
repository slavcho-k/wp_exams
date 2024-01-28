package mk.ukim.finki.wp.kol2022.g1.service.impl;

import mk.ukim.finki.wp.kol2022.g1.model.Employee;
import mk.ukim.finki.wp.kol2022.g1.model.EmployeeType;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidEmployeeIdException;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidSkillIdException;
import mk.ukim.finki.wp.kol2022.g1.repository.EmployeeRepository;
import mk.ukim.finki.wp.kol2022.g1.repository.SkillRepository;
import mk.ukim.finki.wp.kol2022.g1.service.EmployeeService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SkillRepository skillRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Employee> listAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
    }

    @Override
    public Employee create(String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        return employeeRepository.save(new Employee(
                name, email, passwordEncoder.encode(password), type, skillRepository.findAllById(skillId), employmentDate
        ));
    }

    @Override
    public Employee update(Long id, String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        Employee employee = employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);

        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setType(type);
        employee.setSkills(skillRepository.findAllById(skillId));
        employee.setEmploymentDate(employmentDate);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee delete(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        employeeRepository.delete(employee);
        return employee;
    }

    //todo
    @Override
    public List<Employee> filter(Long skillId, Integer yearsOfService) {
        if (skillId != null && yearsOfService != null) {
            return employeeRepository.findAllBySkillsContainsAndEmploymentDateBefore(
                    skillRepository.findById(skillId).orElseThrow(InvalidEmployeeIdException::new),
                    LocalDate.now().minusYears(yearsOfService)
            );
        } else if (skillId != null) {
            return employeeRepository.findAllBySkillsContains(skillRepository.findById(skillId).orElseThrow(InvalidSkillIdException::new));
        } else if (yearsOfService != null) {
            return employeeRepository.findAllByEmploymentDateBefore(LocalDate.now().minusYears(yearsOfService));
        } else {
            return this.listAll();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> employee = employeeRepository.findByEmail(username);

        if (employee.isEmpty()) {
            throw new UsernameNotFoundException("");
        }

        System.out.println(employee.get().getType());
        return new User(employee.get().getEmail(), employee.get().getPassword(), Collections.singleton(employee.get().getType()));
    }
}
