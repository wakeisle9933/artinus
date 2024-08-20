package com.art.main.artinus.repository;

import com.art.main.artinus.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByPhoneNumber(String phoneNumber);
}
