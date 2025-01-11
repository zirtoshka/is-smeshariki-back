package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SmesharikService {
    private final SmesharikRepository repository;

    public Smesharik save(Smesharik smesharik) {
        return repository.save(smesharik);
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Smesharik create(Smesharik smesharik) {
        Map<String, String> errors = new HashMap<>();

        if (repository.existsByLogin(smesharik.getUsername())) {
            errors.put("login", "Пользователь с таким login уже существует");
        }
        if (repository.existsByEmail(smesharik.getEmail())) {
            errors.put("email", "Пользователь с таким email уже существует");
        }

        if (!errors.isEmpty()) {
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }

        return save(smesharik);
    }


    public Smesharik getByLogin(String login) {
        return repository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    public Smesharik getCurrentSmesharik() {
        var login = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByLogin(login);
    }
}
