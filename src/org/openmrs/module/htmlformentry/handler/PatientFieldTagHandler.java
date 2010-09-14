package org.openmrs.module.htmlformentry.handler;

import java.util.Map;

import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.element.PatientFieldDetailSubmissionElement;

/**
 * Handles the {@code <patientField>} tag
 */
public class PatientFieldTagHandler extends SubstitutionTagHandler {
	
	/**
	 * @see org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler#getSubstitution(org.openmrs.module.htmlformentry.FormEntrySession, org.openmrs.module.htmlformentry.FormSubmissionController, java.util.Map)
	 */
	protected String getSubstitution(FormEntrySession session, FormSubmissionController controllerActions,
			Map<String, String> parameters) {
		
		PatientFieldDetailSubmissionElement element = new PatientFieldDetailSubmissionElement(session.getContext(), parameters);
		session.getSubmissionController().addAction(element);

		return element.generateHtml(session.getContext());
	}
}
