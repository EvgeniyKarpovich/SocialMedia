package by.karpovich.SocialMedia.jpa.repository;

import by.karpovich.SocialMedia.jpa.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, PagingAndSortingRepository<PostEntity, Long> {

    @Query("""
            SELECT p FROM PostEntity p JOIN p.user u JOIN u.subscriptions s WHERE s.id = :userId
            """)
    Page<PostEntity> findAll(Pageable pageable, Long userId);
}
