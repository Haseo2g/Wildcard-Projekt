package br.com.datahub.view;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.datahub.controller.ExceptionHandler;
import br.com.datahub.controller.QueryController;
import br.com.datahub.entity.Column;
import br.com.datahub.entity.Database;
import br.com.datahub.entity.Query;
import br.com.datahub.service.DatabaseService;
import br.com.datahub.service.QueryService;
import br.com.datahub.util.Util;

@Controller
@RequestMapping(value = "/query")
public class QueryView {

	@Autowired
	QueryService queryService;

	@Autowired
	QueryController queryController;

	@Autowired
	DatabaseService databaseService;
	
	@Autowired
	ExceptionHandler exceptionHandler;

	Util util = new Util();

	/**
	 * Method responsible for returning a list of query available 
	 * 
	 * @param idQuery
	 * @param params
	 * @return a page with a list of queries availables
	 */
	@RequestMapping(value = "/execute")
	public ModelAndView queryList(@Param(value = "idQuery") long idQuery, @Param(value = "params") String params) {
		ModelAndView mav = new ModelAndView("pages/query/response");
		try {
			List<List<String>> table = queryService.loadList(idQuery, params);
			Map<String, Column> headers = queryService.loadHeaders(idQuery);
			if (headers != null && !headers.isEmpty()) {
				mav.addObject("headers", headers);
				mav.addObject("table", table);
				Query query = queryService.findById(idQuery).get();
				mav.addObject("title", query.getName());
				mav.addObject("message", "Tabela carregada com sucesso!");
			}
		} catch (Exception e) {
			mav.addObject("message", "Não foi possível localizar retornos na pesquisa: " + exceptionHandler.getExceptionMessage(e));
		}
		return mav;
	}
	
	/**
	 * Methods responsible for return a query insert preview 
	 * 
	 * @param query
	 * @return a preview of a query to insert a new query 
	 */
	@RequestMapping(value = "/preview", method = RequestMethod.POST)
	public ModelAndView previewInsert(Query query) {
		ModelAndView mav = queryInsert();

		mav = previewBase(query, mav);
		return mav;
	}
	
	/**
	 * Methods responsible for return a query update preview 
	 * 
	 * @param query
	 * @return a preview of a query to update a existing query
	 */
	@RequestMapping(value = "/preview", method = RequestMethod.PUT)
	public ModelAndView previewUpdate(Query query) {
		ModelAndView mav = queryUpdate(query.getId());

		mav = previewBase(query, mav);
		
		return mav;
	}

	/**
	 * Method responsible for returning a page with all available filters of a query response
	 * 
	 * @param idQuery
	 * @return a query's filters page
	 */
	@RequestMapping(value = "/filter")
	public ModelAndView queryFilterPreview(@Param(value = "idQuery") long idQuery) {
		ModelAndView mav = new ModelAndView("pages/query/filter_preview");
		try {
			Map<String, Column> headers = queryService.loadHeaders(idQuery);
			if (headers != null && !headers.isEmpty()) {
				mav.addObject("headers", headers);
				mav.addObject("idQuery", idQuery);
				mav.addObject("message", "Selecione os filtros: ");
			}
		} catch (Exception e) {
			mav.addObject("message", "Não foi possível localizar retornos na pesquisa: " + exceptionHandler.getExceptionMessage(e));
		}
		return mav;
	}

	/**
	 * Method responsible for return the query insert page 
	 * 
	 * @return the query insert page
	 */
	@RequestMapping(value = "/insert")
	public ModelAndView queryInsert() {
		ModelAndView mav = new ModelAndView("pages/query/save");

		mav.addObject("function", "insert");
		mav.addObject("query", new Query());

		List<Database> databases = databaseService.findAll();
		mav.addObject("databases", databases);
		return mav;
	}

	/**
	 * Method responsible for returning the query update page
	 * 
	 * @param idQuery
	 * @return the query update page 
	 */
	@RequestMapping(value = "/update")
	public ModelAndView queryUpdate(@Param(value = "idQuery") long idQuery) {
		ModelAndView mav = new ModelAndView("pages/query/save");
		mav.addObject("function", "update");
		Optional<Query> query = queryService.findById(idQuery);

		if (query.isPresent()) {
			mav.addObject("query", query);
			List<Database> databases = databaseService.findAll();
			mav.addObject("databases", databases);
			return mav;
		}
		return queryInsert();
	}

	/**
	 * Method responsible for returning a page with all available queries
	 * 
	 * @return list of available queries
	 */
	@RequestMapping(value = "/list")
	public ModelAndView queryList() {
		ModelAndView mav = new ModelAndView("pages/query/list");
		List<Query> queries = queryService.findAll();

		mav.addObject("queries", queries);
		return mav;
	}

	/**
	 * Method responsible for inserting a new query
	 * 
	 * @param query
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView savePost(@Valid @ModelAttribute Query query, BindingResult binding, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView();
		ResponseEntity<?> response = queryController.insert(query);
		mav = queryList();
		mav.setViewName("redirect:/query/list");

		redirectAttributes = util.returnAlert(redirectAttributes, response);

		return mav;
	}

	/**
	 * Method responsible for updating a query
	 * 
	 * @param query
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public ModelAndView savePut(@Valid @ModelAttribute Query query, BindingResult binding, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView();
		ResponseEntity<?> response = queryController.update(query);
		mav = queryList();
		mav.setViewName("redirect:/query/list");

		redirectAttributes = util.returnAlert(redirectAttributes, response);

		return mav;
	}

	/**
	 * Method responsible for deleting a query
	 * 
	 * @param idQuery
	 * @param redirectAttributes
	 * @return to redirect page
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ModelAndView delete(long idQuery, RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView("redirect:/query/list");
		ResponseEntity<?> response = queryController.delete(idQuery);

		redirectAttributes = util.returnAlert(redirectAttributes, response);

		return mav;
	}
	
	/**
	 * Method responsible for return a view with all necessary objects
	 * 
	 * @param query
	 * @param mav
	 * @return a view with table objects
	 */
	private ModelAndView previewBase(Query query, ModelAndView mav) {
		try {
			mav.addObject("query", query);
			
			Query queryPreview = new Query();
			queryPreview.setQueryLine(query.getQueryLine());
			queryPreview.setDatabase(query.getDatabase());
			queryPreview.setQueryLine(queryService.getQueryPreview(queryPreview.getQueryLine()));

			Map<String, Column> headers = queryService.loadHeaders(queryPreview);
			if (headers != null && !headers.isEmpty()) {
				mav.addObject("headers", headers);
				List<List<String>> table = queryService.loadList(queryPreview);
				mav.addObject("table", table);
				mav.addObject("message", "Tabela carregada com sucesso!");
				mav.addObject("status_alert", "alert alert-success alert-dismissible");
			}
		} catch (Exception e) {
			mav.addObject("message", "Não foi possível localizar retornos na pesquisa: " + exceptionHandler.getExceptionMessage(e));
			mav.addObject("status_alert", "alert alert-danger alert-dismissible");
		}
		return mav;
	}
}
