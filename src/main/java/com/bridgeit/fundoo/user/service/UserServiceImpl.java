package com.bridgeit.fundoo.user.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgeit.fundoo.exception.UserException;
import com.bridgeit.fundoo.response.Response;
import com.bridgeit.fundoo.response.ResponseToken;
import com.bridgeit.fundoo.user.dto.LoginDTO;
import com.bridgeit.fundoo.user.dto.UserDTO;
import com.bridgeit.fundoo.user.model.User;
import com.bridgeit.fundoo.user.repository.UserRepository;
import com.bridgeit.fundoo.utility.ResponseHelper;
import com.bridgeit.fundoo.utility.TokenUtil;
import com.bridgeit.fundoo.utility.Utility;


@PropertySource("classpath:message.properties")
@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private Response statusResponse;

	@Autowired
	private Environment environment;

	@Override
	public Response onRegister(UserDTO userDto) {
		System.out.println("in service");
		String emailid = userDto.getEmailId();
		System.out.println(emailid);
		User user = modelMapper.map(userDto, User.class);
		Optional<User> useralreadyPresent = userRepo.findByEmailId(user.getEmailId());
		if (useralreadyPresent.isPresent()) {
			throw new UserException(environment.getProperty("status.register.emailExistError"));
		}
		// encode user password
		String password = passwordEncoder.encode(userDto.getPassword());

		user.setPassword(password);
		user = userRepo.save(user);
		Long userId = user.getUserId();
		System.out.println(emailid + " " + userId);
		Utility.send(emailid, "confirmation mail", Utility.getUrl(userId) + "/valid");
      
		statusResponse = ResponseHelper.statusResponse(200, "register successfully");
		return statusResponse;

	}

	@Override
	public ResponseToken onLogin(LoginDTO loginDto) throws UserException, UnsupportedEncodingException {
		Optional<User> user = userRepo.findByEmailId(loginDto.getEmailId());
		ResponseToken response = new ResponseToken();
		if (user.isPresent()) {

			return authentication(user, loginDto.getPassword());

		}
		else
		{
		response.setStatusCode(100);
		response.setToken(null);
		response.setStatusMessage("email id not exist");
		return response;
		
		}

	}

	@Override
	public ResponseToken authentication(Optional<User> user, String password) {

		ResponseToken response = new ResponseToken();
		if (user.get().isVerify()) {
			boolean status = passwordEncoder.matches(password, user.get().getPassword());

			if (status == true) {
				String token = tokenUtil.createToken(user.get().getUserId());
				response.setToken(token);
				response.setStatusCode(200);
				response.setStatusMessage(environment.getProperty("user.login"));
				return response;
			}

			throw new UserException(401, environment.getProperty("user.login.password"));
		}

		throw new UserException(401, environment.getProperty("user.login.verification"));
	
	}

	@Override
	public Response validateEmailId(String token) {
		long id = tokenUtil.decodeToken(token);
		User user = userRepo.findById(id)
		.orElseThrow(() -> new UserException(404, environment.getProperty("user.validation.email")));
		user.setVerify(true);
		userRepo.save(user);
		statusResponse = ResponseHelper.statusResponse(200, environment.getProperty("user.validation"));
		
		return statusResponse;
	}
	
	@Override
	public Response forgetPassword(String emailId) throws UserException, UnsupportedEncodingException {
		Optional<User> useralreadyPresent = userRepo.findByEmailId(emailId);
                                                                                                           
		if (!useralreadyPresent.isPresent()) {

			throw new UserException(401, environment.getProperty("user.forgetpassword.emaiId"));
		}
		Long id = useralreadyPresent.get().getUserId();
		Utility.send(emailId, "password reset mail", Utility.getUrl(id));
		return ResponseHelper.statusResponse(200, environment.getProperty("user.forgetpassword.link"));
	}
	
	@Override
	public Response resetPaswords(String token, String password) {
		Long id = tokenUtil.decodeToken(token);
		System.out.println(id);
		User user = userRepo.findById(id)
				.orElseThrow(() -> new UserException(404, environment.getProperty("user.resetpassword.user")));
		System.out.println(user);
		
		String encodedpassword = passwordEncoder.encode(password);
		user.setPassword(encodedpassword);
		userRepo.save(user);
		return ResponseHelper.statusResponse(200, "password successfully reset");

	}
	
	public List<User> getAllUser(String token)
	{
		long userId=tokenUtil.decodeToken(token);
		Optional<User> user = userRepo.findById(userId);
		
		if(!user.isPresent())
		{
			throw new UserException("invalid input");
		}
		
		List<User> users=userRepo.findByUserId(userId);
		List<User> listuser= new ArrayList<>();
		
		for(User userlist:users)
		{
			User userdto = modelMapper.map(userlist,User.class);
			listuser.add(userdto);
			
		}
		
		return listuser;
	}


}


