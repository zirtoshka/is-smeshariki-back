package itma.smesharikiback.controllers;

import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.requests.CommentRequest;
import itma.smesharikiback.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {
    private final CommentService service;

    public CommentController( CommentService service) {
        this.service = service;
    }

    @PostMapping(value  = "/api/comment/add")
    public Comment add(
            @RequestBody CommentRequest request
    ) {
        System.out.println(1);
            return service.create(request);
    }


}
