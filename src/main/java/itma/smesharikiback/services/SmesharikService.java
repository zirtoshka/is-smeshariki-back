package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import itma.smesharikiback.requests.smesharik.SmesharikChangePasswordRequest;
import itma.smesharikiback.requests.smesharik.SmesharikChangeRoleRequest;
import itma.smesharikiback.requests.smesharik.SmesharikUpdateRequest;
import itma.smesharikiback.response.MessageResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.response.SmesharikResponse;
import itma.smesharikiback.specification.SmesharikSpecification;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SmesharikService {
    private final SmesharikRepository repository;
    private final CommonService commonService;
    private final FriendService friendService;
    private final PasswordEncoder passwordEncoder;

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

        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";
        if (!Pattern.matches(regex, smesharik.getEmail())) {
            errors.put("email", "Передан некорректный email.");
        }

        regex = "#[A-Fa-f0-9]{6}";
        if (smesharik.getColor() != null && !Pattern.matches(regex, smesharik.getColor())) {
            errors.put("color", "Передан некорректный color.");
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
        smesharik.setColor(request.getColor());

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
        if (!errors.isEmpty()) {
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }

        return buildResponse(save(smesharik));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SmesharikResponse get(String login) {
        Smesharik smesharik = getByLogin(login);
        return buildResponse(smesharik);
    }

    public @NotNull MessageResponse changePassword(SmesharikChangePasswordRequest request, String login) {
        HashMap<String, String> errors = new HashMap<>();
        Smesharik smesharik = getByLoginPermissions(login);

        System.out.println(new BCryptPasswordEncoder().encode(request.getOldPassword()));
        System.out.println(smesharik.getPassword());
        if (!passwordEncoder.matches(request.getOldPassword(), smesharik.getPassword())) {
            errors.put("oldPassword", "Старый пароль неверный.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            errors.put("message", "Пароли не могут совпадать.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }

        smesharik.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(smesharik);

        return new MessageResponse().setMessage("Пароль успешно именён!");
    }

    public @NotNull SmesharikResponse changeRole(SmesharikChangeRoleRequest request, String login) {
        commonService.checkIfAdmin();
        HashMap<String, String> errors = new HashMap<>();
        Smesharik smesharik = getByLogin(login);
        if (smesharik.getRole().equals(request.getRole())) {
            errors.put("role", "Не должна совпадать с текущей.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }

        smesharik.setRole(request.getRole());
        return buildResponse(repository.save(smesharik));
    }


    private SmesharikResponse buildResponse(Smesharik smesharik) {
        return new SmesharikResponse()
                .setName(smesharik.getName())
                .setLogin(smesharik.getLogin())
                .setEmail(smesharik.getEmail())
                .setRole(smesharik.getRole())
                .setIsOnline(smesharik.getIsOnline())
                .setColor(smesharik.getColor());
    }

    private Smesharik getByLoginPermissions(String login) {
        HashMap<String, String> errors = new HashMap<>();
        Smesharik smesharik = getByLogin(login);

        if (!Objects.equals(smesharik.getId(), commonService.getCurrentSmesharik().getId())) {
            errors.put("message", "Ошибка доступа.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }

        return smesharik;
    }

    private Smesharik getByLogin(String login) {
        HashMap<String, String> errors = new HashMap<>();

        Optional<Smesharik> smesharikOpt = repository.findByLogin(login);
        if (smesharikOpt.isEmpty()) {
            errors.put("message", "Данный smesharik не найден.");
            throw new GeneralException(HttpStatus.NOT_FOUND, errors);
        }

        return smesharikOpt.get();
    }


    public @NotNull PaginatedResponse<SmesharikResponse> getAll(String nameOrLogin, List<String> roles, @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Smesharik> specification = Specification.where(SmesharikSpecification.hasNameOrLogin(nameOrLogin))
                .and(SmesharikSpecification.hasRoles(roles));

        Page<Smesharik> smesharikPage = repository.findAll(specification, pageable);

        List<SmesharikResponse> responses = smesharikPage.getContent().stream()
                .map(this::buildResponse)
                .toList();

        return new PaginatedResponse<>(
                responses,
                smesharikPage.getTotalPages(),
                smesharikPage.getTotalElements(),
                smesharikPage.getNumber(),
                smesharikPage.getSize()
        );

    }
}
