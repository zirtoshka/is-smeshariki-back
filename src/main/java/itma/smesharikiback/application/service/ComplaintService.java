package itma.smesharikiback.application.service;

import itma.smesharikiback.application.mapper.DomainMapper;
import itma.smesharikiback.infrastructure.specification.ComplaintSpecification;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.Comment;
import itma.smesharikiback.domain.model.Complaint;
import itma.smesharikiback.domain.model.GeneralStatus;
import itma.smesharikiback.domain.model.Post;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.model.SmesharikRole;
import itma.smesharikiback.domain.repository.CommentRepository;
import itma.smesharikiback.domain.repository.ComplaintRepository;
import itma.smesharikiback.domain.repository.PostRepository;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import itma.smesharikiback.presentation.dto.request.ComplaintRequest;
import itma.smesharikiback.presentation.dto.response.ComplaintResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ComplaintService {
    private final SmesharikRepository smesharikRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommonService commonService;
    private final ComplaintRepository complaintRepository;
    private final DomainMapper domainMapper;

    public ComplaintResponse createComplaint(@Validated ComplaintRequest complaintRequest) {
        Complaint complaint = new Complaint();
        return updateComplaintModel(complaint, complaintRequest);
    }

    public ComplaintResponse updateComplaint(Long id, @Validated ComplaintRequest complaintRequest) {
        Complaint complaint = getComplaintById(id);
        return updateComplaintModel(complaint, complaintRequest);
    }

    public ComplaintResponse updateComplaintModel(Complaint complaint, @Validated ComplaintRequest complaintRequest) {
        HashMap<String, String> errors = new HashMap<>();

        if (complaintRequest.getAdmin() != null) {
            Optional<Smesharik> admin = smesharikRepository.findByLogin(complaintRequest.getAdmin());
            if (admin.isEmpty()) {
                errors.put("admin", "Администратор не найден.");
                throw new ValidationException(errors);
            }
            if (!admin.get().getRole().equals(SmesharikRole.ADMIN)) {
                errors.put("admin", "Пользователь не является администратором.");
                throw new ValidationException(errors);
            }
            complaint.setAdmin(admin.get());
        } else {
            complaint.setAdmin(null);
        }

        if (Stream.of(
                complaintRequest.getComment(),
                complaintRequest.getPost()
        ).filter(Objects::nonNull).count() != 1) {
            errors.put("message", "Нужно указать либо post, либо comment.");
            throw new ValidationException(errors);
        }

        if (complaintRequest.getPost() != null) {
            Optional<Post> post = postRepository.findById(complaintRequest.getPost());
            if (post.isEmpty()) {
                errors.put("post", "Пост не найден.");
                throw new ValidationException(errors);
            }
            complaint.setPost(post.get());
            complaint.setComment(null);
        } else {
            Optional<Comment> comment = commentRepository.findById(complaintRequest.getComment());
            if (comment.isEmpty()) {
                errors.put("comment", "Комментарий не найден.");
                throw new ValidationException(errors);
            }
            complaint.setComment(comment.get());
            complaint.setPost(null);
        }

        complaint.setCreationDate(complaintRequest.getCreationDate());
        complaint.setStatus(complaintRequest.getStatus());
        complaint.setClosingDate(complaintRequest.getClosingDate());
        complaint.setDescription(complaintRequest.getDescription());
        complaint.setViolationType(complaintRequest.getViolationType());
        return domainMapper.toComplaintResponse(complaintRepository.save(complaint));
    }

    public ComplaintResponse getComplaint(Long id) {
        Complaint complaint = getComplaintById(id);
        return domainMapper.toComplaintResponse(complaint);
    }

    private Complaint getComplaintById(Long id) {
        Optional<Complaint> complaint = complaintRepository.findById(id);
        HashMap<String, String> errors = new HashMap<>();

        if (complaint.isEmpty()) {
            errors.put("message", "Жалоба не найдена.");
            throw new ValidationException(errors);
        }

        commonService.checkIfAdmin();

        return complaint.get();
    }

    public PaginatedResponse<ComplaintResponse> getAllComplaints(
            String description,
            List<GeneralStatus> statuses,
            Boolean isMine,
            String sortField,
            Boolean ascending,
            Integer page,
            Integer size
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                ascending ? Sort.by(sortField).ascending() : Sort.by(sortField).descending()
        );

        Page<Complaint> resultPage = complaintRepository.findAll(
                ComplaintSpecification.getComplaints(description, statuses, isMine, commonService.getCurrentSmesharik()),
                pageRequest);

        List<ComplaintResponse> content = resultPage.getContent().stream()
                .map(domainMapper::toComplaintResponse)
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
