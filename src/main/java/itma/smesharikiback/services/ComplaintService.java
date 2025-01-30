package itma.smesharikiback.services;

import itma.smesharikiback.config.PaginationSpecification;
import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.*;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.models.reposirories.ComplaintRepository;
import itma.smesharikiback.models.reposirories.PostRepository;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import itma.smesharikiback.requests.ComplaintRequest;
import itma.smesharikiback.response.ComplaintResponse;
import itma.smesharikiback.response.PaginatedResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    private final SmesharikService smesharikService;
    private ComplaintRepository complaintRepository;

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
            Optional<Smesharik> admin = smesharikRepository.findById(complaintRequest.getAdmin());
            if (admin.isEmpty()) {
                errors.put("admin", "Admin не найден.");
                throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
            }
            if (!admin.get().getRole().equals(SmesharikRole.ADMIN)) {
                errors.put("admin", "User не является admin.");
                throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
            }
            complaint.setAdmin(admin.get());
        }

        if (Stream.of(
                complaintRequest.getComment(),
                complaintRequest.getPost()
        ).filter(Objects::nonNull).count() != 1) {
            errors.put("message", "Передать надо либо post, либо comment.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }

        if (complaintRequest.getPost() != null) {
            Optional<Post> post = postRepository.findById(complaintRequest.getPost());
            if (post.isEmpty()) {
                errors.put("message", "Post не был найден.");
                throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
            }
            complaint.setPost(post.get());
        } else {
            Optional<Comment> comment = commentRepository.findById(complaintRequest.getComment());
            if (comment.isEmpty()) {
                errors.put("message", "Comment не был найден.");
                throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
            }
            complaint.setComment(comment.get());
        }

        complaint.setCreationDate(complaintRequest.getCreationDate());
        complaint.setStatus(complaintRequest.getStatus());
        complaint.setClosingDate(complaintRequest.getClosingDate());
        complaint.setDescription(complaintRequest.getDescription());
        complaint.setViolationType(complaintRequest.getViolationType());
        return buildResponse(complaintRepository.save(complaint));
    }

    public ComplaintResponse getComplaint(Long id) {
        Complaint complaint = getComplaintById(id);
        return buildResponse(complaint);
    }

    private Complaint getComplaintById(Long id) {
        Optional<Complaint> complaint = complaintRepository.findById(id);
        HashMap<String, String> errors = new HashMap<>();

        if (complaint.isEmpty()) {
            errors.put("message", "Заявка не найдена.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }

        if (!smesharikService.getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN)) {
            errors.put("message", "Ошибка доступа.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }

        return complaint.get();
    }

    public PaginatedResponse<ComplaintResponse> getAllComplaints(
            String filter,
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
                PaginationSpecification.filterByMultipleFields(filter), pageRequest);


        List<ComplaintResponse> content = resultPage.getContent().stream()
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

    private ComplaintResponse buildResponse(Complaint complaint) {
        ComplaintResponse complaintResponse = new ComplaintResponse();
        if (complaint.getAdmin() != null) complaintResponse.setAdmin(complaint.getAdmin().getId());
        if (complaint.getComment() != null) complaintResponse.setComment(complaint.getComment().getId());
        if (complaint.getPost() != null) complaintResponse.setPost(complaint.getPost().getId());

        return complaintResponse
                .setId(complaint.getId())
                .setDescription(complaint.getDescription())
                .setStatus(complaint.getStatus())
                .setClosingDate(complaint.getClosingDate())
                .setCreationDate(complaint.getCreationDate())
                .setViolationType(complaint.getViolationType());
    }


}
