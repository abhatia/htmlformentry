package org.openmrs.module.htmlformentry.element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.ValidationException;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.widget.AddressWidget;
import org.openmrs.module.htmlformentry.widget.DateWidget;
import org.openmrs.module.htmlformentry.widget.DropdownWidget;
import org.openmrs.module.htmlformentry.widget.ErrorWidget;
import org.openmrs.module.htmlformentry.widget.HiddenFieldWidget;
import org.openmrs.module.htmlformentry.widget.LocationWidget;
import org.openmrs.module.htmlformentry.widget.NumberFieldWidget;
import org.openmrs.module.htmlformentry.widget.Option;
import org.openmrs.module.htmlformentry.widget.RadioButtonsWidget;
import org.openmrs.module.htmlformentry.widget.TextFieldWidget;
import org.openmrs.module.htmlformentry.widget.Widget;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.validator.PatientIdentifierValidator;
import org.springframework.util.StringUtils;

/**
 * Holds the widgets used to represent Patient Details, and serves as both the
 * HtmlGeneratorElement and the FormSubmissionControllerAction for Patient
 * Details.
 */
public class PatientFieldDetailSubmissionElement implements HtmlGeneratorElement, FormSubmissionControllerAction {

	public static final String FIELD_BIRTH_DATE_OR_AGE = "birthDateOrAge";

	public static final String FIELD_BIRTH_DATE = "birthDate";

	public static final String FIELD_AGE = "age";

	public static final String FIELD_GENDER = "gender";

	public static final String FIELD_FAMILY_NAME = "familyName";

	public static final String FIELD_MIDDLE_NAME = "middleName";

	public static final String FIELD_PERSON_NAME = "personName";

	public static final String FIELD_GIVEN_NAME = "givenName";

	public static final String FIELD_IDENTIFIER = "identifier";

	public static final String FIELD_IDENTIFIER_TYPE = "identifierType";

	public static final String FIELD_IDENTIFIER_LOCATION = "identifierLocation";

	public static final String FIELD_ADDRESS = "address";

	private Widget givenNameWidget;
	private ErrorWidget givenNameErrorWidget;
	private Widget middleNameWidget;
	private Widget familyNameWidget;
	private ErrorWidget familyNameErrorWidget;
	private RadioButtonsWidget genderWidget;
	private ErrorWidget genderErrorWidget;
	private Widget ageWidget;
	private ErrorWidget ageErrorWidget;
	private Widget birthDateWidget;
	private ErrorWidget birthDateErrorWidget;
	private Widget identifierTypeWidget;
	private Widget identifierTypeValueWidget;
	private ErrorWidget identifierTypeValueErrorWidget;
	private Widget identifierLocationWidget;
	private ErrorWidget identifierLocationErrorWidget;
	private AddressWidget addressWidget;

	public PatientFieldDetailSubmissionElement(FormEntryContext context, Map<String, String> attributes) {
		createElement(context, attributes);
	}

