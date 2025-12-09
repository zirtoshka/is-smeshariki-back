package itma.smesharikiback.domain.repository;

import itma.smesharikiback.domain.model.Complaint;
import itma.smesharikiback.domain.model.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>, JpaSpecificationExecutor<Complaint> {
    List<Complaint> findByAdmin(Smesharik admin);
    List<Complaint> findByPostId(Long postId);
    List<Complaint> findByCommentId(Long commentId);
    List<Complaint> findByStatus(String status);
}












