package itma.smesharikiback.controllers;

import itma.smesharikiback.response.CarrotResponse;
import itma.smesharikiback.services.CarrotService;
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
}
