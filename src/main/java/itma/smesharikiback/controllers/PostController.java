package itma.smesharikiback.controllers;

import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.response.PostResponse;
import itma.smesharikiback.response.PostWithCarrotsResponse;
import itma.smesharikiback.services.PostService;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {
    private PostService postService;

    @PostMapping(consumes = MULTIPART_FORM_DATA)
    public @NotNull PostResponse uploadImage(
            @RequestParam("imageFile") MultipartFile file,
            @RequestParam(required = false, defaultValue = "true") Boolean isDraft,
            @RequestParam(required = false, defaultValue = "true") Boolean pprivate,
            @RequestParam @Size(max = 4096, message = "Длина text до 4096.") @NotEmpty String text
    ) {
        return postService.create(file, isDraft, pprivate, text);
    }

    @GetMapping("/{id}")
    public @NotNull PostResponse get(@PathVariable Long id) {
        return postService.get(id);
    }

    @GetMapping("/feed")
    public @NotNull PaginatedResponse<PostWithCarrotsResponse> feed(
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(required = false, defaultValue = "publicationDate") String sortField,
            @RequestParam(required = false, defaultValue = "true") @NotNull Boolean ascending,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return postService.feed(filter, sortField, ascending, page, size);
    }

    @GetMapping("/download")
    public @NotNull ResponseEntity<?> downloadImage(@RequestParam String fileName) {
        return postService.downloadImage(fileName);
    }
}