package oop.libapp.register;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findAuthorityByAuthority(String authorityName);
}
