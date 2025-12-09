package itma.smesharikiback.presentation.controller;

import itma.smesharikiback.presentation.dto.response.CarrotResponse;
import itma.smesharikiback.presentation.dto.response.MessageResponse;
import itma.smesharikiback.application.service.CarrotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/carrot")
@RequiredArgsConstructor
public class CarrotController {
    private final CarrotService carrotService;

    @PostMapping
    public CarrotResponse add(
            @RequestParam(required = false) Long comment,
            @RequestParam(required = false) Long post
    ) {
        return carrotService.create(post, comment);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(
            @RequestParam(required = false) Long comment,
            @RequestParam(required = false) Long post
    ) {
        return carrotService.delete(post, comment);
    }

    @GetMapping("/check")
    public MessageResponse checkLike(
            @RequestParam(required = false) Long comment,
            @RequestParam(required = false) Long post
    ) {
        return carrotService.check(post, comment);
    }
}