	public void createElement(FormEntryContext context, Map<String, String> attributes) {
		String field = attributes.get("field");
		Patient existingPatient = context.getExistingPatient();
		if (FIELD_PERSON_NAME.equalsIgnoreCase(field)) {
			givenNameWidget = new TextFieldWidget();
			givenNameErrorWidget = new ErrorWidget();
			createWidgets(context, givenNameWidget, givenNameErrorWidget, existingPatient != null ? existingPatient.getGivenName() : null);

			middleNameWidget = new TextFieldWidget();
			createWidgets(context, middleNameWidget, null, existingPatient != null ? existingPatient.getMiddleName() : null);

			familyNameWidget = new TextFieldWidget();
			familyNameErrorWidget = new ErrorWidget();
			createWidgets(context, familyNameWidget, familyNameErrorWidget, existingPatient != null ? existingPatient.getFamilyName() : null);
		}
		else if (FIELD_GIVEN_NAME.equalsIgnoreCase(field)) {
			givenNameWidget = new TextFieldWidget();
			givenNameErrorWidget = new ErrorWidget();
			createWidgets(context, givenNameWidget, givenNameErrorWidget, existingPatient != null ? existingPatient.getGivenName() : null);
		}
		else if (FIELD_MIDDLE_NAME.equalsIgnoreCase(field)) {
			middleNameWidget = new TextFieldWidget();
			createWidgets(context, middleNameWidget, null, existingPatient != null ? existingPatient.getMiddleName() : null);
		}
		else if (FIELD_FAMILY_NAME.equalsIgnoreCase(field)) {
			familyNameWidget = new TextFieldWidget();
			familyNameErrorWidget = new ErrorWidget();
			createWidgets(context, familyNameWidget, familyNameErrorWidget, existingPatient != null ? existingPatient.getFamilyName() : null);
		}
		else if (FIELD_GENDER.equalsIgnoreCase(field)) {
			genderWidget = new RadioButtonsWidget();
			genderErrorWidget = new ErrorWidget();
			genderWidget.addOption(new Option("Male", "M", false));
			genderWidget.addOption(new Option("Female", "F", false));
			createWidgets(context, genderWidget, genderErrorWidget, existingPatient != null ? existingPatient.getGender() : null);
		}
		else if (FIELD_AGE.equalsIgnoreCase(field)) {
			ageWidget = new NumberFieldWidget(0d, 200d, false);
			ageErrorWidget = new ErrorWidget();
			createWidgets(context, ageWidget, ageErrorWidget, existingPatient != null ? existingPatient.getAge() : null);
		}
		else if (FIELD_BIRTH_DATE.equalsIgnoreCase(field)) {
			birthDateWidget = new DateWidget();
			birthDateErrorWidget = new ErrorWidget();
			createWidgets(context, birthDateWidget, birthDateErrorWidget, existingPatient != null ? existingPatient.getBirthdate() : null);
		}
		else if (FIELD_BIRTH_DATE_OR_AGE.equalsIgnoreCase(field)) {
			ageWidget = new NumberFieldWidget(0d, 200d, false);
			ageErrorWidget = new ErrorWidget();
			createWidgets(context, ageWidget, ageErrorWidget, existingPatient != null ? existingPatient.getAge() : null);

			birthDateWidget = new DateWidget();
			birthDateErrorWidget = new ErrorWidget();
			createWidgets(context, birthDateWidget, birthDateErrorWidget, existingPatient != null ? existingPatient.getBirthdate() : null);

		}
		else if (FIELD_IDENTIFIER_TYPE.equalsIgnoreCase(field)) {
			identifierTypeWidget = new DropdownWidget();
			List<PatientIdentifierType> patientIdentifierTypes = HtmlFormEntryUtil.getPatientIdentifierTypes();

			for (PatientIdentifierType patientIdentifierType : patientIdentifierTypes) {
				((DropdownWidget) identifierTypeWidget).addOption(new Option(patientIdentifierType.getName(), patientIdentifierType
						.getPatientIdentifierTypeId().toString(), false));
			}

			createWidgets(context, identifierTypeWidget, null,
					existingPatient != null && existingPatient.getPatientIdentifier() != null ? existingPatient.getPatientIdentifier()
							.getIdentifierType().getId() : null);
		}
		else if (FIELD_IDENTIFIER.equalsIgnoreCase(field)) {

			identifierTypeValueWidget = new TextFieldWidget();
			identifierTypeValueErrorWidget = new ErrorWidget();
			createWidgets(context, identifierTypeValueWidget, identifierTypeValueErrorWidget, existingPatient != null
					&& existingPatient.getPatientIdentifier() != null ? existingPatient.getPatientIdentifier().getIdentifier() : null);

			String typeId = attributes.get("identifierTypeId");
			if (StringUtils.hasText(typeId)) {
				identifierTypeWidget = new HiddenFieldWidget();
				createWidgets(context, identifierTypeWidget, null, typeId);
			}
		}
		else if (FIELD_IDENTIFIER_LOCATION.equalsIgnoreCase(field)) {
			identifierLocationWidget = new LocationWidget();
			identifierLocationErrorWidget = new ErrorWidget();

			List<Location> locations = Context.getLocationService().getAllLocations();
			((LocationWidget) identifierLocationWidget).setOptions(locations);

			createWidgets(context, identifierLocationWidget, identifierLocationErrorWidget, existingPatient != null
					&& existingPatient.getPatientIdentifier() != null ? existingPatient.getPatientIdentifier().getLocation() : null);
		}

		else if (FIELD_ADDRESS.equalsIgnoreCase(field)) {
			addressWidget = new AddressWidget();
			createWidgets(context, addressWidget, null, existingPatient != null ? existingPatient.getPersonAddress() : null);
		}
	}

