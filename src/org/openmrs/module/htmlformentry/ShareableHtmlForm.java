package org.openmrs.module.htmlformentry;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;

/**
 * A clone of an HtmlForm, intended to be used by the Metadata sharing module.
 * The clone includes a list of OpenmrsObjects references in the form xml
 * so that the Metadata sharing module knows which other Openmrs objects it needs
 * to package up with the form.
 */
public class ShareableHtmlForm extends HtmlForm {

	private static Log log = LogFactory.getLog(ShareableHtmlForm.class);
	
	private Collection<OpenmrsObject> dependencies;
	
	public ShareableHtmlForm(HtmlForm form) {
		// first, make a clone of the form
		// (do we need to worry about pass-by-reference?)
		this.setChangedBy(form.getChangedBy());
		this.setCreator(form.getCreator());
		this.setDateChanged(form.getDateChanged());
		this.setDateCreated(form.getDateCreated());
		this.setDateRetired(form.getDateRetired());
		this.setDescription(form.getDescription());
		this.setForm(form.getForm());
		this.setId(form.getId());
		this.setName(form.getName());
		this.setRetired(form.getRetired());
		this.setRetiredBy(form.getRetiredBy());
		this.setRetireReason(form.getRetireReason());
		this.setUuid(form.getUuid());
		this.setXmlData(form.getXmlData());
		
		// replace any Ids with Uuids
		HtmlFormEntryUtil.replaceIdsWithUuids(this);
		
		// strip out any local attributes we don't want to pass one
		stripLocalAttributesFromXml();
		
		// make sure all dependent OpenmrsObjects are loaded and explicitly referenced
		calculateDependencies();
	}
	
    /** Allows HtmlForm to be shared via Metadata Sharing Module **/
    protected HtmlForm saveReplace() {
    	HtmlForm form = new HtmlForm();
    	form.setChangedBy(getChangedBy());
    	form.setCreator(getCreator());
    	form.setDateChanged(getDateChanged());
    	form.setDateCreated(getDateCreated());
    	form.setDateRetired(getDateRetired());
    	form.setDescription(getDescription());
    	form.setForm(getForm());
    	form.setId(getId());
    	form.setName(getName());
    	form.setRetired(getRetired());
    	form.setRetiredBy(getRetiredBy());
    	form.setRetireReason(getRetireReason());
    	form.setUuid(getUuid());
    	form.setXmlData(getXmlData());
    	return form;
    }
	
	public void setDependencies(Collection<OpenmrsObject> dependencies) {
	    this.dependencies = dependencies;
    }

	public Collection<OpenmrsObject> getDependencies() {
	    return this.dependencies;
    }
	
	public void calculateDependencies() {
	
		this.dependencies = new HashSet<OpenmrsObject>();
		
		// pattern to match a uuid, i.e., five blocks of alphanumerics separated by hyphens
		Pattern uuid = Pattern.compile("\\w+-\\w+-\\w+-\\w+-\\w+");
		Matcher matcher = uuid.matcher(this.getXmlData());
		
		while (matcher.find()) {
			
			if (Context.getConceptService().getConceptByUuid(matcher.group()) != null) {
				this.dependencies.add(Context.getConceptService().getConceptByUuid(matcher.group()));
			} 
			else if (Context.getLocationService().getLocationByUuid(matcher.group()) != null) {
				this.dependencies.add(Context.getConceptService().getConceptByUuid(matcher.group()));
			}
			else if (Context.getProgramWorkflowService().getProgramByUuid(matcher.group()) != null) {
				this.dependencies.add(Context.getConceptService().getConceptByUuid(matcher.group()));
			}
			else {
				// there is a chance that the "uuid" pattern could match a non-uuid; one reason I'm
				// choosing *not* to throw an exception or log an error here is to handle that case
				log.warn("Unable to load OpenMrs object with uuid = " + matcher.group());
			}
		}
	}
	
	
	public void stripLocalAttributesFromXml() {
		
		// TODO: implement this
		
	}
	
}
