package org.openmrs.module.htmlformentry.widget;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.PersonAddress;
import org.openmrs.module.htmlformentry.FormEntryContext;

public class AddressWidget extends Gadget {

	private PersonAddress initialValue;

	private TextFieldWidget addressWidget1;

	private TextFieldWidget addressWidget2;

	private TextFieldWidget cityWidget;

	private TextFieldWidget stateWidget;

	private TextFieldWidget countryWidget;

	private TextFieldWidget postalCodeWidget;

	private TextFieldWidget latitudeWidget;

	private TextFieldWidget longitudeWidget;

	public AddressWidget() {
		addressWidget1 = new TextFieldWidget(40);
		addressWidget2 = new TextFieldWidget(40);
		cityWidget = new TextFieldWidget(10);
		stateWidget = new TextFieldWidget(10);
		countryWidget = new TextFieldWidget(10);
		postalCodeWidget = new TextFieldWidget(10);
		latitudeWidget = new TextFieldWidget(10);
		longitudeWidget = new TextFieldWidget(10);
	}

	public AddressWidget(PersonAddress personAddress) {
		this();
		setInitialValue(personAddress);
	}

	public String generateHtml(FormEntryContext context) {

		if (!isRegistered) {
			registerWidgets(context);
			isRegistered = true;
		}

		// have the date and time widgets generate their HTML
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		sb.append("<tr><td>Address:</td><td>").append(addressWidget1.generateHtml(context)).append("</td></tr>");
		sb.append("<tr><td>Address 2:</td><td>").append(addressWidget2.generateHtml(context)).append("</td></tr>");
		sb.append("<tr><td>City/Village:</td><td>").append(cityWidget.generateHtml(context)).append("</td></tr>");
		sb.append("<tr><td>State/Province:</td><td>").append(stateWidget.generateHtml(context)).append("</td></tr>");
		sb.append("<tr><td>Country:</td><td>").append(countryWidget.generateHtml(context)).append("</td></tr>");
		sb.append("<tr><td>Postal Code:</td><td>").append(postalCodeWidget.generateHtml(context)).append("</td></tr>");
		sb.append("<tr><td>Latitude:</td><td>").append(latitudeWidget.generateHtml(context)).append("</td></tr>");
		sb.append("<tr><td>Longitude:</td><td>").append(longitudeWidget.generateHtml(context)).append("</td></tr>");
		sb.append("</table> \n");
		return sb.toString();
	}

	public PersonAddress getValue(FormEntryContext context, HttpServletRequest request) {

		PersonAddress personAddress = new PersonAddress();
		personAddress.setAddress1((String) addressWidget1.getValue(context, request));
		personAddress.setAddress2((String) addressWidget2.getValue(context, request));
		personAddress.setCityVillage((String) cityWidget.getValue(context, request));
		personAddress.setStateProvince((String) stateWidget.getValue(context, request));
		personAddress.setCountry((String) countryWidget.getValue(context, request));
		personAddress.setPostalCode((String) postalCodeWidget.getValue(context, request));
		personAddress.setLatitude((String) latitudeWidget.getValue(context, request));
		personAddress.setLongitude((String) longitudeWidget.getValue(context, request));
		return personAddress;

	}

	public void setInitialValue(Object value) {
		if (value != null) {
			initialValue = (PersonAddress) value;

			addressWidget1.setInitialValue(initialValue.getAddress1());
			addressWidget2.setInitialValue(initialValue.getAddress2());
			cityWidget.setInitialValue(initialValue.getCityVillage());
			stateWidget.setInitialValue(initialValue.getStateProvince());
			countryWidget.setInitialValue(initialValue.getCountry());
			postalCodeWidget.setInitialValue(initialValue.getPostalCode());
			latitudeWidget.setInitialValue(initialValue.getLatitude());
			longitudeWidget.setInitialValue(initialValue.getLongitude());
		}
	}

	@Override
	protected void registerWidgets(FormEntryContext context) {

		context.registerWidget(addressWidget1);
		context.registerWidget(addressWidget2);
		context.registerWidget(cityWidget);
		context.registerWidget(stateWidget);
		context.registerWidget(countryWidget);
		context.registerWidget(postalCodeWidget);
		context.registerWidget(latitudeWidget);
		context.registerWidget(longitudeWidget);
	}
}
