package itma.smesharikiback.services;

import itma.smesharikiback.specification.PaginationSpecification;
import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.*;
import itma.smesharikiback.models.reposirories.BanRepository;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.models.reposirories.PostRepository;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import itma.smesharikiback.requests.BanRequest;
import itma.smesharikiback.response.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kotlin.Triple;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@AllArgsConstructor
public class BanService {

    private final SmesharikRepository smesharikRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final BanRepository banRepository;
    private final CommonService commonService;

    @Transactional
    public BanResponse create(BanRequest request) {
        Triple<Post, Comment, Smesharik> triple = getValues(request);

        Ban ban = new Ban();
        return getBanResponse(request, triple, ban);
    }

    @Transactional
    public MessageResponse delete(Long ban) {
        HashMap<String, String> map = new HashMap<>();

        if (!commonService.getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN)) {
            map.put("message", "Отказано в доступе.");
            throw new GeneralException(HttpStatus.FORBIDDEN, map);
        }

        Optional<Ban> ban1 = banRepository.findById(ban);
        if (ban1.isEmpty()) {
            map.put("ban", "Бан не был найден.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }

        banRepository.delete(ban1.get());
        return new MessageResponse().setMessage("Бан удален!");
    }

    private BanResponse buildResponse(Ban ban) {
        BanResponse response = new BanResponse();
        if (ban.getSmesharik() != null) response.setSmesharik(ban.getSmesharik().getLogin());
        if (ban.getPost() != null) response.setPost(ban.getPost().getId());
        if (ban.getComment() != null) response.setComment(ban.getComment().getId());

        return response
                .setId(ban.getId())
                .setReason(ban.getReason())
                .setEndDate(ban.getEndDate())
                .setCreationDate(ban.getCreationDate());
    }


    public BanResponse update(Long id, BanRequest request) {
        Triple<Post, Comment, Smesharik> triple = getValues(request);
        Ban ban = banRepository.getReferenceById(id);

        return getBanResponse(request, triple, ban);
    }

    private BanResponse getBanResponse(BanRequest request, Triple<Post, Comment, Smesharik> triple, Ban ban) {
        ban.setSmesharik(triple.getThird());
        ban.setPost(triple.getFirst());
        ban.setComment(triple.getSecond());
        ban.setReason(request.getReason());
        ban.setCreationDate(request.getCreationDate());
        ban.setEndDate(request.getEndDate());

        return buildResponse(banRepository.save(ban));
    }

    private Triple<Post, Comment, Smesharik> getValues(BanRequest request) {
        HashMap<String, String> map = new HashMap<>();

        if (!commonService.getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN)) {
            map.put("message", "Отказано в доступе.");
            throw new GeneralException(HttpStatus.FORBIDDEN, map);
        }

        if (Stream.of(
                request.getComment(),
                request.getPost(),
                request.getSmesharik()
        ).filter(Objects::nonNull).count() != 1) {
            map.put("message", "Некорректный запрос.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }

        Smesharik smesharik = null;
        Post post = null;
        Comment comment = null;
        if (request.getSmesharik() != null) {
            smesharik = smesharikRepository.findByLogin(request.getSmesharik()).orElse(null);
        } else if (request.getPost() != null) {
            post = postRepository.findById(request.getPost()).orElse(null);
        } else {
            comment = commentRepository.findById(request.getComment()).orElse(null);
        }

        return new Triple<>(post, comment, smesharik);
    }

    public BanResponse get(Long id) {
        HashMap<String, String> map = new HashMap<>();

        if (!commonService.getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN)) {
            map.put("message", "Отказано в доступе.");
            throw new GeneralException(HttpStatus.FORBIDDEN, map);
        }

        Optional<Ban> ban = banRepository.findById(id);
        if (ban.isEmpty()) {
            map.put("message", "Бан не был найден.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }

        return buildResponse(ban.get());
    }

    public PaginatedResponse<BanResponse> getAll(
            String filter,
            String sortField,
            @NotNull Boolean ascending,
            @Min(value = 0) Integer page,
            @Min(value = 3) @Max(value = 50) Integer size
    ) {
        HashMap<String, String> map = new HashMap<>();

        if (!commonService.getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN)) {
            map.put("message", "Отказано в доступе.");
            throw new GeneralException(HttpStatus.FORBIDDEN, map);
        }

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                ascending ? Sort.by(sortField).ascending() : Sort.by(sortField).descending()
        );

        Page<Ban> resultPage = banRepository.findAll(
                PaginationSpecification.filterByMultipleFields(filter),
                pageRequest
        );

        List<BanResponse> content = resultPage.getContent().stream()
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
}
