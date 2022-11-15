package br.com.datahub.view;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.datahub.controller.ProfileController;
import br.com.datahub.controller.UserController;
import br.com.datahub.entity.Role;
import br.com.datahub.entity.User;
import br.com.datahub.service.RoleService;
import br.com.datahub.service.UserService;
import br.com.datahub.util.Util;

@Controller
@RequestMapping(value = "/user")
public class UserView {

	@Autowired
	RoleService roleService;

	@Autowired
	UserService userService;

	@Autowired
	UserController userController;

	@Autowired
	ProfileController profileController;

	Util util = new Util();

	/**
	 * Method responsible for returning the insert user page
	 * 
	 * @return a ModelAndView with the view that it will be load
	 */
	@RequestMapping(value = "/insert")
	public ModelAndView insert() {
		ModelAndView mav = new ModelAndView("pages/user/save");

		List<Role> roles = roleService.findAll();
		mav.addObject("function", "insert");
		mav.addObject("user", new User());
		mav.addObject("roles", roles);
		return mav;
	}

	/**
	 * Method responsible for returning the update page
	 * 
	 * @param idUser
	 * @return the update page
	 */
	@RequestMapping(value = "/update")
	public ModelAndView update(@RequestParam(required = true, value = "idUser") long idUser) {
		ModelAndView mav = insert();
		Optional<User> user = userService.findById(idUser);

		if (user.isPresent()) {
			mav.addObject("function", "update");
			mav.addObject("user", user.get());
			return mav;
		}
		return mav;
	}

	/**
	 * Method responsible for listing all available users
	 * 
	 * @return the list of all available users
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView("pages/user/list");
		List<User> users = userService.findAll();

		mav.addObject("users", users);
		return mav;
	}

	/**
	 * Method responsible for inserting a new user
	 * 
	 * @param user
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView savePost(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView();
		ResponseEntity<?> response = userController.insert(user);
		mav = list();
		mav.setViewName("redirect:/user/list");

		redirectAttributes = util.returnAlert(redirectAttributes, response);

		return mav;
	}

	/**
	 * Method responsible for updating a user
	 * 
	 * @param user
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public ModelAndView savePut(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView();
		ResponseEntity<?> response = userController.update(user);
		mav = list();
		mav.setViewName("redirect:/user/list");

		redirectAttributes = util.returnAlert(redirectAttributes, response);

		return mav;
	}

	/**
	 * Method responsible for deleting a user
	 * 
	 * @param idUser
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ModelAndView delete(long idUser, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView("redirect:/user/list");
		ResponseEntity<?> response = userController.delete(idUser);

		redirectAttributes = util.returnAlert(redirectAttributes, response);

		return mav;
	}
}
