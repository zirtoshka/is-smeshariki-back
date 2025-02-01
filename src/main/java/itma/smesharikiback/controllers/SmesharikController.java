package itma.smesharikiback.controllers;

import itma.smesharikiback.requests.smesharik.SmesharikChangePasswordRequest;
import itma.smesharikiback.requests.smesharik.SmesharikChangeRoleRequest;
import itma.smesharikiback.requests.smesharik.SmesharikUpdateRequest;
import itma.smesharikiback.response.MessageResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.response.SmesharikResponse;
import itma.smesharikiback.services.SmesharikService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/smesharik")
@RequiredArgsConstructor
public class SmesharikController {
    private final SmesharikService smesharikService;

    @PutMapping(value = "/{login}")
    public @NotNull SmesharikResponse put(@PathVariable String login, @Validated @RequestBody SmesharikUpdateRequest request) {
        return smesharikService.update(request, login);
    }

    @PostMapping("/{login}/changePassword")
    public @NotNull MessageResponse changePassword(@PathVariable String login,
                                                   @Validated @RequestBody SmesharikChangePasswordRequest request) {
        return smesharikService.changePassword(request, login);
    }

    @PostMapping("/{login}/changeRole")
    public @NotNull SmesharikResponse changeRole(@PathVariable String login,
                                               @Validated @RequestBody SmesharikChangeRoleRequest request) {
        return smesharikService.changeRole(request, login);
    }

    @GetMapping(value = "/{login}")
    public @NotNull SmesharikResponse get(@PathVariable String login) {
        return smesharikService.get(login);
    }

    @GetMapping
    public @NotNull PaginatedResponse<SmesharikResponse> getAll(
            @RequestParam(required = false, defaultValue = "") String nameOrLogin,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        if (roles == null || roles.isEmpty()) {
            roles = Arrays.asList("USER", "ADMIN", "DOCTOR");
        }

        return smesharikService.getAll(nameOrLogin, roles, page, size);
    }
}
