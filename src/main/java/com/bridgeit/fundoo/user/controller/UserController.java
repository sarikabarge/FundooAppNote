package com.bridgeit.fundoo.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgeit.fundoo.exception.UserException;
import com.bridgeit.fundoo.response.Response;
import com.bridgeit.fundoo.response.ResponseToken;
import com.bridgeit.fundoo.user.dto.LoginDTO;
import com.bridgeit.fundoo.user.dto.UserDTO;
import com.bridgeit.fundoo.user.model.User;
import com.bridgeit.fundoo.user.service.UserService;

@RequestMapping("/user")
@RestController
public class UserController {
	@Autowired
	UserService userService;

	@PostMapping("/register")
	public ResponseEntity<Response> register( @RequestBody @Valid UserDTO userDto)
			throws UserException, UnsupportedEncodingException {
		System.out.println("in controller");
		Response response = userService.onRegister(userDto);
		System.out.println(response);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping("/login")
	public ResponseEntity<ResponseToken> onLogin(  @RequestBody @Valid LoginDTO loginDTO)
			throws UserException, UnsupportedEncodingException {

		ResponseToken response = userService.onLogin(loginDTO);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/forgotpassword")
	public ResponseEntity<Response> forgotPassword( @RequestParam String emailId)
			throws UnsupportedEncodingException, UserException, MessagingException {
		System.out.println(emailId);
		Response status = userService.forgetPassword(emailId);

		return new ResponseEntity<Response>(status, HttpStatus.OK);

	}
	
	@PutMapping(value = "/resetpassword/{token}")
	public ResponseEntity<Response> resetPassword(@RequestParam String token, @RequestParam("password") String password)
			throws UserException {
		Response response = userService.resetPaswords(token, password);
		return new ResponseEntity<Response>(response, HttpStatus.OK);

	}
    
    @GetMapping(value = "/{token}/valid")
	public ResponseEntity<Response> emailValidation(@PathVariable String token) throws UserException {

		Response response = userService.validateEmailId(token);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
    
    @GetMapping("/getAllUser")
	List<User> getUser(@RequestHeader String token)
	{
		List<User> listuser=userService.getAllUser(token);
		return listuser;
	}
}