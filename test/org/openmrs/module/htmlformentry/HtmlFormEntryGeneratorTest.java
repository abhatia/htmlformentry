package org.openmrs.module.htmlformentry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class HtmlFormEntryGeneratorTest extends BaseModuleContextSensitiveTest {

	protected final Log log = LogFactory.getLog(getClass());

	protected static final String XML_DATASET_PACKAGE_PATH = "org/openmrs/module/htmlformentry/include/HtmlFormEntryTest-data3.xml";

	private Patient patient = null;

	@Before
	public void setupDatabase() throws Exception {
		executeDataSet(XML_DATASET_PACKAGE_PATH);

		patient = Context.getPatientService().getPatient(2);
	}

	/**
	 * @see {@link HtmlFormEntryGenerator#applyExcludes(FormEntrySession,String)}
	 * 
	 */
	@Test
	@Verifies(value = "should return correct xml after apply excludeIf tag", method = "applyExcludes(FormEntrySession,String)")
	public void applyExcludes_shouldReturnCorrectXmlAfterApplyExcludeIfTag()
			throws Exception {
		String htmlform = "<htmlform><excludeIf logicTest=\"GENDER = F\">This shows a logic test for a woman</excludeIf><excludeIf logicTest=\"GENDER = M\">This shows a logic test for a man</excludeIf></htmlform>";
		FormEntrySession session = new FormEntrySession(patient, htmlform);
		Assert.assertEquals(
				"<htmlform>This shows a logic test for a woman</htmlform>",
				session.getHtmlToDisplay());
	}

	/**
	 * @see {@link HtmlFormEntryGenerator#applyIncludes(FormEntrySession,String)}
	 * 
	 */
	@Test
	@Verifies(value = "should return correct xml after apply include tag", method = "applyIncludes(FormEntrySession,String)")
	public void applyIncludes_shouldReturnCorrectXmlAfterApplyIncludeTag()
			throws Exception {
		String htmlform = "<htmlform><includeIf logicTest=\"GENDER = F\">This shows a logic test for a woman</includeIf><includeIf logicTest=\"GENDER = M\">This shows a logic test for a man</includeIf></htmlform>";
		FormEntrySession session = new FormEntrySession(patient, htmlform);
		Assert.assertEquals(
				"<htmlform>This shows a logic test for a man</htmlform>",
				session.getHtmlToDisplay());
	}

	/**
	 * @see {@link HtmlFormEntryGenerator#getTestStr(String)}
	 * 
	 */
	@Test
	@Verifies(value = "should extract the correct expression from teststr", method = "getTestStr(String)")
	public void getTestStr_shouldExtractTheCorrectExpressionFromTeststr()
			throws Exception {
		String includeStr = "logicTest = \"GENDER = F\">";// <includeIf test =
		// "FEMALE">
		Assert.assertEquals("GENDER = F", HtmlFormEntryGenerator
				.getTestStr(includeStr));
		includeStr = "velocityTest = \"\"$patient.gender\" == \"F\"\">";
		Assert.assertEquals("\"$patient.gender\" == \"F\"",
				HtmlFormEntryGenerator.getTestStr(includeStr));
	}

	/**
	 * @see {@link HtmlFormEntryGenerator#processIncludeLogic(FormEntrySession,String)}
	 * 
	 */
	@Test
	@Verifies(value = "should return a correct boolean value for logic test string", method = "processIncludeLogic(FormEntrySession,String)")
	public void processIncludeLogic_shouldReturnACorrectBooleanValueForLogicTestString()
			throws Exception {
		String htmlform = "<htmlform><includeIf logicTest=\"GENDER = F\">This shows a logic test for a woman</includeIf><includeIf logicTest=\"GENDER = M\">This shows a logic test for a man</includeIf></htmlform>";
		String testStr = "logicTest=\"GENDER = M\">";
		FormEntrySession session = new FormEntrySession(patient, htmlform);
		Assert.assertEquals(true, HtmlFormEntryGenerator.processIncludeLogic(
				session, testStr));
	}
}