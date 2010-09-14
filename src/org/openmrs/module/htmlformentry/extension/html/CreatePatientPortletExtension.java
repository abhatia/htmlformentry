package org.openmrs.module.htmlformentry.extension.html;

import org.openmrs.module.web.extension.PortletExt;

/**
 * Define the extension in the UI to enter into create patient html form
 * selection portlet
 */
public class CreatePatientPortletExtension extends PortletExt {

	/**
	 * @see org.openmrs.module.web.extension.PortletExt#getPortletParameters()
	 */
	public String getPortletParameters() {
		return null;
	}

	/**
	 * @see org.openmrs.module.web.extension.PortletExt#getPortletUrl()
	 */
	public String getPortletUrl() {
		return "createPatientHtmlFormSelect.portlet";
	}

}
