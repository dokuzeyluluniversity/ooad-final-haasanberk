package oop.libapp.register;

import oop.libapp.exception.ResourceNotFoundException;
import oop.libapp.register.exception.NewUserValidationException;
import oop.libapp.register.exception.UsernameAlreadyTakenException;
import oop.libapp.security.secret.ISecretGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@RestController
public class RegistrationController {

	private IUserService userService;
	private IAuthorityService authorityService;
	private ISecretGenerator secretGenerator;

	// New Design Pattern Implementation
	private FactoryUser factoryUser;
	private FactoryAuthority factoryAuthority;

	@Autowired
	public RegistrationController(IUserService userService, IAuthorityService authorityService,
			ISecretGenerator secretGenerator) {
		this.userService = userService;
		this.authorityService = authorityService;
		this.secretGenerator = secretGenerator;

		// New Design Pattern Implementation
		this.factoryUser = FactoryUser.getFactoryUser();
		this.factoryAuthority = FactoryAuthority.getFactoryAuthority();
	}

	@RequestMapping(value = "users/register", method = RequestMethod.POST)
	public ResponseEntity<?> registerUser(
			@Valid @RequestHeader(name = "Authorization", required = false) @RequestBody NewUserDto newUserDto,
			BindingResult result) throws NewUserValidationException, UsernameAlreadyTakenException {

		if (result.hasErrors()) {
			List<FieldError> fieldErrors = result.getFieldErrors();
			List<ObjectError> objectErrors = result.getGlobalErrors();

			throw new NewUserValidationException(fieldErrors, objectErrors);
		}

		String username = newUserDto.getUsername();
		String password = newUserDto.getPassword();
		Authority authority;

		try {
			authority = authorityService.findByAuthority("ROLE_USER");
		} catch (ResourceNotFoundException ex) {
			// default authority not found, so we have to create it


			// New Design Pattern Implementation
			authority = factoryAuthority.getAuthority("ROLE_USER");
		}

		try {
			User user = userService.findUserByUsername(username);
		} catch (ResourceNotFoundException ex) {
			// username given by the user can be taken, so we proceed
			Map<String, Boolean> response = new HashMap<>();

			// New Design Pattern Implementation
			User user = factoryUser.getUser();


			user.setPassword(password);
			user.setUsername(username);
			user.setAuthorities(new HashSet<>(Arrays.asList(authority)));
			user.setSecret(secretGenerator.generateSecret());

			User savedUser = userService.save(user);
			if (savedUser != null) {
				response.put("isRegistered", true);
			} else {
				response.put("isRegistered", false);
			}
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}

		throw new UsernameAlreadyTakenException("This username is already taken");
	}
}
