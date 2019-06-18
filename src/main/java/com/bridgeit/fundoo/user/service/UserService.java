package com.bridgeit.fundoo.user.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bridgeit.fundoo.exception.UserException;
import com.bridgeit.fundoo.response.Response;
import com.bridgeit.fundoo.response.ResponseToken;
import com.bridgeit.fundoo.user.dto.LoginDTO;
import com.bridgeit.fundoo.user.dto.UserDTO;
import com.bridgeit.fundoo.user.model.User;


@Service
public interface UserService {

	Response onRegister(UserDTO userDto) throws UserException, UnsupportedEncodingException;

	ResponseToken onLogin(LoginDTO loginDto) throws UserException, UnsupportedEncodingException;

	ResponseToken authentication(Optional<User> user, String password);

	Response validateEmailId(String token);
	
	Response forgetPassword(String emailId) throws UserException, UnsupportedEncodingException;
	
	Response resetPaswords(String token, String password);
	
	List<User> getAllUser(String token);
}
