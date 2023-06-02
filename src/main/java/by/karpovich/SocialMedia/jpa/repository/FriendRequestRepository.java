package by.karpovich.SocialMedia.jpa.repository;

import by.karpovich.SocialMedia.jpa.entity.FriendRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {

    @Query("""
            SELECT f FROM FriendRequestEntity f WHERE f.receiver.id = :userId AND f.id = :friendRequestId
                        """)
    FriendRequestEntity findFriendRequestByIdFromUser(Long userId, Long friendRequestId);

    @Query("""
            SELECT f FROM FriendRequestEntity f WHERE f.sender.id = :senderId AND f.receiver.id = :receiverId       
                            """)
    FriendRequestEntity findFriendRequestByUserIdByReceiverId(Long senderId, Long receiverId);


    @Query("""
            SELECT f FROM FriendRequestEntity f WHERE f.id = :friendRequestId AND f.receiver.id = :receiverId   
                             """)
    FriendRequestEntity findFriendRequestFromUserByRequestId(Long friendRequestId, Long receiverId);


}
