package oop.libapp.register;

import oop.libapp.exception.ResourceNotFoundException;

public interface IAuthorityService {
    Authority findByAuthority(String authority) throws ResourceNotFoundException;
    Authority findById(Long id) throws ResourceNotFoundException;
}
