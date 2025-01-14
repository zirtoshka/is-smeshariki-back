package itma.smesharikiback.controllers;

import itma.smesharikiback.requests.CommentRequest;
import itma.smesharikiback.response.CommentResponse;
import itma.smesharikiback.services.CommentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @PostMapping
    public CommentResponse add(
            @Validated @RequestBody CommentRequest request
    ) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public CommentResponse get(@PathVariable @NotNull Long id) {
        return service.get(id);
    }

}
