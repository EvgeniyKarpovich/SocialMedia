package by.karpovich.SocialMedia.jpa.repository;

import by.karpovich.SocialMedia.jpa.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("""
            SELECT m from  MessageEntity m  where m.sender.id =  :senderId and m.receiver.id = :receiverId
            """)
    List<MessageEntity> mesBySenId(Long senderId, Long receiverId);

    List<MessageEntity> findBySenderIdAndReceiverId(Long senderId, Long ReceiverId);
}
