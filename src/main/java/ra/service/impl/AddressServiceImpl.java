package ra.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ra.exception.SimpleException;
import ra.model.dto.request.AddressRequest;
import ra.model.entity.Address;
import ra.repository.IAddressRepository;
import ra.repository.IUserRepository;
import ra.service.IAddressService;


import java.util.List;

@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    public IAddressRepository addressRepository;
    @Autowired
    private IUserRepository userRepository;

    @Override
    public List<Address> findAll() throws SimpleException {
        return addressRepository.findAll();
    }

    @Override
    public Address save(AddressRequest addressRequest) throws SimpleException {
        if (addressRepository.existsByFullAddress(addressRequest.getFullAddress())) {
            throw new SimpleException("product name already exists: " +
                    addressRequest.getFullAddress(), HttpStatus.CONFLICT);
        }
        Address address = Address.builder()
                .fullAddress(addressRequest.getFullAddress())
                .phone(addressRequest.getPhone())
                .receiveName(addressRequest.getReceiveName())
                .user(userRepository.findById(addressRequest.getUserId())
                        .orElseThrow(()->new SimpleException( "User not found", HttpStatus.NOT_FOUND)))
                .build();
        return addressRepository.save(address);
    }

    @Override
    public Address findById(Integer addressId) throws SimpleException {
        return addressRepository.findById(addressId)
                .orElseThrow(()->new SimpleException("Not pound" + addressId, HttpStatus.NOT_FOUND));
    }

    @Override
    public void delete(Integer addressId) throws SimpleException {
        if (addressRepository.findById(addressId).isPresent()) {
            addressRepository.deleteById(addressId);
        }else {
            throw new SimpleException("Not pound" + addressId, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Address update(Integer addressId, AddressRequest addressRequest) throws SimpleException {
        // Kiểm tra sự tồn tại của address
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(()->new SimpleException("Not pound" + addressId, HttpStatus.NOT_FOUND));

        //Kiểm tra sự tồn tại của address
        if (addressRequest.getFullAddress()!=null && addressRequest.getFullAddress().equals(existingAddress.getFullAddress())) {
            throw new SimpleException("Address is existing: "
                    + addressRequest.getFullAddress(), HttpStatus.BAD_REQUEST);
        }
        // Cập nhật các thông tin sản phẩm
        existingAddress.setFullAddress(addressRequest.getFullAddress());
        existingAddress.setPhone(addressRequest.getPhone());
        existingAddress.setReceiveName(addressRequest.getReceiveName());
        existingAddress.setUser(userRepository.findById(addressRequest.getUserId())
                .orElseThrow(()->new SimpleException("User not found", HttpStatus.NOT_FOUND)));
        return addressRepository.save(existingAddress);
    }

    @Override
    public List<Address> findByUserId(Long userId) throws SimpleException {
        return addressRepository.findByUserUserId(userId);
    }

    @Override
    public Address findByIdAndUserId(Integer addressId, Long userId) {
        return addressRepository.findByAddressIdAndUserUserId(addressId, userId);
    }
}
