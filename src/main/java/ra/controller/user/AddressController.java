package ra.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.exception.SimpleException;
import ra.model.dto.request.AddressRequest;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Address;
import ra.security.principle.MyUserDetails;
import ra.service.IAddressService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class AddressController {
    @Autowired
    private IAddressService addressService;


    @GetMapping("/getMyAllAddress")
    public ResponseEntity<?> getAllAddress() throws SimpleException {
        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId(); // Lấy userId từ thông tin người dùng

        // Lấy tất cả địa chỉ của người dùng từ dịch vụ
        List<Address> addressList = addressService.findByUserId(userId);

        // Trả về phản hồi với danh sách địa chỉ
        return ResponseEntity.ok().body(new SimpleResponse(addressList, HttpStatus.OK));
    }

    @GetMapping("/getAddressById/{addressId}")
    public ResponseEntity<?> getAddressById(@PathVariable Integer addressId) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        // Lấy địa chỉ từ dịch vụ theo ID và userId
        Address address = addressService.findByIdAndUserId(addressId, userId);
        if (address == null) {
            throw new SimpleException("Address not found or does not belong to the user", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(new SimpleResponse(address, HttpStatus.OK));
    }

    @PostMapping("/addAddress")
    public ResponseEntity<?> addAddress(@Valid @RequestBody AddressRequest addressRequest) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        // Thiết lập userId cho AddressRequest
        addressRequest.setUserId(userId);
        //ADD DIA CHI MOI
        Address savedAddress = addressService.save(addressRequest);
        return ResponseEntity.ok().body(new SimpleResponse(savedAddress, HttpStatus.CREATED));
    }


    @PutMapping("/updateAddress/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable Integer addressId, @Valid @RequestBody AddressRequest addressRequest) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Thiết lập userId cho AddressRequest
        addressRequest.setUserId(userId);

        // Xác nhận địa chỉ tồn tại và thuộc về người dùng
        Address address = addressService.findByIdAndUserId(addressId, userId);
        if (address == null) {
            throw new SimpleException("Address not found or does not belong to the user", HttpStatus.NOT_FOUND);
        }
        // Cập nhật địa chỉ
        Address updatedAddress = addressService.update(addressId, addressRequest);
        return ResponseEntity.ok().body(new SimpleResponse(updatedAddress, HttpStatus.OK));
    }

    @DeleteMapping("/deleteAddress/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Integer addressId) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        Address address = addressService.findByIdAndUserId(addressId, userId);
        if (address == null) {
            throw new SimpleException("Address not found or does not belong to the user", HttpStatus.NOT_FOUND);
        }
        addressService.delete(addressId);
        return ResponseEntity.ok().body(new SimpleResponse("Address deleted successfully", HttpStatus.OK));


    }

}
