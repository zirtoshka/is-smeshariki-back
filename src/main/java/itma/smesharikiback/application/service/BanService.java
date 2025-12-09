package itma.smesharikiback.application.service;

import itma.smesharikiback.application.mapper.DomainMapper;
import itma.smesharikiback.infrastructure.specification.PaginationSpecification;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.Ban;
import itma.smesharikiback.domain.model.Comment;
import itma.smesharikiback.domain.model.Post;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.repository.BanRepository;
import itma.smesharikiback.domain.repository.CommentRepository;
import itma.smesharikiback.domain.repository.PostRepository;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import itma.smesharikiback.presentation.dto.request.BanRequest;
import itma.smesharikiback.presentation.dto.response.BanResponse;
import itma.smesharikiback.presentation.dto.response.MessageResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kotlin.Triple;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final DomainMapper domainMapper;

    @Transactional
    public BanResponse create(BanRequest request) {
        Triple<Post, Comment, Smesharik> triple = getValues(request);

        Ban ban = new Ban();
        return saveBan(request, triple, ban);
    }

    @Transactional
    public MessageResponse delete(Long banId) {
        commonService.checkIfAdmin();
        HashMap<String, String> map = new HashMap<>();

        Optional<Ban> ban = banRepository.findById(banId);
        if (ban.isEmpty()) {
            map.put("ban", "Бан не найден.");
            throw new ValidationException(map);
        }

        banRepository.delete(ban.get());
        return new MessageResponse().setMessage("Бан удален!");
    }

    public BanResponse update(Long id, BanRequest request) {
        Triple<Post, Comment, Smesharik> triple = getValues(request);
        Ban ban = banRepository.getReferenceById(id);

        return saveBan(request, triple, ban);
    }

    private BanResponse saveBan(BanRequest request, Triple<Post, Comment, Smesharik> triple, Ban ban) {
        ban.setSmesharik(triple.getThird());
        ban.setPost(triple.getFirst());
        ban.setComment(triple.getSecond());
        ban.setReason(request.getReason());
        ban.setCreationDate(request.getCreationDate());
        ban.setEndDate(request.getEndDate());

        return domainMapper.toBanResponse(banRepository.save(ban));
    }

    private Triple<Post, Comment, Smesharik> getValues(BanRequest request) {
        commonService.checkIfAdmin();
        HashMap<String, String> map = new HashMap<>();

        if (Stream.of(
                request.getComment(),
                request.getPost(),
                request.getSmesharik()
        ).filter(Objects::nonNull).count() != 1) {
            map.put("message", "Нужно указать smesharik, post или comment.");
            throw new ValidationException(map);
        }

        Smesharik smesharik = null;
        Post post = null;
        Comment comment = null;
        if (request.getSmesharik() != null) {
            smesharik = smesharikRepository.findByLogin(request.getSmesharik()).orElse(null);
            if (smesharik == null) {
                map.put("smesharik", "Пользователь не найден.");
                throw new ValidationException(map);
            }
        } else if (request.getPost() != null) {
            post = postRepository.findById(request.getPost()).orElse(null);
            if (post == null) {
                map.put("post", "Пост не найден.");
                throw new ValidationException(map);
            }
        } else {
            comment = commentRepository.findById(request.getComment()).orElse(null);
            if (comment == null) {
                map.put("comment", "Комментарий не найден.");
                throw new ValidationException(map);
            }
        }

        return new Triple<>(post, comment, smesharik);
    }

    public BanResponse get(Long id) {
        commonService.checkIfAdmin();
        HashMap<String, String> map = new HashMap<>();

        Optional<Ban> ban = banRepository.findById(id);
        if (ban.isEmpty()) {
            map.put("message", "Бан не найден.");
            throw new ValidationException(map);
        }

        return domainMapper.toBanResponse(ban.get());
    }

    public PaginatedResponse<BanResponse> getAll(
            String filter,
            String sortField,
            @NotNull Boolean ascending,
            @Min(value = 0) Integer page,
            @Min(value = 3) @Max(value = 50) Integer size
    ) {
        commonService.checkIfAdmin();
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
                .map(domainMapper::toBanResponse)
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
