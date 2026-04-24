/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_6;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.MedicationDispense;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.MedicationDispenseService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.EncounterResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.parameter.MedicationDispenseCriteria;
import org.openmrs.util.PrivilegeConstants;

/**
 * {@link Resource} for MedicationDispense, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/medicationdispense", supportedClass = MedicationDispense.class,
		supportedOpenmrsVersions = {"2.6.* - 9.*" })
public class MedicationDispenseResource2_6 extends DataDelegatingCrudResource<MedicationDispense> {

	@Override
	public String getRequiredGetPrivilege() {
		return PrivilegeConstants.GET_MEDICATION_DISPENSE;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("patient", Representation.REF);
			description.addProperty("encounter", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("drug", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("dispenser", Representation.REF);
			description.addProperty("drugOrder", Representation.REF);
			description.addProperty("status", Representation.REF);
			description.addProperty("statusReason", Representation.REF);
			description.addProperty("type", Representation.REF);
			description.addProperty("quantity");
			description.addProperty("quantityUnits", Representation.REF);
			description.addProperty("dose");
			description.addProperty("doseUnits", Representation.REF);
			description.addProperty("route", Representation.REF);
			description.addProperty("frequency", Representation.REF);
			description.addProperty("asNeeded");
			description.addProperty("dosingInstructions");
			description.addProperty("datePrepared");
			description.addProperty("dateHandedOver");
			description.addProperty("wasSubstituted");
			description.addProperty("substitutionType", Representation.REF);
			description.addProperty("substitutionReason", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("patient", Representation.REF);
			description.addProperty("encounter", Representation.DEFAULT);
			description.addProperty("concept", Representation.DEFAULT);
			description.addProperty("drug", Representation.DEFAULT);
			description.addProperty("location", Representation.DEFAULT);
			description.addProperty("dispenser", Representation.DEFAULT);
			description.addProperty("drugOrder", Representation.DEFAULT);
			description.addProperty("status", Representation.DEFAULT);
			description.addProperty("statusReason", Representation.DEFAULT);
			description.addProperty("type", Representation.DEFAULT);
			description.addProperty("quantity");
			description.addProperty("quantityUnits", Representation.DEFAULT);
			description.addProperty("dose");
			description.addProperty("doseUnits", Representation.DEFAULT);
			description.addProperty("route", Representation.DEFAULT);
			description.addProperty("frequency", Representation.DEFAULT);
			description.addProperty("asNeeded");
			description.addProperty("dosingInstructions");
			description.addProperty("datePrepared");
			description.addProperty("dateHandedOver");
			description.addProperty("wasSubstituted");
			description.addProperty("substitutionType", Representation.DEFAULT);
			description.addProperty("substitutionReason", Representation.DEFAULT);
			description.addProperty("formFieldNamespace");
			description.addProperty("formFieldPath");
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getGETModel(Representation)
	 */
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
			        .property("uuid", new StringProperty())
			        .property("display", new StringProperty())
			        .property("patient", new RefProperty("#/definitions/PatientGetRef"))
			        .property("encounter", new RefProperty("#/definitions/EncounterGetRef"))
			        .property("concept", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("drug", new RefProperty("#/definitions/DrugGetRef"))
			        .property("location", new RefProperty("#/definitions/LocationGetRef"))
			        .property("dispenser", new RefProperty("#/definitions/ProviderGetRef"))
			        .property("drugOrder", new RefProperty("#/definitions/DrugOrderGetRef"))
			        .property("status", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("statusReason", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("type", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("quantity", new DoubleProperty())
			        .property("quantityUnits", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("dose", new DoubleProperty())
			        .property("doseUnits", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("route", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("frequency", new RefProperty("#/definitions/OrderFrequencyGetRef"))
			        .property("asNeeded", new BooleanProperty())
			        .property("dosingInstructions", new StringProperty())
			        .property("datePrepared", new DateProperty())
			        .property("dateHandedOver", new DateProperty())
			        .property("wasSubstituted", new BooleanProperty())
			        .property("substitutionType", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("substitutionReason", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("voided", new BooleanProperty());
		}
		if (rep instanceof FullRepresentation) {
			model
			        .property("formFieldNamespace", new StringProperty())
			        .property("formFieldPath", new StringProperty());
		}
		return model;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getCREATEModel(Representation)
	 */
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
		        .property("patient", new StringProperty().example("uuid"))
		        .property("encounter", new StringProperty().example("uuid"))
		        .property("concept", new StringProperty().example("uuid"))
		        .property("drug", new StringProperty().example("uuid"))
		        .property("location", new StringProperty().example("uuid"))
		        .property("dispenser", new StringProperty().example("uuid"))
		        .property("drugOrder", new StringProperty().example("uuid"))
		        .property("status", new StringProperty().example("uuid"))
		        .property("statusReason", new StringProperty().example("uuid"))
		        .property("type", new StringProperty().example("uuid"))
		        .property("quantity", new DoubleProperty())
		        .property("quantityUnits", new StringProperty().example("uuid"))
		        .property("dose", new DoubleProperty())
		        .property("doseUnits", new StringProperty().example("uuid"))
		        .property("route", new StringProperty().example("uuid"))
		        .property("frequency", new StringProperty().example("uuid"))
		        .property("asNeeded", new BooleanProperty())
		        .property("dosingInstructions", new StringProperty())
		        .property("datePrepared", new DateProperty())
		        .property("dateHandedOver", new DateProperty())
		        .property("wasSubstituted", new BooleanProperty())
		        .property("substitutionType", new StringProperty().example("uuid"))
		        .property("substitutionReason", new StringProperty().example("uuid"))
		        .property("formFieldNamespace", new StringProperty())
		        .property("formFieldPath", new StringProperty());
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getUPDATEModel(Representation)
	 */
	@Override
	public Model getUPDATEModel(Representation rep) {
		return new ModelImpl()
		        .property("encounter", new StringProperty().example("uuid"))
		        .property("concept", new StringProperty().example("uuid"))
		        .property("drug", new StringProperty().example("uuid"))
		        .property("location", new StringProperty().example("uuid"))
		        .property("dispenser", new StringProperty().example("uuid"))
		        .property("drugOrder", new StringProperty().example("uuid"))
		        .property("status", new StringProperty().example("uuid"))
		        .property("statusReason", new StringProperty().example("uuid"))
		        .property("type", new StringProperty().example("uuid"))
		        .property("quantity", new DoubleProperty())
		        .property("quantityUnits", new StringProperty().example("uuid"))
		        .property("dose", new DoubleProperty())
		        .property("doseUnits", new StringProperty().example("uuid"))
		        .property("route", new StringProperty().example("uuid"))
		        .property("frequency", new StringProperty().example("uuid"))
		        .property("asNeeded", new BooleanProperty())
		        .property("dosingInstructions", new StringProperty())
		        .property("datePrepared", new DateProperty())
		        .property("dateHandedOver", new DateProperty())
		        .property("wasSubstituted", new BooleanProperty())
		        .property("substitutionType", new StringProperty().example("uuid"))
		        .property("substitutionReason", new StringProperty().example("uuid"))
		        .property("formFieldNamespace", new StringProperty())
		        .property("formFieldPath", new StringProperty());
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("patient");
		description.addRequiredProperty("concept");
		description.addRequiredProperty("status");
		description.addProperty("encounter");
		description.addProperty("drug");
		description.addProperty("location");
		description.addProperty("dispenser");
		description.addProperty("drugOrder");
		description.addProperty("statusReason");
		description.addProperty("type");
		description.addProperty("quantity");
		description.addProperty("quantityUnits");
		description.addProperty("dose");
		description.addProperty("doseUnits");
		description.addProperty("route");
		description.addProperty("frequency");
		description.addProperty("asNeeded");
		description.addProperty("dosingInstructions");
		description.addProperty("datePrepared");
		description.addProperty("dateHandedOver");
		description.addProperty("wasSubstituted");
		description.addProperty("substitutionType");
		description.addProperty("substitutionReason");
		description.addProperty("formFieldNamespace");
		description.addProperty("formFieldPath");
		return description;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("encounter");
		description.addProperty("concept");
		description.addProperty("drug");
		description.addProperty("location");
		description.addProperty("dispenser");
		description.addProperty("drugOrder");
		description.addProperty("status");
		description.addProperty("statusReason");
		description.addProperty("type");
		description.addProperty("quantity");
		description.addProperty("quantityUnits");
		description.addProperty("dose");
		description.addProperty("doseUnits");
		description.addProperty("route");
		description.addProperty("frequency");
		description.addProperty("asNeeded");
		description.addProperty("dosingInstructions");
		description.addProperty("datePrepared");
		description.addProperty("dateHandedOver");
		description.addProperty("wasSubstituted");
		description.addProperty("substitutionType");
		description.addProperty("substitutionReason");
		description.addProperty("formFieldNamespace");
		description.addProperty("formFieldPath");
		return description;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(String)
	 */
	@Override
	public MedicationDispense getByUniqueId(String uuid) {
		return Context.getService(MedicationDispenseService.class).getMedicationDispenseByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(Object, String, RequestContext)
	 */
	@Override
	protected void delete(MedicationDispense medicationDispense, String reason, RequestContext requestContext)
	        throws ResponseException {
		Context.getService(MedicationDispenseService.class).voidMedicationDispense(medicationDispense, reason);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
	public MedicationDispense newDelegate() {
		return new MedicationDispense();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(Object)
	 */
	@Override
	public MedicationDispense save(MedicationDispense medicationDispense) {
		return Context.getService(MedicationDispenseService.class).saveMedicationDispense(medicationDispense);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(Object, RequestContext)
	 */
	@Override
	public void purge(MedicationDispense medicationDispense, RequestContext requestContext) throws ResponseException {
		Context.getService(MedicationDispenseService.class).purgeMedicationDispense(medicationDispense);
	}

	/**
	 * @param medicationDispense the dispense event to display
	 * @return the display string
	 */
	@PropertyGetter("display")
	public String getDisplayString(MedicationDispense medicationDispense) {
		StringBuilder ret = new StringBuilder();
		ret.append("(").append(medicationDispense.getStatus().getDisplayString()).append(") ");
		if (medicationDispense.getDrug() != null) {
			ret.append(medicationDispense.getDrug().getName());
		} else {
			ret.append(medicationDispense.getConcept().getDisplayString());
		}

		if (medicationDispense.getQuantity() != null && medicationDispense.getQuantityUnits() != null) {
			ret.append(": ").append(medicationDispense.getQuantity()).append(" ").append(medicationDispense.getQuantityUnits().getDisplayString());
		}

		return ret.toString();
	}

	/**
	 * Annotated setter for formFieldPath
	 */
	@PropertySetter("formFieldPath")
	public static void setFormFieldPath(MedicationDispense medicationDispense, Object formFieldPath) {
		medicationDispense.setFormField(medicationDispense.getFormFieldNamespace(), (String) formFieldPath);
	}

	/**
	 * Annotated setter for formFieldNamespace
	 */
	@PropertySetter("formFieldNamespace")
	public static void setFormFieldNamespace(MedicationDispense medicationDispense, Object namespace) {
		medicationDispense.setFormField((String) namespace, medicationDispense.getFormFieldPath());
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(RequestContext)
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("patient");
		String encounterUuid = context.getRequest().getParameter("encounter");

		MedicationDispenseCriteria criteria = new MedicationDispenseCriteria();

		if (StringUtils.isNotBlank(patientUuid)) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null) {
				return new EmptySearchResult();
			}
			criteria.setPatient(patient);
		}

		if (StringUtils.isNotBlank(encounterUuid)) {
			Encounter encounter = ((EncounterResource1_8) Context.getService(RestService.class)
			        .getResourceBySupportedClass(Encounter.class)).getByUniqueId(encounterUuid);
			if (encounter == null) {
				return new EmptySearchResult();
			}
			criteria.setEncounter(encounter);
		}

		if (criteria.getPatient() == null && criteria.getEncounter() == null) {
			return new EmptySearchResult();
		}

		return new NeedsPaging<MedicationDispense>(
		        Context.getService(MedicationDispenseService.class).getMedicationDispenseByCriteria(criteria), context);
	}
}
