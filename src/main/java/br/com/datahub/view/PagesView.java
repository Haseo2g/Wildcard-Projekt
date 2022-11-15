package br.com.datahub.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PagesView {

	@Autowired
	QueryView queryView;

	@RequestMapping(value = "/")
	public ModelAndView index() {
		return new ModelAndView("index");
	}

	@RequestMapping(value = "/home")
	public ModelAndView home() {
		return queryView.queryList();
	}
}
