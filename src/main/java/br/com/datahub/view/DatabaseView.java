package br.com.datahub.view;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.datahub.controller.DatabaseController;
import br.com.datahub.entity.Database;
import br.com.datahub.service.DatabaseService;
import br.com.datahub.util.Util;

@Controller
@RequestMapping("database")
public class DatabaseView {

	@Autowired
	DatabaseService databaseService;

	@Autowired
	DatabaseController databaseController;
	
	Util util = new Util();

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/insert")
	public ModelAndView databaseInsert() {
		ModelAndView mav = new ModelAndView("pages/database/save");
		Util util = new Util();
		mav.addObject("function", "insert");
		mav.addObject("database", new Database());
		mav.addObject("connections", util.getAllConnectionType());
		return mav;
	}

	/**
	 * 
	 * @param idDatabase
	 * @return
	 */
	@RequestMapping(value = "/update")
	public ModelAndView databaseUpdate(@RequestParam(required = true, value = "idDatabase") long idDatabase) {
		ModelAndView mav = new ModelAndView("pages/database/save");
		Optional<Database> database = databaseService.findById(idDatabase);

		if (database.isPresent()) {
			mav.addObject("function", "update");
			mav.addObject("database", database);
			Util util = new Util();
			mav.addObject("connections", util.getAllConnectionType());
			return mav;
		}
		return databaseInsert();
	}

	/**
	 * Method responsible for returning a page with all availables databases 
	 * 
	 * @return a list of available queries
	 */
	@RequestMapping(value = "/list")
	public ModelAndView databaseList() {
		ModelAndView mav = new ModelAndView("pages/database/list");
		List<Database> databases = databaseService.findAll();

		mav.addObject("databases", databases);
		return mav;
	}

	/**
	 * Method responsible for inserting a new user
	 * 
	 * @param database
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView savePost(@Valid @ModelAttribute Database database, BindingResult binding, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView();
		ResponseEntity<?> response = databaseController.insert(database);
		mav = databaseList();
		mav.setViewName("redirect:/database/list");

		redirectAttributes = util.returnAlert(redirectAttributes, response);
		
		return mav;
	}

	/**
	 * Method responsible for updating a user
	 * 
	 * @param database
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public ModelAndView savePut(@Valid @ModelAttribute Database database, BindingResult binding, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView();
		ResponseEntity<?> response = databaseController.update(database);
		mav = databaseList();
		mav.setViewName("redirect:/database/list");

		redirectAttributes = util.returnAlert(redirectAttributes, response);
		
		return mav;
	}

	/**
	 * Method responsible for deleting a database
	 * 
	 * @param idDatabase
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ModelAndView delete(long idDatabase, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView();
		ResponseEntity<?> response = databaseController.delete(idDatabase);
		mav = databaseList();
		mav.setViewName("redirect:/database/list");
		
		redirectAttributes = util.returnAlert(redirectAttributes, response);
		
		return mav;
	}
}
