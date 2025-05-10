package com.example.chatserver.member.repostiory;

import com.example.chatserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    //Optional<> -> 있을수도 있고, 없을수도 있다. -> isPresent()
    Optional<Member> findByEmail(String email);
}
