package org.openmrs.module.htmlformentry.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * The controller for entering/viewing a patient form. This should always be set to sessionForm=false.
 * <p/>
 * Handles {@code patientHtmlFormEntry.form} requests. Renders view {@code patientHtmlFormEntry.jsp}.
 */
public class PatientHtmlFormEntryController extends HtmlFormEntryController {

	private boolean isEncounterEnabled = false;

	@Override
	protected FormEntrySession formBackingObject(HttpServletRequest request) throws Exception {

		HtmlForm htmlForm = null;
		Patient patient = null;

		Mode mode = Mode.VIEW;
		if ("Enter".equalsIgnoreCase(request.getParameter("mode"))) {
			mode = Mode.ENTER;
			patient = new Patient();
		}
		else if ("Edit".equalsIgnoreCase(request.getParameter("mode"))) {
			mode = Mode.EDIT;
		}

		String formIdParam = request.getParameter("formId");
		if (StringUtils.hasText(formIdParam)) {
			Form form = Context.getFormService().getForm(Integer.parseInt(formIdParam));
			htmlForm = HtmlFormEntryUtil.getService().getHtmlFormByForm(form);
		}

		String htmlFormIdParam = request.getParameter("htmlFormId");
		if (StringUtils.hasText(htmlFormIdParam)) {
			htmlForm = HtmlFormEntryUtil.getService().getHtmlForm(Integer.valueOf(htmlFormIdParam));
		}

		if (!StringUtils.hasText(formIdParam) && !StringUtils.hasText(htmlFormIdParam))
			throw new IllegalArgumentException("You must specify either an htmlFormId or a formId");

		if (htmlForm == null)
			throw new IllegalArgumentException("The given form ID '" + formIdParam + "' or given htmlform ID '" + htmlFormIdParam
					+ "' does not have an HtmlForm associated with it");

		if (mode == Mode.VIEW) {
			String patientIDParam = request.getParameter("patientId");
			if (StringUtils.hasText(patientIDParam)) {
				try {
					patient = Context.getPatientService().getPatient(new Integer(patientIDParam));
				}
				catch (Exception e) {
				}
				if (patient == null)
					throw new IllegalArgumentException("No patient with id " + patientIDParam
							+ "  or not able to reterive the given patient details.");
			}
			else
				throw new IllegalArgumentException("patientId param missing");
		}

		FormEntrySession session;
		isEncounterEnabled = hasEncouterTag(htmlForm.getXmlData());
		session = new FormEntrySession(patient, htmlForm, mode);

		// In case we're not using a sessionForm, we need to check for the case
		// where the underlying form was modified while a user was filling a
		// form out
		if (StringUtils.hasText(request.getParameter("formModifiedTimestamp"))) {
			long submittedTimestamp = Long.valueOf(request.getParameter("formModifiedTimestamp"));
			if (submittedTimestamp != session.getFormModifiedTimestamp()) {
				throw new RuntimeException(Context.getMessageSourceService().getMessage("htmlformentry.error.formModifiedBeforeSubmission"));
			}
		}

		Context.setVolatileUserData(HtmlFormEntryController.FORM_IN_PROGRESS_KEY, session);

		return session;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object commandObject, BindException errors)
			throws Exception {
		FormEntrySession session = (FormEntrySession) commandObject;
		if (isEncounterEnabled) {
			session.prepareForSubmit();
		}
		else {
			session.preparePersonForSubmit();
		}

		if (session.getContext().getMode() == Mode.ENTER
				&& (session.getSubmissionActions().getPersonsToCreate() == null || session.getSubmissionActions().getPersonsToCreate().size() == 0))
			throw new IllegalArgumentException("This form is not going to create an Patient");

		return handleSubmit(request, response, session, errors);
	}

	private boolean hasEncouterTag(String xmlData) {
		for (String tag : HtmlFormEntryConstants.ENCOUNTER_TAGS) {
			tag = "<" + tag;
			if (xmlData.contains(tag)) {
				return true;
			}
		}
		return false;
	}
}
