package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ForumUserServiceImpl implements ForumUserService, UserDetailsService {
    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    public ForumUserServiceImpl(ForumUserRepository forumUserRepository, InterestRepository interestRepository, PasswordEncoder passwordEncoder) {
        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ForumUser> listAll() {
        return forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return forumUserRepository.findById(id).orElseGet(null);
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        return forumUserRepository.save(new ForumUser(
                name,
                email,
                passwordEncoder.encode(password),
                type,
                interestRepository.findAllById(interestId),
                birthday
        ));
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        Optional<ForumUser> userOptional = forumUserRepository.findById(id);

        if (userOptional.isPresent()) {
            ForumUser user = userOptional.get();
            user.setId(id);
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setType(type);
            user.setInterests(interestRepository.findAllById(interestId));
            user.setBirthday(birthday);
            return forumUserRepository.save(user);
        }

        throw new InvalidForumUserIdException();
    }

    @Override
    public ForumUser delete(Long id) {
        Optional<ForumUser> user = forumUserRepository.findById(id);

        if (user.isPresent()) {
            forumUserRepository.delete(user.get());
            return user.get();
        }

        throw new InvalidForumUserIdException();
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        if (interestId == null && age == null) {
            return forumUserRepository.findAll();
        } else if (interestId != null && age != null) {
            LocalDate date = LocalDate.now().minusYears(age);
            Interest interest = interestRepository.getById(interestId);
            return forumUserRepository.findAllByBirthdayIsBeforeAndInterestsContains(date, interest);
        } else if (interestId == null) {
            LocalDate date = LocalDate.now().minusYears(age);
            return forumUserRepository.findAllByBirthdayIsBefore(date);
        } else {
            return forumUserRepository.findAllByInterestsContains(interestRepository.getById(interestId));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ForumUser> user = forumUserRepository.findByEmail(username);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }

        return User.builder()
                .username(user.get().getEmail())
                .password(user.get().getPassword())
                .authorities("ROLE_" + user.get().getType().toString())
                .build();
    }
}
