package by.karpovich.SocialMedia.jpa.repository;

import by.karpovich.SocialMedia.jpa.entity.FriendRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {
}
