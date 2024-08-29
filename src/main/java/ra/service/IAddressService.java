package ra.service;

import ra.exception.SimpleException;
import ra.model.dto.request.AddressRequest;
import ra.model.entity.Address;

import java.util.List;

public interface IAddressService {
    List<Address> findAll()throws SimpleException;
    Address save(AddressRequest addressRequest) throws SimpleException;
    Address findById(Integer addressId) throws SimpleException;
    void delete(Integer addressId) throws SimpleException;
    Address update(Integer addressId,AddressRequest addressRequest) throws SimpleException;
    List<Address> findByUserId(Long userId) throws SimpleException;
    Address findByIdAndUserId(Integer addressId, Long userId);
}
