package org.openmrs.module.htmlformentry.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.web.controller.PortletController;

/**
 * Portlet to display a list of all HTML Forms in the system.
 * <p/>
 * Handles {@code createpatientHtmlFormSelect.portlet} requests.
 */
public class CreatePatientPortletController extends PortletController {

	/**
	 * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest,
	 *      java.util.Map)
	 */
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		model.put("htmlForms", HtmlFormEntryUtil.getService().getAllHtmlForms());
		model.put("postURL", "module/htmlformentry/patientHtmlFormEntry.form");
	}
}
