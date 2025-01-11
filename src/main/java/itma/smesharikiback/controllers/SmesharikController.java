package itma.smesharikiback.controllers;

import itma.smesharikiback.requests.SmesharikUpdateRequest;
import itma.smesharikiback.response.SmesharikResponse;
import itma.smesharikiback.services.SmesharikService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/smesharik")
@RequiredArgsConstructor
public class SmesharikController {
    private final SmesharikService smesharikService;

    @PutMapping(value = "/{login}")
    public SmesharikResponse put(@PathVariable String login, @RequestBody SmesharikUpdateRequest request) {
        return smesharikService.update(request, login);
    }

    @GetMapping(value = "/{login}")
    public SmesharikResponse get(@PathVariable String login) {
        return smesharikService.get(login);
    }
}
