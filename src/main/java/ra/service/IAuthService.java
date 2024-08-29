package ra.service;

import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.FormLogin;
import ra.model.dto.request.FormRegister;
import ra.model.dto.response.JwtResponse;

public interface IAuthService {
    void register(FormRegister formRegister) throws CustomException, SimpleException;
    JwtResponse login(FormLogin formLogin) throws CustomException, SimpleException;
}
