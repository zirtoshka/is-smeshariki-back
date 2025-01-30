package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Complaint;
import itma.smesharikiback.models.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>, JpaSpecificationExecutor<Complaint> {
    List<Complaint> findByAdmin(Smesharik admin);
    List<Complaint> findByPostId(Long postId);
    List<Complaint> findByCommentId(Long commentId);
    List<Complaint> findByStatus(String status);
}