	private void createWidgets(FormEntryContext context, Widget fieldWidget, ErrorWidget errorWidget, Object initialValue) {
		context.registerWidget(fieldWidget);
		if (errorWidget != null) {
			context.registerErrorWidget(fieldWidget, errorWidget);
		}
		if (initialValue != null && StringUtils.hasText(initialValue.toString())) {
			fieldWidget.setInitialValue(initialValue);
		}
	}

	@Override
	public String generateHtml(FormEntryContext context) {
		StringBuilder sb = new StringBuilder();
		MessageSourceService mss = Context.getMessageSourceService();

		if (givenNameWidget != null) {
			if (middleNameWidget != null && familyNameWidget != null) {
				sb.append(mss.getMessage("PersonName.givenName")).append(" ");
			}
			sb.append(givenNameWidget.generateHtml(context));
			if (context.getMode() != Mode.VIEW)
				sb.append(givenNameErrorWidget.generateHtml(context));
		}

		if (middleNameWidget != null) {
			if (givenNameWidget != null && familyNameWidget != null) {
				sb.append(" ").append(mss.getMessage("PersonName.middleName")).append(" ");
			}
			sb.append(middleNameWidget.generateHtml(context));
		}

		if (familyNameWidget != null) {
			if (givenNameWidget != null && middleNameWidget != null) {
				sb.append(" ").append(mss.getMessage("PersonName.familyName")).append(" ");
			}
			sb.append(familyNameWidget.generateHtml(context));
			if (context.getMode() != Mode.VIEW)
				sb.append(familyNameErrorWidget.generateHtml(context));
		}

		if (genderWidget != null) {
			sb.append(genderWidget.generateHtml(context));
			if (context.getMode() != Mode.VIEW)
				sb.append(genderErrorWidget.generateHtml(context));
		}

		if (birthDateWidget != null) {
			if (ageWidget != null) {
				sb.append(mss.getMessage("Person.birthdate")).append(" ");
			}
			sb.append(birthDateWidget.generateHtml(context));
			if (context.getMode() != Mode.VIEW)
				sb.append(birthDateErrorWidget.generateHtml(context));
		}

		if (ageWidget != null) {
			if (birthDateWidget != null) {
				sb.append(" ").append(mss.getMessage("Person.age.or")).append(" ");
			}
			else {
				sb.append(mss.getMessage("Person.age")).append(" ");
			}
			sb.append(ageWidget.generateHtml(context));
			if (context.getMode() != Mode.VIEW)
				sb.append(ageErrorWidget.generateHtml(context));
		}

		if (identifierTypeWidget != null) {
			sb.append(identifierTypeWidget.generateHtml(context)).append(" ");
		}

		if (identifierTypeValueWidget != null) {
			sb.append(identifierTypeValueWidget.generateHtml(context));
			if (context.getMode() != Mode.VIEW)
				sb.append(identifierTypeValueErrorWidget.generateHtml(context));
		}

		if (identifierLocationWidget != null) {
			sb.append(identifierLocationWidget.generateHtml(context));
			if (context.getMode() != Mode.VIEW)
				sb.append(identifierLocationErrorWidget.generateHtml(context));
		}

		if (addressWidget != null) {
			sb.append(addressWidget.generateHtml(context));
		}

		return sb.toString();
	}

