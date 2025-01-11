package itma.smesharikiback.controllers;

import itma.smesharikiback.requests.SmesharikUpdateRequest;
import itma.smesharikiback.response.SmesharikResponse;
import itma.smesharikiback.services.SmesharikService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/smesharik")
@RequiredArgsConstructor
public class SmesharikController {
    private final SmesharikService smesharikService;

    @PutMapping(value = "/{login}")
    public @NotNull SmesharikResponse put(@PathVariable String login, @Validated @RequestBody SmesharikUpdateRequest request) {
        return smesharikService.update(request, login);
    }

    @GetMapping(value = "/{login}")
    public @NotNull SmesharikResponse get(@PathVariable String login) {
        return smesharikService.get(login);
    }
}
