package itma.smesharikiback.services;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import itma.smesharikiback.config.PaginationSpecification;
import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.reposirories.PostRepository;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.response.PostResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final SmesharikService smesharikService;
    private final PostRepository postRepository;
    private MinioClient minioClient;
    private String bucketName;

    public PostResponse create(MultipartFile file, Boolean isDraft, Boolean pprivate, String text) {
        String fileName = uploadImage(file);
        Post post = new Post();
        post.setAuthor(smesharikService.getCurrentSmesharik());
        post.setCreationDate(new Timestamp(new Date().getTime()).toLocalDateTime());
        post.setText(text);
        post.setIsDraft(isDraft);
        post.setIsPrivate(pprivate);
        post.setPathToImage(fileName);
        if (!isDraft) post.setPublicationDate(new Timestamp(new Date().getTime()).toLocalDateTime());
        return buildResponse(postRepository.save(post));
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "Post не найден.");
            throw new GeneralException(HttpStatus.NOT_FOUND, map);
        }
        return buildResponse(post);
    }

    public @NotNull PaginatedResponse<PostResponse> getAll(
            String filter,
            String sortField,
            @NotNull Boolean ascending,
            @Min(value = 0) Integer page,
            @Min(value = 3) @Max(value = 50) Integer size
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                ascending ? Sort.by(sortField).ascending() : Sort.by(sortField).descending()
        );

        Page<Post> resultPage = postRepository.findAll(
                PaginationSpecification.filterByMultipleFields(filter), pageRequest);

        List<PostResponse> content = resultPage.getContent().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                resultPage.getTotalPages(),
                resultPage.getTotalElements(),
                resultPage.getNumber(),
                resultPage.getSize()
        );
    }

    public PostResponse buildResponse(Post post) {
        return new PostResponse()
                .setId(post.getId())
                .setAuthor(post.getAuthor().getId())
                .setCreationDate(post.getCreationDate())
                .setText(post.getText())
                .setIsDraft(post.getIsDraft())
                .setIsPrivate(post.getIsPrivate())
                .setPathToImage(post.getPathToImage())
                .setPublicationDate(post.getPublicationDate());
    }

    public String uploadImage(MultipartFile file) {
        HashMap<String, String> errors = new HashMap<>();
        imageValid(file);
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null ?
                originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
        String uniqueFileName = UUID.randomUUID() + fileExtension;
        try {

            try (InputStream inputStream = file.getInputStream()) {
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uniqueFileName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build();

                minioClient.putObject(putObjectArgs);
            }

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            errors.put("imageFile", "Ошибка при сохранении файла.");
            throw new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, errors);
        }

        return uniqueFileName;
    }

    public ResponseEntity<?> downloadImage(String fileName) {
        HashMap<String, String> errors = new HashMap<>();

        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            byte[] content = inputStream.readAllBytes();


            return ResponseEntity.ok()
                    .body(content);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            errors.put("imageFile", "Ошибка при сохранении файла.");
            throw new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, errors);
        }
    }

    private void imageValid(MultipartFile file) {
        HashMap<String, String> errors = new HashMap<>();

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            errors.put("imageFile", "Файл не является изображением.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                errors.put("imageFile", "Файл не является валидным изображением.");
                throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
            }
        } catch (IOException e) {
            errors.put("imageFile", "Ошибка при чтении файла.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }
    }


}
