package itma.smesharikiback.presentation.controller;

import itma.smesharikiback.presentation.dto.request.CommentRequest;
import itma.smesharikiback.presentation.dto.response.CommentResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import itma.smesharikiback.application.service.CommentService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @GetMapping
    public PaginatedResponse<CommentResponse> getAll(
            @RequestParam(required = false) Long comment,
            @RequestParam(required = false) Long post,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
            ) {
        return service.getAll(comment, post, page, size);
    }

}













