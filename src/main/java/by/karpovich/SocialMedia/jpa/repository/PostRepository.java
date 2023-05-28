package by.karpovich.SocialMedia.jpa.repository;

import by.karpovich.SocialMedia.jpa.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
