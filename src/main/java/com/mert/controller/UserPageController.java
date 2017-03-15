package com.mert.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mert.model.User;
import com.mert.service.UserServiceImpl;
import com.mert.service.UserTaskService;

@Controller
@RequestMapping("/myprofile")
public class UserPageController {


	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private UserTaskService userTaskService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@RequestMapping(value = "/inf", method = RequestMethod.GET)
	public ModelAndView showProfile() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("rule", new User());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		//POINT=7 http://stackoverflow.com/questions/22364886/neither-bindingresult-nor-plain-target-object-for-bean-available-as-request-attr
		modelAndView.addObject("user", userService.findUser(user.getId()));
		modelAndView.addObject("mode", "MODE_INF");
		//--------------------------------------------
		User control = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("control", control.getRole().getRole());//Authentication for NavBar
		//---------------------------------------------
		modelAndView.setViewName("user_profile");
		return modelAndView;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView saveProfile(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView("redirect:/myprofile/inf");
		user.setPassword(userService.findUser(user.getId()).getPassword());
		user.setRole(userService.findUser(user.getId()).getRole());
		user.setActive(userService.findUser(user.getId()).getActive());
		userService.save(user);
		return modelAndView;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView updateProfile(@RequestParam int id) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("rule", new User());
		modelAndView.addObject("user", userService.findUser(id));
		modelAndView.addObject("mode", "MODE_EDIT");
		//--------------------------------------------
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User control = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("control", control.getRole().getRole());//Authentication for NavBar
		//---------------------------------------------
		modelAndView.setViewName("user_profile");
		return modelAndView;
	}


	@RequestMapping(value = "/görevlerim", method = RequestMethod.GET)
	public ModelAndView showMyTask(@RequestParam int id) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("rule", new User());
		modelAndView.addObject("user", userService.findUser(id));
		modelAndView.addObject("usertasks", userTaskService.findAll());
		modelAndView.addObject("mode", "MODE_TASKS");
		//--------------------------------------------
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User control = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("control", control.getRole().getRole());//Authentication for NavBar
		//---------------------------------------------
		modelAndView.setViewName("user_profile");
		return modelAndView;
	}


	//--------------------------------------------------------------------------------------------------------

	@RequestMapping(value = "/savepass_change", method = RequestMethod.POST)
	public ModelAndView confirmPasswordChange(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		
		String oldPassword = userService.findUser(user.getId()).getPassword();
		String postOldPassword =user.getName(); 

		System.out.println(oldPassword + " ---- " +postOldPassword+"  ----- "+ user.getPassword()) ;
		if(passwordEncoder.matches(postOldPassword, oldPassword)){
			
			user.setName(userService.findUser(user.getId()).getName());
			user.setEmail(userService.findUser(user.getId()).getEmail());
			user.setRole(userService.findUser(user.getId()).getRole());
			user.setActive(userService.findUser(user.getId()).getActive());
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			userService.save(user);
			modelAndView.addObject("user", userService.findUser(user.getId()));
			modelAndView.addObject("mode", "MODE_PASS");
			//--------------------------------------------
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User control = userService.findUserByEmail(auth.getName());
			modelAndView.addObject("control", control.getRole().getRole());//Authentication for NavBar
			//---------------------------------------------
			modelAndView.addObject("rule", new User());
			modelAndView.addObject("pw_success", "Şifreler eşleşti");
			modelAndView.setViewName("user_profile");
		}
		else {
			
			modelAndView.addObject("user", userService.findUser(user.getId()));
			modelAndView.addObject("mode", "MODE_PASS");
			modelAndView.addObject("pw_error", "Error : Şifreler uyuşmuyor");
			modelAndView.addObject("rule", new User());
			modelAndView.setViewName("user_profile");
			
		}

		return modelAndView;
	}

	@RequestMapping(value = "/change_password", method = RequestMethod.GET)
	public ModelAndView changePassword(@RequestParam int id) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("rule", new User());
		modelAndView.addObject("user", userService.findUser(id));
		modelAndView.addObject("mode", "MODE_PASS");
		//--------------------------------------------
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User control = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("control", control.getRole().getRole());//Authentication for NavBar
		//---------------------------------------------
		modelAndView.setViewName("user_profile");
		return modelAndView;
	}


}







