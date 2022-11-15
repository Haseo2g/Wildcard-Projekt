package br.com.datahub.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.datahub.entity.Audit;
import br.com.datahub.service.AuditService;

@Controller
@RequestMapping("audit")
public class AuditView {

	@Autowired
	AuditService auditService;
	
	@RequestMapping(value = "/list")
	public ModelAndView databaseList() {
		ModelAndView mav = new ModelAndView("pages/audit/list");
		List<Audit> audits = auditService.findAll();

		mav.addObject("audits", audits);
		return mav;
	}
}