	@Override
	public void handleSubmission(FormEntrySession session, HttpServletRequest request) {

		Patient patient = (Patient) session.getSubmissionActions().getCurrentPerson();
		if (patient == null) {
			throw new RuntimeException("Programming exception: person shouldn't be null");
		}

		if (givenNameWidget != null) {
			String value = (String) givenNameWidget.getValue(session.getContext(), request);
			PersonName personName = patient.getPersonName();

			if (personName == null) {
				personName = new PersonName();
				patient.addName(personName);
			}
			personName.setGivenName(value);
			personName.setPreferred(true);
		}

		if (middleNameWidget != null) {
			String value = (String) middleNameWidget.getValue(session.getContext(), request);
			PersonName personName = patient.getPersonName();

			if (personName == null) {
				personName = new PersonName();
				patient.addName(personName);
			}
			personName.setMiddleName(value);
			personName.setPreferred(true);
		}

		if (familyNameWidget != null) {
			String value = (String) familyNameWidget.getValue(session.getContext(), request);
			PersonName personName = patient.getPersonName();

			if (personName == null) {
				personName = new PersonName();
				patient.addName(personName);
			}
			personName.setFamilyName(value);
			personName.setPreferred(true);
		}

		if (genderWidget != null) {
			String value = (String) genderWidget.getValue(session.getContext(), request);
			patient.setGender(value);
		}

		if (ageWidget != null) {
			Double value = (Double) ageWidget.getValue(session.getContext(), request);
			if (value != null) {
				calculateBirthDate(patient, null, value.intValue() + "");
			}
		}

		if (birthDateWidget != null) {
			Date value = (Date) birthDateWidget.getValue(session.getContext(), request);
			if (value != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				calculateBirthDate(patient, formatter.format(value), null);
			}
		}

		if (identifierTypeValueWidget != null) {
			PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
			if (patientIdentifier == null) {
				patientIdentifier = new PatientIdentifier();
				patient.addIdentifier(patientIdentifier);
			}

			String identifier = (String) identifierTypeValueWidget.getValue(session.getContext(), request);
			if (identifier != null && !identifier.equals(patientIdentifier.getIdentifier()) && patientIdentifier.getIdentifierType() != null) {
				validateIdentifier(patientIdentifier.getIdentifierType().getPatientIdentifierTypeId(), identifier);
			}
			patientIdentifier.setIdentifier(identifier);
			patientIdentifier.setPreferred(true);
		}

		if (identifierTypeWidget != null) {
			PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
			if (patientIdentifier == null) {
				patientIdentifier = new PatientIdentifier();
				patient.addIdentifier(patientIdentifier);
			}

			PatientIdentifierType pit = getIdentifierType((String) identifierTypeWidget.getValue(session.getContext(), request));

			if (!pit.equals(patientIdentifier.getIdentifierType())) {
				validateIdentifier(pit.getPatientIdentifierTypeId(), patientIdentifier.getIdentifier());
			}
			patientIdentifier.setIdentifierType(pit);
			patientIdentifier.setPreferred(true);

		}

		if (identifierLocationWidget != null) {
			PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
			if (patientIdentifier == null) {
				patientIdentifier = new PatientIdentifier();
				patient.addIdentifier(patientIdentifier);
			}

			Location location = (Location) identifierLocationWidget.getValue(session.getContext(), request);

			patientIdentifier.setLocation(location);
			patientIdentifier.setPreferred(true);

		}

		if (addressWidget != null) {
			PersonAddress personAddress = (PersonAddress) addressWidget.getValue(session.getContext(), request);
			personAddress.setPreferred(true);
			patient.addAddress(personAddress);
		}

		if (session.getContext().getMode() == Mode.ENTER) {
			Integer identifierType = null;
			String identifier = null;
			if (patient.getPatientIdentifier() != null) {
				if (patient.getPatientIdentifier().getIdentifierType() != null) {
					identifierType = patient.getPatientIdentifier().getIdentifierType().getPatientIdentifierTypeId();
				}
				identifier = patient.getPatientIdentifier().getIdentifier();
			}
			if (identifierType != null && identifier != null) {
				validateIdentifier(identifierType, identifier);
			}
		}
	}

	private void validateIdentifier(Integer identifierType, String identifier) {
		if (identifierType != null && identifier != null) {
			try {
				PatientIdentifier pi = new PatientIdentifier();

				pi.setIdentifier(identifier);
				pi.setIdentifierType(getIdentifierType(identifierType.toString()));

				PatientIdentifierValidator.validateIdentifier(pi);
			}
			catch (Exception e) {
				throw new ValidationException(e.getMessage());
			}
		}
	}

	private PatientIdentifierType getIdentifierType(String id) {
		PatientIdentifierType patientIdentifierType = Context.getPatientService().getPatientIdentifierType(new Integer(id));
		if (patientIdentifierType == null) {
			throw new RuntimeException("Invalid identifierTypeId given " + id);
		}
		return patientIdentifierType;
	}

