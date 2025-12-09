package itma.smesharikiback.application.service;

import itma.smesharikiback.application.mapper.DomainMapper;
import itma.smesharikiback.domain.exception.AccessDeniedException;
import itma.smesharikiback.domain.exception.DomainException;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import itma.smesharikiback.presentation.dto.request.smesharik.SmesharikChangePasswordRequest;
import itma.smesharikiback.presentation.dto.request.smesharik.SmesharikChangeRoleRequest;
import itma.smesharikiback.presentation.dto.request.smesharik.SmesharikUpdateRequest;
import itma.smesharikiback.presentation.dto.response.MessageResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import itma.smesharikiback.presentation.dto.response.SmesharikResponse;
import itma.smesharikiback.infrastructure.specification.SmesharikSpecification;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
    private final PasswordEncoder passwordEncoder;
    private final DomainMapper domainMapper;

    public Smesharik save(Smesharik smesharik) {
        return repository.save(smesharik);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void create(Smesharik smesharik) {
        Map<String, String> errors = new HashMap<>();

        if (repository.existsByLogin(smesharik.getUsername())) {
            errors.put("login", "User with this login already exists");
        }
        if (repository.existsByEmail(smesharik.getEmail())) {
            errors.put("email", "User with this email already exists");
        }

        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";
        if (!Pattern.matches(regex, smesharik.getEmail())) {
            errors.put("email", "Invalid email.");
        }

        regex = "#[A-Fa-f0-9]{6}";
        if (smesharik.getColor() != null && !Pattern.matches(regex, smesharik.getColor())) {
            errors.put("color", "Invalid color.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        save(smesharik);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SmesharikResponse update(SmesharikUpdateRequest request, String login) {
        HashMap<String, String> errors = new HashMap<>();
        Smesharik smesharik = getByLoginPermissions(login);

        smesharik.setName(request.getName());
        smesharik.setColor(request.getColor());

        Optional<Smesharik> smeshharikByLogin = repository.findByLogin(login);
        if (smeshharikByLogin.isPresent() && !smeshharikByLogin.get().equals(smesharik)) {
            errors.put("login", "Login already taken.");
        } else {
            smesharik.setLogin(request.getLogin());
        }
        Optional<Smesharik> smeshharikByEmail = repository.findByEmail(request.getEmail());
        if (smeshharikByEmail.isPresent() && !smeshharikByEmail.get().equals(smesharik)) {
            errors.put("email", "Email already taken.");
        } else {
            smesharik.setEmail(request.getEmail());
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return domainMapper.toSmesharikResponse(save(smesharik));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SmesharikResponse get(String login) {
        Smesharik smesharik = getByLogin(login);
        return domainMapper.toSmesharikResponse(smesharik);
    }

    public @NotNull MessageResponse changePassword(SmesharikChangePasswordRequest request, String login) {
        HashMap<String, String> errors = new HashMap<>();
        Smesharik smesharik = getByLoginPermissions(login);

        if (!passwordEncoder.matches(request.getOldPassword(), smesharik.getPassword())) {
            errors.put("oldPassword", "Old password is incorrect.");
            throw new AccessDeniedException(errors);
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            errors.put("message", "New password must be different from old.");
            throw new AccessDeniedException(errors);
        }

        smesharik.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(smesharik);

        return new MessageResponse().setMessage("Password changed successfully.");
    }

    public @NotNull SmesharikResponse changeRole(SmesharikChangeRoleRequest request, String login) {
        commonService.checkIfAdmin();
        HashMap<String, String> errors = new HashMap<>();
        Smesharik smesharik = getByLogin(login);
        if (smesharik.getRole().equals(request.getRole())) {
            errors.put("role", "Requested role already set.");
            throw new AccessDeniedException(errors);
        }

        smesharik.setRole(request.getRole());
        return domainMapper.toSmesharikResponse(repository.save(smesharik));
    }

    private Smesharik getByLoginPermissions(String login) {
        HashMap<String, String> errors = new HashMap<>();
        Smesharik smesharik = getByLogin(login);

        if (!Objects.equals(smesharik.getId(), commonService.getCurrentSmesharik().getId())) {
            errors.put("message", "Access denied.");
            throw new AccessDeniedException(errors);
        }

        return smesharik;
    }

    private Smesharik getByLogin(String login) {
        HashMap<String, String> errors = new HashMap<>();

        Optional<Smesharik> smesharikOpt = repository.findByLogin(login);
        if (smesharikOpt.isEmpty()) {
            errors.put("message", "Smesharik not found.");
            throw new DomainException(HttpStatus.NOT_FOUND, errors);
        }

        return smesharikOpt.get();
    }


    public @NotNull PaginatedResponse<SmesharikResponse> getAll(String nameOrLogin, List<String> roles, @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Smesharik> specification = Specification.where(SmesharikSpecification.hasNameOrLogin(nameOrLogin))
                .and(SmesharikSpecification.hasRoles(roles));

        Page<Smesharik> smesharikPage = repository.findAll(specification, pageable);

        List<SmesharikResponse> responses = smesharikPage.getContent().stream()
                .map(domainMapper::toSmesharikResponse)
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

