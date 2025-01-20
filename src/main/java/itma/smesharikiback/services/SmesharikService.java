package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import itma.smesharikiback.requests.SmesharikUpdateRequest;
import itma.smesharikiback.response.SmesharikResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmesharikService {
    private final SmesharikRepository repository;

    public Smesharik save(Smesharik smesharik) {
        return repository.save(smesharik);
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void create(Smesharik smesharik) {
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

        save(smesharik);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SmesharikResponse update(SmesharikUpdateRequest request, String login) {
        HashMap<String, String> errors = new HashMap<>();
        Smesharik smesharik = getByLoginPermissions(login);
        smesharik.setName(request.getName());
        Optional<Smesharik> smesharik1 = repository.findByLogin(login);
        if (smesharik1.isPresent() && !smesharik1.get().equals(smesharik)) {
            errors.put("login", "Такой login уже занят.");
        } else {
            smesharik.setLogin(request.getLogin());
        }
        smesharik1 = repository.findByEmail(request.getEmail());
        if (smesharik1.isPresent() && !smesharik1.get().equals(smesharik)) {
            errors.put("email", "Такой email уже занят.");
        } else {
            smesharik.setEmail(request.getEmail());
        }
        smesharik.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));

        if (!errors.isEmpty()) {
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }

        return buildResponse(save(smesharik));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SmesharikResponse get(String login) {
        Smesharik smesharik = getByLoginPermissions(login);
        return buildResponse(smesharik);
    }

    private SmesharikResponse buildResponse(Smesharik smesharik) {
        return new SmesharikResponse()
                .setName(smesharik.getName())
                .setLogin(smesharik.getLogin())
                .setEmail(smesharik.getEmail());
    }


    private Smesharik getByLoginPermissions(String login) {
        HashMap<String, String> errors = new HashMap<>();

        Optional<Smesharik> smesharikOpt = repository.findByLogin(login);
        if (smesharikOpt.isEmpty()) {
            errors.put("message", "Данный smesharik не найден.");
            throw new GeneralException(HttpStatus.NOT_FOUND, errors);
        }
        Smesharik smesharik = smesharikOpt.get();
        if (!Objects.equals(smesharik.getId(), getCurrentSmesharik().getId())) {
            errors.put("message", "Ошибка доступа.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }
        return smesharik;
    }

    public Smesharik getSmesharik(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("followee", "Followee не был найден.");
                    return new GeneralException(HttpStatus.BAD_REQUEST, errors);
                });
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