	@Override
	public Collection<FormSubmissionError> validateSubmission(FormEntryContext context, HttpServletRequest submission) {
		List<FormSubmissionError> ret = new ArrayList<FormSubmissionError>();

		List<FormSubmissionError> ageOrBirthdDateErrorMessage = new ArrayList<FormSubmissionError>();

		validateMandatoryField(context, submission, givenNameWidget, givenNameErrorWidget, ret);
		validateMandatoryField(context, submission, familyNameWidget, familyNameErrorWidget, ret);
		validateMandatoryField(context, submission, genderWidget, genderErrorWidget, ret);
		validateMandatoryField(context, submission, identifierTypeValueWidget, identifierTypeValueErrorWidget, ret);
		validateMandatoryField(context, submission, identifierLocationWidget, identifierLocationErrorWidget, ret);

		if (ageWidget != null && validateMandatoryField(context, submission, ageWidget, ageErrorWidget, ageOrBirthdDateErrorMessage)) {
			try {
				Number value = (Number) ageWidget.getValue(context, submission);
			}
			catch (Exception e) {
				ret.add(new FormSubmissionError(context.getFieldName(ageErrorWidget), e.getMessage()));
			}
		}

		if (birthDateWidget != null
				&& validateMandatoryField(context, submission, birthDateWidget, birthDateErrorWidget, ageOrBirthdDateErrorMessage)) {
			try {
				if (birthDateWidget.getValue(context, submission) != null
						&& OpenmrsUtil.compare((Date) birthDateWidget.getValue(context, submission), new Date()) > 0) {
					ret.add(new FormSubmissionError(context.getFieldName(birthDateErrorWidget), Context.getMessageSourceService().getMessage(
							"htmlformentry.error.cannotBeInFuture")));
				}
			}
			catch (Exception e) {
				ret.add(new FormSubmissionError(context.getFieldName(birthDateErrorWidget), e.getMessage()));
			}
		}

		if (ageWidget != null && birthDateWidget != null) {
			if (ageOrBirthdDateErrorMessage.size() > 1) {
				ret.add(new FormSubmissionError(context.getFieldName(ageErrorWidget), Context.getMessageSourceService().getMessage(
						"Person.birthdate.required")));
			}
		}
		else {
			ret.addAll(ageOrBirthdDateErrorMessage);
		}

		if (identifierTypeWidget != null && identifierTypeValueWidget != null) {
			String identifierTypeId = (String) identifierTypeWidget.getValue(context, submission);
			String identifierValue = (String) identifierTypeValueWidget.getValue(context, submission);
			try {
				PatientIdentifier pi = new PatientIdentifier();

				pi.setIdentifier(identifierValue);
				pi.setIdentifierType(getIdentifierType(identifierTypeId));

				PatientIdentifierValidator.validateIdentifier(pi);
			}
			catch (Exception e) {
				ret.add(new FormSubmissionError(context.getFieldName(identifierTypeValueErrorWidget), e.getMessage()));
			}
		}

		return ret;
	}

	private boolean validateMandatoryField(FormEntryContext context, HttpServletRequest submission, Widget widget, ErrorWidget errorWidget,
			List<FormSubmissionError> ret) {

		try {
			if (widget != null) {
				Object obj = widget.getValue(context, submission);
				if (obj == null || (obj instanceof String && !StringUtils.hasText((String) obj))) {
					throw new Exception("htmlformentry.error.required");
				}
			}
		}
		catch (Exception ex) {
			ret.add(new FormSubmissionError(context.getFieldName(errorWidget), Context.getMessageSourceService().getMessage(ex.getMessage())));
			return false;
		}

		return true;
	}

	private void calculateBirthDate(Person person, String date, String age) {
		Date birthdate = null;
		boolean birthdateEstimated = false;
		if (date != null && !date.equals("")) {
			try {
				// only a year was passed as parameter
				if (date.length() < 5) {
					Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, Integer.valueOf(date));
					c.set(Calendar.MONTH, 0);
					c.set(Calendar.DATE, 1);
					birthdate = c.getTime();
					birthdateEstimated = true;
				}
				// a full birthdate was passed as a parameter
				else {
					birthdate = Context.getDateFormat().parse(date);
					birthdateEstimated = false;
				}
			}
			catch (ParseException e) {
				throw new RuntimeException("Error getting date from birthdate", e);
			}
		}
		else if (age != null && !age.equals("")) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			Integer d = c.get(Calendar.YEAR);
			d = d - Integer.parseInt(age);
			try {
				birthdate = DateFormat.getDateInstance(DateFormat.SHORT).parse("01/01/" + d);
				birthdateEstimated = true;
			}
			catch (ParseException e) {
				throw new RuntimeException("Error getting date from age", e);
			}
		}
		if (birthdate != null)
			person.setBirthdate(birthdate);
		person.setBirthdateEstimated(birthdateEstimated);
	}
}
