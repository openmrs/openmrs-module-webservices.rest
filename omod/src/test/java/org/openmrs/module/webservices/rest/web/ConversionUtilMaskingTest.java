/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.module.webservices.rest.web.ConversionUtil.MASKED_DISPLAY;
import static org.openmrs.module.webservices.rest.web.ConversionUtil.MASKED_PROPERTY;
import static org.openmrs.module.webservices.rest.web.representation.Representation.DEFAULT;
import static org.openmrs.module.webservices.rest.web.representation.Representation.FULL;
import static org.openmrs.module.webservices.rest.web.representation.Representation.REF;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openmrs.CodedOrFreeText;
import org.openmrs.Allergy;
import org.openmrs.Concept;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.ConceptSearchResult;
import org.openmrs.Condition;
import org.openmrs.Diagnosis;
import org.openmrs.DrugIngredient;
import org.openmrs.DrugReferenceMap;
import org.openmrs.Encounter;
import org.openmrs.FieldAnswer;
import org.openmrs.MedicationDispense;
import org.openmrs.Order;
import org.openmrs.Obs;
import org.openmrs.OrderFrequency;
import org.openmrs.OrderGroup;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Relationship;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.MedicationDispenseService;
import org.openmrs.notification.AlertRecipient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.DrugReferenceMapResource1_10;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.OrderableResource1_10;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.DrugIngredientResource1_11;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.FieldAnswerResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptSearchResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.PatientAllergyResource2_0;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_6.MedicationDispenseResource2_6;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_7.ConceptReferenceRangeResource2_7;
import org.openmrs.security.ConceptMaskingTestRule;
import org.openmrs.security.DrugMaskingTestRule;
import org.openmrs.security.ObsMaskingTestRule;
import org.openmrs.security.PersonMaskingTestRule;
import org.openmrs.security.ProviderMaskingTestRule;
import org.openmrs.security.UnregisteredTestObject;
import org.openmrs.security.UnregisteredTestObjectMaskingTestRule;
import org.openmrs.security.UuidMaskingTestRule;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * What a representation looks like when a {@link org.openmrs.security.DomainObjectAuthorizationRule}
 * refuses an object it contains, or a record one of its display strings was read from.
 */
public class ConversionUtilMaskingTest extends BaseModuleWebContextSensitiveTest {

	private static final int DENIED_PATIENT_ID = 7;

	private static final int ALLOWED_PATIENT_ID = 8;

	/** {@code relationship.person_b} of this relationship is person 7, printed in its display. */
	private static final String RELATIONSHIP_UUID = "8c0a3d9c-bf61-496b-95b0-72e6bc4a2b83";

	/** An order group on patient 2, whose identifier and name its display prints. */
	private static final String ORDER_GROUP_UUID = "1c96f25c-4949-4f72-9931-d808fbcdb613";

	private static final int ORDER_GROUP_PATIENT_ID = 2;

	/** Provider 3, whose display prints the name of person 501. */
	private static final String PROVIDER_UUID = "e1009293-c561-47ae-b112-214052c17888";

	private static final int PROVIDER_PERSON_ID = 501;

	/** A condition coded with concept 111, "Concept 1", which its display prints. */
	private static final String CODED_CONDITION_UUID = "c1d4185b-0364-4978-a635-3165a82a3178";

	private static final int CONDITION_CONCEPT_ID = 111;

	/** A diagnosis coded with concept 1001, "Some Diagnosis", which its display prints. */
	private static final String CODED_DIAGNOSIS_UUID = "59bf4fbc-fcdb-4a5b-97ea-0d5c4b4315a7";

	private static final int DIAGNOSIS_CONCEPT_ID = 1001;

	/** An obs group holding no value of its own, so its display is its members' values joined. */
	private static final int OBS_GROUP_ID = 5000;

	/** The only member of {@link #OBS_GROUP_ID}, whose "Some text" its display prints. */
	private static final int OBS_GROUP_MEMBER_ID = 5001;

	/** A member of {@link #OBS_GROUP_MEMBER_ID}, one level further down. */
	private static final int DEEPER_OBS_ID = 5002;

	/** An obs listed among its own group's members, holding {@code value_text="foo"}. */
	private static final int SELF_LINKED_OBS_ID = 1025;

	/** The other member of {@link #SELF_LINKED_OBS_ID}'s group. */
	private static final int SELF_LINKED_GROUP_MEMBER_ID = 1026;

	/** Encounter 3, whose only encounter provider is provider 1, linked to person 1 "Super User". */
	private static final String ENCOUNTER_UUID = "6519d653-393b-4118-9c83-a3715b82d4ac";

	private static final int ENCOUNTER_PROVIDER_PERSON_ID = 1;

	private static final int ENCOUNTER_PROVIDER_ID = 1;

	/** A coded obs: question concept 21 "FOOD ASSISTANCE FOR ENTIRE FAMILY", answer 22 "UNKNOWN". */
	private static final int CODED_OBS_ID = 15;

	private static final int CODED_OBS_QUESTION_CONCEPT_ID = 21;

	private static final int CODED_OBS_ANSWER_CONCEPT_ID = 22;

	/** An obs answered with drug 2, whose display prints "Triomune-30 (<concept 792's name>)". */
	private static final int DRUG_VALUED_OBS_ID = 5200;

	private static final int DRUG_CONCEPT_ID = 792;

	/**
	 * Order 1, a <em>drug</em> order on drug 3 "ASPIRIN" (concept 88). Its display comes from
	 * {@code DrugOrderSubclassHandler1_10}, not from {@code OrderResource1_8}.
	 */
	private static final String DRUG_ORDER_UUID = "921de0a3-05c4-444a-be03-e01b4c4b9142";

	private static final int ASPIRIN_DRUG_ID = 3;

	/**
	 * Order 6, a test order on concept 5497 "CD4 COUNT". Every non-drug order in the dataset is a test
	 * order, and {@code TestOrderSubclassHandler1_10} delegates its display to
	 * {@code OrderResource1_8}, so this is how that getter is reached through a representation.
	 */
	private static final String TEST_ORDER_UUID = "1c96f25c-4949-4f72-9931-d808fbc226de";

	private static final int CD4_COUNT_CONCEPT_ID = 5497;

	/** Concept reference range 30, on concept 4090 "Diastolic blood pressure". */
	private static final String CONCEPT_REFERENCE_RANGE_UUID = "239c1904-15ff-45e1-ac9d-d83afb637926";

	private static final int DIASTOLIC_CONCEPT_ID = 4090;

	/** Allergy 4, whose coded allergen is concept 88 "ASPIRIN". */
	private static final String ALLERGY_UUID = "43ac5109-7d8c-11e1-909d-c80aa9edcf4e";

	/** A drug ingredient whose ingredient is concept 88 "ASPIRIN". */
	private static final String DRUG_INGREDIENT_UUID = "6519d653-393d-4118-9c83-a3715b82d4dc";

	private static final int ASPIRIN_CONCEPT_ID = 88;

	/**
	 * A reference map on drug 2, whose display name it prints. The map lives in
	 * {@code DrugReferenceMap1_10Test_Dataset.xml}, which also renames drug 2 to "Panadol" - the
	 * standard dataset calls the same row "Triomune-30", so each test asserts the name its own dataset
	 * gives it.
	 */
	private static final String DRUG_REFERENCE_MAP_UUID = "fe7693a0-8a55-11e3-8507-1f26fdd6e3f2";

	private static final int DRUG_ID = 2;

	/** A medication dispense on concept 300 "Test Drug", with no drug set. */
	private static final String MEDICATION_DISPENSE_UUID = "f6b7b4de-596c-4d38-bb14-167fc872732c";

	private static final int MEDICATION_DISPENSE_CONCEPT_ID = 300;

	/** Visit 1, on visit type 1 "Initial HIV Clinic Visit" at location 1 "Unknown Location". */
	private static final String VISIT_UUID = "1e5d5d48-6b78-11e0-93c3-18a905e044dc";

	private static final String VISIT_LOCATION_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";

	private static final String VISIT_TYPE_UUID = "c0c579b0-8e59-401d-8a4a-976a0b183519";

	/** Encounter type 2 "Emergency", the type of {@link #ENCOUNTER_UUID}. */
	private static final String ENCOUNTER_TYPE_UUID = "07000be2-26b6-4cce-8b40-866d8435b613";

	/** Patient 2, "101-6 - Horatio Test Hornblower". */
	private static final int PATIENT_ID = 2;

	/** The preferred identifier of patient 2, "101-6", of type "OpenMRS Identification Number". */
	private static final String PREFERRED_IDENTIFIER_UUID = "8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614";

	private static final String IDENTIFIER_TYPE_UUID = "1a339fe9-38bc-4ab3-b180-320988c0b968";

	/** The preferred name of person 2, "Horatio Test Hornblower". */
	private static final String PREFERRED_NAME_UUID = "399e3a7b-6482-487d-94ce-c07bb3ca3cc7";

	/**
	 * The only name of person 501, "Bruno Otterbourg". Person 501 rather than patient 2 for the
	 * PersonResource case: a person who is also a patient converts through {@code PatientResource1_8},
	 * whose display is the one above.
	 */
	private static final String NON_PATIENT_PERSON_NAME_UUID = "d7e51684-a077-4039-9d85-8fd599609156";

	/** Patient 2's enrolment in program 1, "HIV PROGRAM". */
	private static final String PATIENT_PROGRAM_UUID = "b75462a0-4c92-451e-b8bc-e98b38b76534";

	private static final String PROGRAM_UUID = "da4a0391-ba62-4fad-ad66-1e3722d16380";

	/** The obs {@code obsWithValueCodedName.xml} adds, naming concept 22's "UNKNOWN". */
	private static final int VALUE_CODED_NAME_OBS_ID = 5300;

	/** Person attribute 8: person 2's "Civil Status", whose value is concept 6 "MARRIED". */
	private static final String CIVIL_STATUS_ATTRIBUTE_UUID = "0c28eb28-efdf-4553-8db4-ae7c88ead7f8";

	private static final String CIVIL_STATUS_ATTRIBUTE_TYPE_UUID = "a0f5521c-dbbd-4c10-81b2-1b7ab18330df";

	private static final int MARRIED_CONCEPT_ID = 6;

	/** Concept 6's only name, which is what its display string prints. */
	private static final String MARRIED_CONCEPT_NAME_UUID = "f74e3673-983b-41e3-b872-56f0e6f7696e";

	/** The concept-valued visit attribute type {@code visitAttributesWithRecordValues.xml} adds. */
	private static final String VISIT_ATTRIBUTE_TYPE_UUID = "b2d8f4c1-5e37-4a90-8f6b-7c1e0a9d3b54";

	/** Concept 22 "UNKNOWN", which the concept-valued attribute's value_reference names. */
	private static final String UNKNOWN_CONCEPT_UUID = "e10ffe54-5184-4efe-8960-cd565ec1cdf8";

	/** Visit 2, which carries the provider-valued attribute: provider 1, on person 1 "Super User". */
	private static final String PROVIDER_ATTRIBUTE_VISIT_UUID = "4e663d66-6b78-11e0-93c3-18a905e044dc";

	private static final int SUPER_USER_PERSON_ID = 1;

	private static final String SUPER_USER_NAME_UUID = "38a686df-d459-484c-9e7c-3f43a9bced58";

	/** Visit 3, which carries the program-valued attribute: program 1, on concept 9 "HIV PROGRAM". */
	private static final String PROGRAM_ATTRIBUTE_VISIT_UUID = "6f85b2a6-6b78-11e0-93c3-18a905e044dc";

	private static final int PROGRAM_CONCEPT_ID = 9;

	/** A workflow of program 1, printed as its id by {@code Program.toString()}. */
	private static final String PROGRAM_WORKFLOW_UUID = "84f0effa-dd73-46cb-b931-7cd6be6c5f81";

	/** The preferred name of person 7, which {@link #RELATIONSHIP_UUID}'s display reads. */
	private static final String RELATIONSHIP_PERSON_B_NAME_UUID = "a65c347e-1384-493a-a55b-d325924acd94";

	/** Relationship type 1, whose "Doctor" the same display prints between the two names. */
	private static final String RELATIONSHIP_TYPE_UUID = "6d9002ea-a96b-4889-af78-82d48c57a110";

	/** Encounter role 1 "Unknown", the role encounter 3's only provider plays. */
	private static final String ENCOUNTER_ROLE_UUID = "a0b03050-c99b-11e0-9572-0800200c9a66";

	/** The order set of {@link #ORDER_GROUP_UUID}, whose name closes that group's display. */
	private static final String ORDER_SET_UUID = "2d3fb1d0-ae06-22e3-a5e2-0140211c1001";

	/** Order frequency 1, named by concept 113 "1/day x 7 days/week" - it has no name of its own. */
	private static final String ORDER_FREQUENCY_UUID = "28090760-7c38-11e3-baa7-0800200c9a66";

	private static final int ORDER_FREQUENCY_CONCEPT_ID = 113;

	private static final String ORDER_FREQUENCY_CONCEPT_NAME = "1/day x 7 days/week";

	/** User 502 "butch", on person 502 "Hippocrates of Cos", whose name an alert recipient prints. */
	private static final String ALERT_RECIPIENT_USER_UUID = "c98a1558-e131-11de-babe-001e378eb67e";

	private static final int ALERT_RECIPIENT_PERSON_ID = 502;

	private static final String ALERT_RECIPIENT_NAME_UUID = "0e69f78c-e3f2-4d9c-933b-d52aa8a669b8";

	/** Asks for nothing but the display, so only the display getter is under test. */
	private static final Representation DISPLAY_ONLY = new CustomRepresentation("display");

	@AfterEach
	public void disarmRules() {
		PersonMaskingTestRule.deniedId = null;
		ObsMaskingTestRule.deniedId = null;
		ConceptMaskingTestRule.deniedId = null;
		DrugMaskingTestRule.deniedId = null;
		ProviderMaskingTestRule.deniedId = null;
		UnregisteredTestObjectMaskingTestRule.deniedId = null;
		UuidMaskingTestRule.deniedUuid = null;
		UuidMaskingTestRule.deniedUuidAtServiceGuards = null;
	}

	@Test
	public void convertToRepresentation_shouldMaskAnObjectARuleRefuses() throws Exception {
		Patient patient = Context.getPatientService().getPatient(DENIED_PATIENT_ID);
		PersonMaskingTestRule.deniedId = DENIED_PATIENT_ID;

		Object converted = ConversionUtil.convertToRepresentation(patient, DEFAULT);

		assertTrue(isMasked(converted), "a refused patient should come out masked, got " + converted);
		assertEquals(1, asMap(converted).size(), "a mask should carry nothing but the marker");
	}

	@Test
	public void convertToRepresentation_shouldNotMaskAnObjectNoRuleRefuses() throws Exception {
		Patient patient = Context.getPatientService().getPatient(ALLOWED_PATIENT_ID);
		PersonMaskingTestRule.deniedId = DENIED_PATIENT_ID;

		Object converted = ConversionUtil.convertToRepresentation(patient, DEFAULT);

		assertFalse(isMasked(converted));
		assertNotNull(asMap(converted).get("uuid"));
	}

	@Test
	public void convertToRepresentation_shouldMaskANestedObjectARuleRefuses() throws Exception {
		Obs obs = anObsOn(DENIED_PATIENT_ID);
		PersonMaskingTestRule.deniedId = DENIED_PATIENT_ID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(obs, DEFAULT));

		assertNotNull(converted.get("uuid"), "the observation itself is not refused");
		assertTrue(isMasked(converted.get("person")), "the person it hangs off is, got " + converted.get("person"));
	}

	@Test
	public void convertToRepresentation_shouldKeepACollectionsSizeWhenAnElementIsMasked() throws Exception {
		List<Person> people = Arrays.asList(Context.getPersonService().getPerson(DENIED_PATIENT_ID),
		    Context.getPersonService().getPerson(ALLOWED_PATIENT_ID));
		PersonMaskingTestRule.deniedId = DENIED_PATIENT_ID;

		Object converted = ConversionUtil.convertToRepresentation(people, REF);

		List<?> representations = assertInstanceOf(List.class, converted);
		assertEquals(2, representations.size(), "a masked element is replaced, never dropped");
		assertTrue(isMasked(representations.get(0)));
		assertFalse(isMasked(representations.get(1)));
	}

	@Test
	public void convertToRepresentation_shouldDropRatherThanMaskWhenThePrivilegeIsMissingToo() throws Exception {
		Person person = Context.getPersonService().getPerson(DENIED_PATIENT_ID);
		PersonMaskingTestRule.deniedId = DENIED_PATIENT_ID;
		Context.logout();

		Object converted = ConversionUtil.convertToRepresentation(person, DEFAULT, new PrivilegedPersonResource());

		assertSame(ConversionUtil.PRIVILEGE_DENIED, converted,
		    "a missing privilege keeps dropping the property, as it did before masking existed");
	}

	/**
	 * The no-converter shortcut hands an object with no registered resource to the serializer whole, so
	 * this fails if the masking check is moved below it.
	 */
	@Test
	public void convertToRepresentation_shouldMaskARefusedObjectThatHasNoConverter() throws Exception {
		UnregisteredTestObject object = new UnregisteredTestObject();
		object.setId(1);
		object.setSecret("not for this reader");
		UnregisteredTestObjectMaskingTestRule.deniedId = 1;

		Object converted = ConversionUtil.convertToRepresentation(object, DEFAULT);

		assertTrue(isMasked(converted), "a refused object with no resource should still mask, got " + converted);
		assertNotEquals(object, converted, "the shortcut would have returned the object itself");
	}

	@Test
	public void convertToRepresentation_shouldNotMaskAnObjectWithNoConverterThatNoRuleRefuses() throws Exception {
		UnregisteredTestObject object = new UnregisteredTestObject();
		object.setId(2);
		UnregisteredTestObjectMaskingTestRule.deniedId = 1;

		assertSame(object, ConversionUtil.convertToRepresentation(object, DEFAULT));
	}

	// a display string is derived from the records it reads, so masking those records does nothing for
	// it: these pin down the getters that read one a rule can refuse

	/**
	 * The group holds no value of its own, so {@code Obs.getValueAsString} joins its members' values
	 * into the string: refusing the member has to take the group's display with it, or the value goes
	 * out beside the mask - once here and again in the visible member's {@code obsGroup} ref.
	 */
	@Test
	public void getDisplayString_shouldMaskAnObsGroupsDisplayWhenAMembersValueIsRefused() throws Exception {
		executeDataSet("obsWithGroupMembers.xml");
		Obs group = Context.getObsService().getObs(OBS_GROUP_ID);
		assertEquals("Some text", group.getValueAsString(Context.getLocale()),
		    "the group's value should be its member's, or this is testing nothing");
		ObsMaskingTestRule.deniedId = OBS_GROUP_MEMBER_ID;

		assertEquals(MASKED_DISPLAY, display(group, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(group, REF),
		    "ref carries display too, so it must not be the way around the mask");
	}

	/**
	 * A member one level further down, whose value this group's display does not reach because the
	 * member above it has a value of its own. Masked anyway: see
	 * {@code ObsResource1_8.readsARefusedRecord} on why the walk does not restate
	 * {@code getValueAsString}'s branching.
	 */
	@Test
	public void getDisplayString_shouldMaskAnObsGroupsDisplayWhenADeeperMemberIsRefused() throws Exception {
		executeDataSet("obsWithGroupMembers.xml");
		Obs group = Context.getObsService().getObs(OBS_GROUP_ID);
		ObsMaskingTestRule.deniedId = DEEPER_OBS_ID;

		assertEquals(MASKED_DISPLAY, display(group, DEFAULT));
	}

	/**
	 * An obs that is a member of its own group, holding a value of its own. {@code getValueAsString}
	 * returns that value before it ever looks at the members, so this display rendered fine before the
	 * walk existed and has to keep doing so - with no rule registered at all, which is every
	 * installation that has not adopted them.
	 */
	@Test
	public void getDisplayString_shouldNotRecurseForeverOnASelfLinkedObs() throws Exception {
		executeDataSet("obsWithGroupMembers.xml");
		Obs selfLinked = Context.getObsService().getObs(SELF_LINKED_OBS_ID);
		assertTrue(selfLinked.getGroupMembers().contains(selfLinked), "obs " + SELF_LINKED_OBS_ID
		        + " should be a member of its own group, or this is testing nothing");

		// only display: asking for groupMembers as well recurses in the representation itself, which it
		// did before any of this and is not what this pins down
		assertEquals("FOOD CONSTRUCT: foo", display(selfLinked, DISPLAY_ONLY));
	}

	/**
	 * The cycle must not swallow the walk either: the other member of that self-linked group is still
	 * reached and still masks the display.
	 */
	@Test
	public void getDisplayString_shouldStillMaskPastACycleWhenAMemberIsRefused() throws Exception {
		executeDataSet("obsWithGroupMembers.xml");
		Obs selfLinked = Context.getObsService().getObs(SELF_LINKED_OBS_ID);
		ObsMaskingTestRule.deniedId = SELF_LINKED_GROUP_MEMBER_ID;

		assertEquals(MASKED_DISPLAY, display(selfLinked, DISPLAY_ONLY));
	}

	@Test
	public void getDisplayString_shouldNotMaskAnObsGroupsDisplayWhenNoMemberIsRefused() throws Exception {
		executeDataSet("obsWithGroupMembers.xml");
		Obs group = Context.getObsService().getObs(OBS_GROUP_ID);
		ObsMaskingTestRule.deniedId = -1;

		assertEquals("FOOD CONSTRUCT: Some text", display(group, DEFAULT));
	}

	/**
	 * {@code EncounterProviderResource1_9} reads the person through {@code Provider.getName()} the same
	 * way {@code ProviderResource1_9} does. Without the check, an encounter rendered that name under
	 * {@code encounterProviders[].display} while the same person's own provider representation came
	 * back masked.
	 */
	@Test
	public void getDisplayString_shouldMaskAnEncounterProvidersDisplayWhenItsPersonIsRefused() throws Exception {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(ENCOUNTER_UUID);
		assertNotNull(encounter);
		PersonMaskingTestRule.deniedId = ENCOUNTER_PROVIDER_PERSON_ID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(encounter, DEFAULT));

		List<?> encounterProviders = assertInstanceOf(List.class, converted.get("encounterProviders"));
		assertFalse(encounterProviders.isEmpty(), "encounter " + ENCOUNTER_UUID + " should have a provider");
		for (Object encounterProvider : encounterProviders) {
			assertEquals(MASKED_DISPLAY, asMap(encounterProvider).get("display"));
		}
	}

	/**
	 * And the provider itself, not only the person behind it. {@code ProviderResource1_9} needs no such
	 * check because the provider is its delegate, so a refused one is masked before its display runs;
	 * here the provider is a record of its own.
	 */
	@Test
	public void getDisplayString_shouldMaskAnEncounterProvidersDisplayWhenTheProviderIsRefused() throws Exception {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(ENCOUNTER_UUID);
		ProviderMaskingTestRule.deniedId = ENCOUNTER_PROVIDER_ID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(encounter, DEFAULT));

		List<?> encounterProviders = assertInstanceOf(List.class, converted.get("encounterProviders"));
		assertFalse(encounterProviders.isEmpty());
		for (Object encounterProvider : encounterProviders) {
			assertEquals(MASKED_DISPLAY, asMap(encounterProvider).get("display"));
		}
	}

	/**
	 * The answer a coded obs records is a Concept of its own, and its name is what the display prints
	 * after the question's. Nothing covered that until this.
	 */
	@Test
	public void getDisplayString_shouldMaskACodedObsDisplayWhenTheAnswerConceptIsRefused() throws Exception {
		Obs coded = Context.getObsService().getObs(CODED_OBS_ID);
		assertEquals("FOOD ASSISTANCE FOR ENTIRE FAMILY: UNKNOWN", display(coded, DISPLAY_ONLY),
		    "the answer's name should be in the display, or this is testing nothing");
		ConceptMaskingTestRule.deniedId = CODED_OBS_ANSWER_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(coded, DISPLAY_ONLY));
	}

	/**
	 * A drug answer, which {@code getValueAsString} prefers over the coded answer's name. There was no
	 * fixture for one, so {@code obsWithDrugValue.xml} adds it.
	 */
	@Test
	public void getDisplayString_shouldMaskADrugValuedObsDisplayWhenTheDrugIsRefused() throws Exception {
		executeDataSet("obsWithDrugValue.xml");
		Obs drugValued = Context.getObsService().getObs(DRUG_VALUED_OBS_ID);
		assertEquals("FOOD ASSISTANCE FOR ENTIRE FAMILY: Triomune-30 (STAVUDINE LAMIVUDINE AND NEVIRAPINE)",
		    display(drugValued, DISPLAY_ONLY), "the drug should be what the display prints, not the coded answer");
		DrugMaskingTestRule.deniedId = DRUG_ID;

		assertEquals(MASKED_DISPLAY, display(drugValued, DISPLAY_ONLY));
	}

	/**
	 * {@code Drug.getFullName} prints the drug's own concept in brackets after its name, so a rule
	 * refusing that concept has to mask the display even though the drug itself is allowed.
	 */
	@Test
	public void getDisplayString_shouldMaskADrugValuedObsDisplayWhenTheDrugsConceptIsRefused() throws Exception {
		executeDataSet("obsWithDrugValue.xml");
		Obs drugValued = Context.getObsService().getObs(DRUG_VALUED_OBS_ID);
		ConceptMaskingTestRule.deniedId = DRUG_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(drugValued, DISPLAY_ONLY));
	}

	@Test
	public void getDisplayString_shouldMaskAnObsDisplayWhenTheQuestionConceptIsRefused() throws Exception {
		Obs coded = Context.getObsService().getObs(CODED_OBS_ID);
		ConceptMaskingTestRule.deniedId = CODED_OBS_QUESTION_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(coded, DISPLAY_ONLY));
	}

	// The seven below all print a Concept's or a Drug's name while the record itself sits elsewhere in
	// the response, masked. Each calls its getter directly: the guard is what is new, and the
	// @PropertyGetter("display") wiring that puts it in a representation is untouched and covered by
	// each resource's own tests. Every one asserts the unmasked string first, so none can pass
	// vacuously if the name stops being read.

	/**
	 * Through the funnel rather than by calling the getter: an order's display is produced by whichever
	 * subclass handler matches, and calling {@code OrderResource1_8.getDisplayString} directly on a
	 * drug order tested a method the response never reaches for it.
	 */
	@Test
	public void getDisplayString_shouldMaskATestOrdersDisplayWhenItsConceptIsRefused() throws Exception {
		Order order = Context.getOrderService().getOrderByUuid(TEST_ORDER_UUID);
		assertNotNull(order);
		assertEquals("CD4 COUNT", display(order, DISPLAY_ONLY),
		    "a test order's display delegates to OrderResource1_8");
		ConceptMaskingTestRule.deniedId = CD4_COUNT_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(order, DISPLAY_ONLY));
	}

	@Test
	public void getDisplayString_shouldMaskADrugOrdersDisplayWhenItsDrugIsRefused() throws Exception {
		Order order = Context.getOrderService().getOrderByUuid(DRUG_ORDER_UUID);
		assertNotNull(order);
		assertTrue(((String) display(order, DISPLAY_ONLY)).contains("ASPIRIN"),
		    "the drug's name should be in the display, got " + display(order, DISPLAY_ONLY));
		DrugMaskingTestRule.deniedId = ASPIRIN_DRUG_ID;

		assertEquals(MASKED_DISPLAY, display(order, DISPLAY_ONLY));
	}

	@Test
	public void getDisplayString_shouldMaskADrugOrdersDisplayWhenItsConceptIsRefused() throws Exception {
		Order order = Context.getOrderService().getOrderByUuid(DRUG_ORDER_UUID);
		ConceptMaskingTestRule.deniedId = ASPIRIN_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(order, DISPLAY_ONLY));
	}

	@Test
	public void getDisplayString_shouldMaskAConceptReferenceRangesDisplayWhenItsConceptIsRefused() throws Exception {
		ConceptReferenceRange range = Context.getConceptService()
		        .getConceptReferenceRangeByUuid(CONCEPT_REFERENCE_RANGE_UUID);
		assertNotNull(range);
		assertEquals("Diastolic blood pressure", display(range, DISPLAY_ONLY));
		ConceptMaskingTestRule.deniedId = DIASTOLIC_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(range, DISPLAY_ONLY));
	}

	/**
	 * Every property of a reference range is read off its concept, so masking the display alone left
	 * the concept's uuid beside it - and a CIEL uuid is the concept's public id padded out, so that
	 * names it as plainly as the display did.
	 */
	@Test
	public void getProperties_shouldMaskAConceptReferenceRangesConceptAndUnitsWhenTheConceptIsRefused()
	        throws Exception {
		ConceptReferenceRange range = Context.getConceptService()
		        .getConceptReferenceRangeByUuid(CONCEPT_REFERENCE_RANGE_UUID);
		ConceptReferenceRangeResource2_7 resource = new ConceptReferenceRangeResource2_7();
		assertNotEquals(MASKED_DISPLAY, resource.getConcept(range));
		ConceptMaskingTestRule.deniedId = DIASTOLIC_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, resource.getConcept(range), "the uuid names the concept on its own");
		assertEquals(MASKED_DISPLAY, resource.getUnits(range));
	}

	@Test
	public void getDisplayString_shouldMaskAnAllergysDisplayWhenTheCodedAllergenIsRefused() throws Exception {
		executeDataSet("allergyTestDataset.xml");
		Allergy allergy = Context.getPatientService().getAllergyByUuid(ALLERGY_UUID);
		assertNotNull(allergy);
		PatientAllergyResource2_0 resource = new PatientAllergyResource2_0();
		assertEquals("ASPIRIN", resource.getDisplayString(allergy));
		ConceptMaskingTestRule.deniedId = ASPIRIN_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, resource.getDisplayString(allergy));
	}

	@Test
	public void getDisplayString_shouldMaskAConceptSearchResultsDisplayWhenItsConceptIsRefused() throws Exception {
		ConceptSearchResult result = new ConceptSearchResult("aspirin", aspirin(), null);
		ConceptSearchResource1_9 resource = new ConceptSearchResource1_9();
		assertEquals("ASPIRIN", resource.getDisplayString(result));
		ConceptMaskingTestRule.deniedId = ASPIRIN_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, resource.getDisplayString(result));
	}

	@Test
	public void getDisplayString_shouldMaskAnOrderablesDisplayWhenItsConceptIsRefused() throws Exception {
		ConceptSearchResult result = new ConceptSearchResult("aspirin", aspirin(), null);
		OrderableResource1_10 resource = new OrderableResource1_10();
		assertEquals("ASPIRIN", resource.getDisplayString(result));
		ConceptMaskingTestRule.deniedId = ASPIRIN_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, resource.getDisplayString(result));
	}

	/** Built here rather than loaded: no dataset in core or this module holds a field answer. */
	@Test
	public void getDisplayString_shouldMaskAFieldAnswersDisplayWhenItsConceptIsRefused() throws Exception {
		FieldAnswer fieldAnswer = new FieldAnswer();
		fieldAnswer.setConcept(aspirin());
		FieldAnswerResource1_8 resource = new FieldAnswerResource1_8();
		assertEquals("Null Field - ASPIRIN", resource.getDisplayString(fieldAnswer));
		ConceptMaskingTestRule.deniedId = ASPIRIN_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, resource.getDisplayString(fieldAnswer));
	}

	@Test
	public void getDisplayString_shouldMaskADrugIngredientsDisplayWhenTheIngredientIsRefused() throws Exception {
		DrugIngredient ingredient = Context.getConceptService().getDrugIngredientByUuid(DRUG_INGREDIENT_UUID);
		assertNotNull(ingredient);
		DrugIngredientResource1_11 resource = new DrugIngredientResource1_11();
		assertEquals("ASPIRIN", resource.getDisplayString(ingredient));
		ConceptMaskingTestRule.deniedId = ASPIRIN_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, resource.getDisplayString(ingredient));
	}

	@Test
	public void getDisplayString_shouldMaskADrugReferenceMapsDisplayWhenTheDrugIsRefused() throws Exception {
		executeDataSet("DrugReferenceMap1_10Test_Dataset.xml");
		DrugReferenceMap map = drugReferenceMap();
		DrugReferenceMapResource1_10 resource = new DrugReferenceMapResource1_10();
		assertTrue(resource.getDisplayString(map).contains("Panadol"),
		    "the drug's name should be in the display, got " + resource.getDisplayString(map));
		DrugMaskingTestRule.deniedId = DRUG_ID;

		assertEquals(MASKED_DISPLAY, resource.getDisplayString(map));
	}

	@Test
	public void getDisplayString_shouldMaskAMedicationDispensesDisplayWhenItsConceptIsRefused() throws Exception {
		executeDataSet("medicationDispenseTestDataset.xml");
		MedicationDispense dispense = Context.getService(MedicationDispenseService.class)
		        .getMedicationDispenseByUuid(MEDICATION_DISPENSE_UUID);
		assertNotNull(dispense);
		MedicationDispenseResource2_6 resource = new MedicationDispenseResource2_6();
		assertTrue(resource.getDisplayString(dispense).contains("Test Drug"),
		    "the concept's name should be in the display, got " + resource.getDisplayString(dispense));
		ConceptMaskingTestRule.deniedId = MEDICATION_DISPENSE_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, resource.getDisplayString(dispense));
	}

	private Concept aspirin() {
		Concept concept = Context.getConceptService().getConcept(ASPIRIN_CONCEPT_ID);
		assertNotNull(concept, "concept " + ASPIRIN_CONCEPT_ID + " should be in the standard dataset");
		return concept;
	}

	private DrugReferenceMap drugReferenceMap() {
		for (DrugReferenceMap map : Context.getConceptService().getDrug(DRUG_ID).getDrugReferenceMaps()) {
			if (DRUG_REFERENCE_MAP_UUID.equals(map.getUuid())) {
				return map;
			}
		}
		throw new AssertionError("drug reference map " + DRUG_REFERENCE_MAP_UUID + " should be in the dataset");
	}

	@Test
	public void getDisplayString_shouldMaskARelationshipsDisplayWhenEitherPersonIsRefused() throws Exception {
		Relationship relationship = Context.getPersonService().getRelationshipByUuid(RELATIONSHIP_UUID);
		assertNotNull(relationship);
		PersonMaskingTestRule.deniedId = DENIED_PATIENT_ID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(relationship, DEFAULT));
		assertTrue(isMasked(converted.get("personB")), "the refused person masks as a nested object");
		assertEquals(MASKED_DISPLAY, converted.get("display"), "and must not be spelled out in the display");
		assertEquals(MASKED_DISPLAY, display(relationship, REF));
	}

	@Test
	public void getDisplayString_shouldMaskAnOrderGroupsDisplayWhenThePatientIsRefused() throws Exception {
		executeDataSet("orderGroupTestDataset.xml");
		OrderGroup group = Context.getOrderService().getOrderGroupByUuid(ORDER_GROUP_UUID);
		assertNotNull(group);
		PersonMaskingTestRule.deniedId = ORDER_GROUP_PATIENT_ID;

		// the identifier the display prints is a disclosure of its own, not only the name
		assertEquals(MASKED_DISPLAY, display(group, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(group, REF));
	}

	@Test
	public void getDisplayString_shouldMaskAProvidersDisplayWhenItsPersonIsRefused() throws Exception {
		executeDataSet("providerTestDataset.xml");
		Provider provider = Context.getProviderService().getProviderByUuid(PROVIDER_UUID);
		assertNotNull(provider);
		PersonMaskingTestRule.deniedId = PROVIDER_PERSON_ID;

		// Provider.getName() reads the person's name, so no rule on Provider itself is involved
		assertEquals(MASKED_DISPLAY, display(provider, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(provider, REF));
	}

	@Test
	public void getDisplayString_shouldMaskAConditionsDisplayWhenTheCodedConceptIsRefused() throws Exception {
		executeDataSet("conditionTestDataset.xml");
		Condition condition = Context.getConditionService().getConditionByUuid(CODED_CONDITION_UUID);
		assertNotNull(condition);
		ConceptMaskingTestRule.deniedId = CONDITION_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(condition, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(condition, REF));
	}

	@Test
	public void getDisplayString_shouldMaskADiagnosisDisplayWhenTheCodedConceptIsRefused() throws Exception {
		executeDataSet("patientDiagnosisDataSet.xml");
		Diagnosis diagnosis = Context.getDiagnosisService().getDiagnosisByUuid(CODED_DIAGNOSIS_UUID);
		assertNotNull(diagnosis);
		ConceptMaskingTestRule.deniedId = DIAGNOSIS_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(diagnosis, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(diagnosis, REF));
	}

	/**
	 * {@code CodedOrFreeTextConverter} rendered {@code coded} by calling the concept resource itself,
	 * which skipped the funnel: a refused concept masked under a custom representation and came back in
	 * full under the default and full ones. Whether it is hidden must not depend on which {@code v=}
	 * was asked for.
	 */
	@Test
	public void convertToRepresentation_shouldMaskARefusedCodedConceptUnderEveryRepresentation() throws Exception {
		executeDataSet("conditionTestDataset.xml");
		Condition condition = Context.getConditionService().getConditionByUuid(CODED_CONDITION_UUID);
		ConceptMaskingTestRule.deniedId = CONDITION_CONCEPT_ID;

		for (Representation rep : Arrays.asList(DEFAULT, FULL)) {
			Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(condition, rep));
			Map<String, Object> codedOrFreeText = asMap(converted.get("condition"));
			assertTrue(isMasked(codedOrFreeText.get("coded")),
			    "coded should mask under " + rep.getRepresentation() + ", got " + codedOrFreeText.get("coded"));
		}
	}

	@Test
	public void convertToRepresentation_shouldStillRenderACodedConceptNoRuleRefuses() throws Exception {
		executeDataSet("conditionTestDataset.xml");
		Condition condition = Context.getConditionService().getConditionByUuid(CODED_CONDITION_UUID);
		ConceptMaskingTestRule.deniedId = -1;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(condition, DEFAULT));
		Map<String, Object> codedOrFreeText = asMap(converted.get("condition"));
		assertFalse(isMasked(codedOrFreeText.get("coded")));
		assertNotNull(asMap(codedOrFreeText.get("coded")).get("uuid"));
	}

	/**
	 * Routing {@code coded} through the funnel subjects it to the privilege check as well as the rules.
	 * A denial has to leave the key out, the way {@code convertDelegateToRepresentation} does for every
	 * other property, rather than put a mask or a null there.
	 */
	@Test
	public void convertToRepresentation_shouldLeaveCodedOutWhenTheConceptPrivilegeIsMissing() throws Exception {
		executeDataSet("conditionTestDataset.xml");
		CodedOrFreeText codedOrFreeText = Context.getConditionService().getConditionByUuid(CODED_CONDITION_UUID)
		        .getCondition();
		assertNotNull(codedOrFreeText.getCoded());
		Context.logout();

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(codedOrFreeText, DEFAULT));

		assertFalse(converted.containsKey("coded"),
		    "a privilege denial drops the property rather than masking it, got " + converted);
	}

	@Test
	public void isRefusedByARule_shouldStillAskTheRulesWithAPublishedAuthenticationAndNoSession() throws Exception {
		Person denied = Context.getPersonService().getPerson(DENIED_PATIENT_ID);
		PersonMaskingTestRule.deniedId = DENIED_PATIENT_ID;
		Authentication published = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(published, "the test context should publish one to hand back");
		Context.closeSession();
		SecurityContextHolder.getContext().setAuthentication(published);

		try {
			assertFalse(Context.isSessionOpen(), "the UserContext should be gone");
			assertTrue(ConversionUtil.isRefusedByARule(denied),
			    "a published Authentication is enough to judge by, so the rules must still be asked");
		}
		finally {
			Context.openSession();
			authenticate();
		}
	}

	// The records below are reached off a delegate the same way, and each is masked where the response
	// carries it while the display read its name, its identifier or its id in full beside the mask.
	// Every one asserts the unmasked string first, so none can pass vacuously if the record stops
	// being read.

	/**
	 * The one a deployment meets first: restricting visits by location is what TRUNK-6798 is for, and
	 * the location came back masked under {@code location} with its name spelled out in the display of
	 * the same visit.
	 */
	@Test
	public void getDisplayString_shouldMaskAVisitsDisplayWhenItsLocationIsRefused() throws Exception {
		Visit visit = Context.getVisitService().getVisitByUuid(VISIT_UUID);
		assertNotNull(visit);
		assertTrue(((String) display(visit, DISPLAY_ONLY)).contains("Unknown Location"),
		    "the location's name should be in the display, got " + display(visit, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = VISIT_LOCATION_UUID;

		assertEquals(MASKED_DISPLAY, display(visit, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(visit, REF), "ref carries display too");
	}

	@Test
	public void getDisplayString_shouldMaskAVisitsDisplayWhenItsVisitTypeIsRefused() throws Exception {
		Visit visit = Context.getVisitService().getVisitByUuid(VISIT_UUID);
		assertTrue(((String) display(visit, DISPLAY_ONLY)).contains("Initial HIV Clinic Visit"),
		    "the visit type's name should be in the display, got " + display(visit, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = VISIT_TYPE_UUID;

		assertEquals(MASKED_DISPLAY, display(visit, DEFAULT));
	}

	@Test
	public void getDisplayString_shouldNotMaskAVisitsDisplayWhenNothingItReadsIsRefused() throws Exception {
		Visit visit = Context.getVisitService().getVisitByUuid(VISIT_UUID);
		UuidMaskingTestRule.deniedUuid = "no-such-uuid";

		assertTrue(((String) display(visit, DEFAULT)).startsWith("Initial HIV Clinic Visit @ Unknown Location"));
	}

	@Test
	public void getDisplayString_shouldMaskAnEncountersDisplayWhenItsTypeIsRefused() throws Exception {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(ENCOUNTER_UUID);
		assertTrue(((String) display(encounter, DISPLAY_ONLY)).startsWith("Emergency"),
		    "the type's name should be in the display, got " + display(encounter, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = ENCOUNTER_TYPE_UUID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(encounter, DEFAULT));
		assertTrue(isMasked(converted.get("encounterType")), "the type masks as a nested object");
		assertEquals(MASKED_DISPLAY, converted.get("display"), "and must not be spelled out in the display");
	}

	/**
	 * The identifier a patient is looked up by, which the display prints ahead of the name. As much a
	 * disclosure as the name is, and masked under {@code identifiers} while it went out here.
	 */
	@Test
	public void getDisplayString_shouldMaskAPatientsDisplayWhenThePreferredIdentifierIsRefused() throws Exception {
		Patient patient = Context.getPatientService().getPatient(PATIENT_ID);
		assertEquals(PREFERRED_IDENTIFIER_UUID, patient.getPatientIdentifier().getUuid(),
		    "the preferred identifier should be the one this refuses");
		assertEquals("101-6 - Horatio Test Hornblower", display(patient, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = PREFERRED_IDENTIFIER_UUID;

		assertEquals(MASKED_DISPLAY, display(patient, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(patient, REF));
	}

	@Test
	public void getDisplayString_shouldMaskAPatientsDisplayWhenTheNameIsRefused() throws Exception {
		Patient patient = Context.getPatientService().getPatient(PATIENT_ID);
		UuidMaskingTestRule.deniedUuid = PREFERRED_NAME_UUID;

		assertEquals(MASKED_DISPLAY, display(patient, DEFAULT));
	}

	@Test
	public void getDisplayString_shouldMaskAPersonsDisplayWhenTheNameIsRefused() throws Exception {
		Person person = Context.getPersonService().getPerson(PROVIDER_PERSON_ID);
		assertEquals("Bruno Otterbourg", display(person, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = NON_PATIENT_PERSON_NAME_UUID;

		assertEquals(MASKED_DISPLAY, display(person, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(person, REF));
	}

	@Test
	public void getDisplayString_shouldMaskAPatientIdentifiersDisplayWhenItsTypeIsRefused() throws Exception {
		PatientIdentifier identifier = Context.getPatientService().getPatientIdentifierByUuid(PREFERRED_IDENTIFIER_UUID);
		assertNotNull(identifier);
		assertEquals("OpenMRS Identification Number = 101-6", display(identifier, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = IDENTIFIER_TYPE_UUID;

		assertEquals(MASKED_DISPLAY, display(identifier, DEFAULT));
	}

	@Test
	public void getDisplayString_shouldMaskAProgramEnrollmentsDisplayWhenTheProgramIsRefused() throws Exception {
		PatientProgram enrollment = Context.getProgramWorkflowService().getPatientProgramByUuid(PATIENT_PROGRAM_UUID);
		assertNotNull(enrollment);
		assertEquals("HIV PROGRAM", display(enrollment, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = PROGRAM_UUID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(enrollment, DEFAULT));
		assertTrue(isMasked(converted.get("program")), "the program masks as a nested object");
		assertEquals(MASKED_DISPLAY, converted.get("display"), "and must not be named in the display");
	}

	// A ConceptName is not reached by a Concept rule, so these two cover the funnel following one to
	// the concept it names. They are properties rather than display strings: what a reader was served
	// was the name's own representation, in full, beside the mask of the concept it hangs off.

	@Test
	public void convertToRepresentation_shouldMaskAnObsValueCodedNameWhenItsConceptIsRefused() throws Exception {
		executeDataSet("obsWithValueCodedName.xml");
		Obs obs = Context.getObsService().getObs(VALUE_CODED_NAME_OBS_ID);
		assertNotNull(obs.getValueCodedName(), "obs " + VALUE_CODED_NAME_OBS_ID + " should name its coded answer");
		assertEquals("UNKNOWN", asMap(asMap(ConversionUtil.convertToRepresentation(obs, DEFAULT)).get("valueCodedName"))
		        .get("display"), "the name should be rendered in full, or this is testing nothing");
		ConceptMaskingTestRule.deniedId = CODED_OBS_ANSWER_CONCEPT_ID;

		for (Representation rep : Arrays.asList(DEFAULT, FULL)) {
			Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(obs, rep));
			assertTrue(isMasked(converted.get("valueCodedName")), "valueCodedName should mask under "
			        + rep.getRepresentation() + ", got " + converted.get("valueCodedName"));
		}
	}

	@Test
	public void convertToRepresentation_shouldMaskACodedOrFreeTextsSpecificNameWhenItsConceptIsRefused()
	        throws Exception {
		executeDataSet("conditionTestDataset.xml");
		Condition condition = Context.getConditionService().getConditionByUuid(CODED_CONDITION_UUID);
		assertNotNull(condition.getCondition().getSpecificName(), "the condition should name its coded concept");
		ConceptMaskingTestRule.deniedId = CONDITION_CONCEPT_ID;

		for (Representation rep : Arrays.asList(DEFAULT, FULL)) {
			Map<String, Object> codedOrFreeText = asMap(
			    asMap(ConversionUtil.convertToRepresentation(condition, rep)).get("condition"));
			assertTrue(isMasked(codedOrFreeText.get("specificName")), "specificName should mask under "
			        + rep.getRepresentation() + ", got " + codedOrFreeText.get("specificName"));
		}
	}

	@Test
	public void convertToRepresentation_shouldStillRenderAConceptNameWhoseConceptNoRuleRefuses() throws Exception {
		executeDataSet("obsWithValueCodedName.xml");
		Obs obs = Context.getObsService().getObs(VALUE_CODED_NAME_OBS_ID);
		ConceptMaskingTestRule.deniedId = -1;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(obs, DEFAULT));
		assertFalse(isMasked(converted.get("valueCodedName")));
		assertNotNull(asMap(converted.get("valueCodedName")).get("uuid"));
	}

	// Attributes, whose value is a record when the type says so and a literal otherwise

	/**
	 * A concept-valued attribute: the value is masked under {@code value} while the display printed
	 * {@code Concept#toString()}, "Concept #22", which names the refused concept by its id.
	 */
	@Test
	public void getDisplayString_shouldMaskAVisitAttributesDisplayWhenItsConceptValueIsRefused() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(VISIT_UUID);
		assertEquals("Primary Diagnosis: Concept #" + CODED_OBS_ANSWER_CONCEPT_ID, display(attribute, DISPLAY_ONLY));
		ConceptMaskingTestRule.deniedId = CODED_OBS_ANSWER_CONCEPT_ID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(attribute, DEFAULT));
		assertTrue(isMasked(converted.get("value")), "the concept masks as a nested object");
		assertEquals(MASKED_DISPLAY, converted.get("display"), "and must not be named in the display");
	}

	@Test
	public void getDisplayString_shouldMaskAVisitAttributesDisplayWhenItsTypeIsRefused() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(VISIT_UUID);
		UuidMaskingTestRule.deniedUuid = VISIT_ATTRIBUTE_TYPE_UUID;

		assertEquals(MASKED_DISPLAY, display(attribute, DEFAULT));
	}

	/**
	 * A person attribute reports a refusal as nothing but a null hydration: the service's guard throws,
	 * {@code Concept.hydrate} swallows that, and {@code PersonAttribute.toString()} falls back to
	 * printing the raw value - the refused concept's own id. So the response carried
	 * {@code "display": "6"} with nothing under {@code value} at all.
	 */
	@Test
	public void getDisplayString_shouldMaskAPersonAttributesDisplayWhenTheConceptItRefersToIsRefused()
	        throws Exception {
		PersonAttribute attribute = Context.getPersonService().getPersonAttributeByUuid(CIVIL_STATUS_ATTRIBUTE_UUID);
		assertNotNull(attribute);
		assertEquals("MARRIED", display(attribute, DISPLAY_ONLY), "the concept's name should be the display");
		ConceptMaskingTestRule.deniedId = MARRIED_CONCEPT_ID;

		String display = (String) display(attribute, DEFAULT);
		assertNotEquals(String.valueOf(MARRIED_CONCEPT_ID), display, "the raw value is the concept's own id");
		assertEquals(MASKED_DISPLAY, display);
	}

	/**
	 * A user-valued attribute, where the record the value names is not the end of it:
	 * {@code User.getDisplayString()} prints the linked person's name, which a {@code User} rule does
	 * not cover. Asking about the hydrated user alone left that name in the display.
	 */
	@Test
	public void getDisplayString_shouldMaskAUserValuedPersonAttributeWhenThePersonBehindItIsRefused()
	        throws Exception {
		PersonAttribute attribute = aUserValuedAttribute();
		assertEquals("Hippocrates of Cos (butch)", display(attribute, DISPLAY_ONLY),
		    "the person's name should be in the display, or this is testing nothing");
		PersonMaskingTestRule.deniedId = ALERT_RECIPIENT_PERSON_ID;

		assertEquals(MASKED_DISPLAY, display(attribute, DISPLAY_ONLY));
	}

	/** And the name, which neither a User nor a Person rule reaches. */
	@Test
	public void getDisplayString_shouldMaskAUserValuedPersonAttributeWhenTheNameIsRefused() throws Exception {
		PersonAttribute attribute = aUserValuedAttribute();
		UuidMaskingTestRule.deniedUuid = ALERT_RECIPIENT_NAME_UUID;

		assertEquals(MASKED_DISPLAY, display(attribute, DISPLAY_ONLY));
	}

	/**
	 * The same shape one type over: {@code Concept.getDisplayString()} prints a {@code ConceptName}, so
	 * a rule refusing the name and not the concept it hangs off reached the display through the record
	 * that was allowed.
	 */
	@Test
	public void getDisplayString_shouldMaskAConceptValuedPersonAttributeWhenOnlyItsNameIsRefused() throws Exception {
		PersonAttribute attribute = Context.getPersonService().getPersonAttributeByUuid(CIVIL_STATUS_ATTRIBUTE_UUID);
		assertEquals("MARRIED", display(attribute, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = MARRIED_CONCEPT_NAME_UUID;

		assertEquals(MASKED_DISPLAY, display(attribute, DISPLAY_ONLY));
	}

	/**
	 * Built here because no dataset in core or this module holds an attribute type whose format is
	 * {@code org.openmrs.User}. {@code User.hydrate} reads the value as a user id.
	 */
	private PersonAttribute aUserValuedAttribute() {
		User user = Context.getUserService().getUserByUuid(ALERT_RECIPIENT_USER_UUID);
		assertNotNull(user, "user " + ALERT_RECIPIENT_USER_UUID + " should be in the standard dataset");
		PersonAttributeType type = new PersonAttributeType();
		type.setName("Account of record");
		type.setDescription("A user-valued attribute type, which no dataset holds");
		type.setFormat(User.class.getName());
		type.setSearchable(false);
		return new PersonAttribute(Context.getPersonService().savePersonAttributeType(type),
		    String.valueOf(user.getUserId()));
	}

	@Test
	public void getDisplayString_shouldMaskAPersonAttributesDisplayWhenItsTypeIsRefused() throws Exception {
		PersonAttribute attribute = Context.getPersonService().getPersonAttributeByUuid(CIVIL_STATUS_ATTRIBUTE_UUID);
		UuidMaskingTestRule.deniedUuid = CIVIL_STATUS_ATTRIBUTE_TYPE_UUID;

		assertEquals(MASKED_DISPLAY, display(attribute, DEFAULT));
	}

	/**
	 * An attribute whose value is its own content rather than a reference to a record. Nothing is
	 * hidden for it, with no rule registered or with one that refuses something else: a literal is
	 * what this getter is for.
	 */
	@Test
	public void getDisplayString_shouldNotMaskAPersonAttributeHoldingALiteralValue() throws Exception {
		PersonAttribute birthplace = Context.getPersonService().getPersonAttribute(6);
		assertEquals("java.lang.String", birthplace.getAttributeType().getFormat());
		UuidMaskingTestRule.deniedUuid = "no-such-uuid";

		assertEquals("London", display(birthplace, DEFAULT));
	}

	/**
	 * The record a value names is not always the end of it: a datatype's value is printed by its own
	 * {@code toString()}, and {@code Provider.toString()} prints every name of the person behind the
	 * provider. So a Person rule left {@code providerName:[Super User]} in the display beside
	 * {@code value.person: {"masked": true}}.
	 */
	@Test
	public void getDisplayString_shouldMaskAProviderValuedAttributeWhenThePersonBehindItIsRefused() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(PROVIDER_ATTRIBUTE_VISIT_UUID);
		assertTrue(((String) display(attribute, DISPLAY_ONLY)).contains("Super User"),
		    "the person's name should be in the display, got " + display(attribute, DISPLAY_ONLY));
		PersonMaskingTestRule.deniedId = SUPER_USER_PERSON_ID;

		assertEquals(MASKED_DISPLAY, display(attribute, DISPLAY_ONLY));
	}

	/** And each of those names, which neither a Provider nor a Person rule reaches. */
	@Test
	public void getDisplayString_shouldMaskAProviderValuedAttributeWhenAPersonNameIsRefused() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(PROVIDER_ATTRIBUTE_VISIT_UUID);
		UuidMaskingTestRule.deniedUuid = SUPER_USER_NAME_UUID;

		assertEquals(MASKED_DISPLAY, display(attribute, DISPLAY_ONLY));
	}

	/**
	 * {@code Program.toString()} prints its concept, which renders as {@code Concept #9} - an id names
	 * the record as plainly as a name does.
	 */
	@Test
	public void getDisplayString_shouldMaskAProgramValuedAttributeWhenItsConceptIsRefused() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(PROGRAM_ATTRIBUTE_VISIT_UUID);
		assertTrue(((String) display(attribute, DISPLAY_ONLY)).contains("concept=Concept #" + PROGRAM_CONCEPT_ID),
		    "the concept's id should be in the display, got " + display(attribute, DISPLAY_ONLY));
		ConceptMaskingTestRule.deniedId = PROGRAM_CONCEPT_ID;

		assertEquals(MASKED_DISPLAY, display(attribute, DISPLAY_ONLY));
	}

	/** And its workflows, printed as ids in the same string. */
	@Test
	public void getDisplayString_shouldMaskAProgramValuedAttributeWhenAWorkflowIsRefused() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(PROGRAM_ATTRIBUTE_VISIT_UUID);
		UuidMaskingTestRule.deniedUuid = PROGRAM_WORKFLOW_UUID;

		assertEquals(MASKED_DISPLAY, display(attribute, DISPLAY_ONLY));
	}

	/**
	 * A value the datatype never got to hand over: a rule that answers the id-and-type overload, which
	 * is how one written for a deployment reads, makes {@code ConceptService}'s guard throw where
	 * {@link org.openmrs.security.Authorize} would have masked. That exception came out of the two
	 * getters and failed the whole representation, so a visit carrying this attribute served nothing.
	 * <p>
	 * No unmasked assertion first, deliberately: reading the value caches it on the attribute, so
	 * asserting the string here would resolve the concept while it was still allowed and leave the
	 * guard nothing to refuse. The unmasked string is pinned down by
	 * {@link #getDisplayString_shouldMaskAVisitAttributesDisplayWhenItsConceptValueIsRefused()}.
	 */
	@Test
	public void getValue_shouldMaskAVisitAttributeValueAServiceGuardRefusesRatherThanFailing() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(VISIT_UUID);
		UuidMaskingTestRule.deniedUuidAtServiceGuards = UNKNOWN_CONCEPT_UUID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(attribute, DEFAULT));

		assertTrue(isMasked(converted.get("value")),
		    "a value the guard would not resolve should mask, got " + converted.get("value"));
		assertEquals(MASKED_DISPLAY, converted.get("display"));
	}

	/**
	 * The same guard, denied on the privilege rather than by a rule - the commoner way to meet it, and
	 * the reason the mask does not claim to tell the two apart: one expression decides both.
	 */
	@Test
	public void getValue_shouldMaskAVisitAttributeValueWhenThePrivilegeToResolveItIsMissing() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(VISIT_UUID);
		Context.logout();

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(attribute, DEFAULT));

		assertTrue(isMasked(converted.get("value")), "got " + converted.get("value"));
		assertEquals(MASKED_DISPLAY, converted.get("display"));
	}

	/**
	 * A mask a getter substituted itself must survive being handed back to the funnel under every
	 * representation, custom ones included: it holds a marker and no record, so converting it again
	 * would look for the hidden record's named properties on it and fail the response that way instead.
	 */
	@Test
	public void convertToRepresentation_shouldLeaveAMaskAGetterSubstitutedAlone() throws Exception {
		executeDataSet("visitAttributesWithRecordValues.xml");
		VisitAttribute attribute = theOnlyAttributeOf(VISIT_UUID);
		UuidMaskingTestRule.deniedUuidAtServiceGuards = UNKNOWN_CONCEPT_UUID;

		for (Representation rep : Arrays.asList(DEFAULT, FULL, new CustomRepresentation("value:(uuid)"))) {
			Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(attribute, rep));
			assertTrue(isMasked(converted.get("value")),
			    "value should mask under " + rep.getRepresentation() + ", got " + converted.get("value"));
		}
	}

	// An alert recipient is a User, and User.getDisplayString() reads the person's name as well as the
	// username

	@Test
	public void getDisplayString_shouldMaskAnAlertRecipientsDisplayWhenItsPersonIsRefused() throws Exception {
		AlertRecipient recipient = anAlertRecipient();
		assertEquals("Hippocrates of Cos (butch)", display(recipient, DISPLAY_ONLY),
		    "the person's name should be in the display, or this is testing nothing");
		PersonMaskingTestRule.deniedId = ALERT_RECIPIENT_PERSON_ID;

		assertEquals(MASKED_DISPLAY, display(recipient, DISPLAY_ONLY));
	}

	/** And the user, which a Person rule does not cover: the two are separate records. */
	@Test
	public void getDisplayString_shouldMaskAnAlertRecipientsDisplayWhenTheUserIsRefused() throws Exception {
		AlertRecipient recipient = anAlertRecipient();
		UuidMaskingTestRule.deniedUuid = ALERT_RECIPIENT_USER_UUID;

		assertEquals(MASKED_DISPLAY, display(recipient, DISPLAY_ONLY));
	}

	/** And the name, which neither a User nor a Person rule reaches. */
	@Test
	public void getDisplayString_shouldMaskAnAlertRecipientsDisplayWhenTheNameIsRefused() throws Exception {
		AlertRecipient recipient = anAlertRecipient();
		UuidMaskingTestRule.deniedUuid = ALERT_RECIPIENT_NAME_UUID;

		assertEquals(MASKED_DISPLAY, display(recipient, DISPLAY_ONLY));
	}

	/**
	 * Built here rather than loaded: no dataset in core or this module holds an alert, and the display
	 * getter reads nothing but the recipient.
	 */
	private AlertRecipient anAlertRecipient() {
		User recipient = Context.getUserService().getUserByUuid(ALERT_RECIPIENT_USER_UUID);
		assertNotNull(recipient, "user " + ALERT_RECIPIENT_USER_UUID + " should be in the standard dataset");
		AlertRecipient alertRecipient = new AlertRecipient();
		alertRecipient.setUuid("647beb7c-d4fc-404c-b751-5c0dc14f5345");
		alertRecipient.setRecipient(recipient);
		return alertRecipient;
	}

	private VisitAttribute theOnlyAttributeOf(String visitUuid) {
		Visit visit = Context.getVisitService().getVisitByUuid(visitUuid);
		assertNotNull(visit);
		assertEquals(1, visit.getActiveAttributes().size(),
		    "visitAttributesWithRecordValues.xml should give visit " + visitUuid + " its only attribute");
		return visit.getActiveAttributes().iterator().next();
	}

	// The second order of the same problem: these displays asked about a record but not about the name
	// or type rows that record's own display reads, so refusing one of those left the string intact.

	@Test
	public void getDisplayString_shouldMaskAConceptsDisplayWhenItsNameIsRefused() throws Exception {
		Concept aspirin = aspirin();
		assertEquals("ASPIRIN", display(aspirin, DISPLAY_ONLY));
		// read off the concept rather than hardcoded: which of its names is preferred depends on the
		// locale, and this test is about the name the display actually reads
		UuidMaskingTestRule.deniedUuid = aspirin.getName().getUuid();

		assertEquals(MASKED_DISPLAY, display(aspirin, DEFAULT));
		assertEquals(MASKED_DISPLAY, display(aspirin, REF));
	}

	@Test
	public void getDisplayString_shouldMaskAProvidersDisplayWhenTheNameItReadsIsRefused() throws Exception {
		executeDataSet("providerTestDataset.xml");
		Provider provider = Context.getProviderService().getProviderByUuid(PROVIDER_UUID);
		assertNotNull(provider);
		UuidMaskingTestRule.deniedUuid = NON_PATIENT_PERSON_NAME_UUID;

		assertEquals(MASKED_DISPLAY, display(provider, DEFAULT));
	}

	@Test
	public void getDisplayString_shouldMaskAnEncounterProvidersDisplayWhenTheNameItReadsIsRefused() throws Exception {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(ENCOUNTER_UUID);
		UuidMaskingTestRule.deniedUuid = SUPER_USER_NAME_UUID;

		assertEncounterProvidersMasked(encounter);
	}

	/** The role is printed after the name, so refusing it has to hide the string as well. */
	@Test
	public void getDisplayString_shouldMaskAnEncounterProvidersDisplayWhenItsRoleIsRefused() throws Exception {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(ENCOUNTER_UUID);
		UuidMaskingTestRule.deniedUuid = ENCOUNTER_ROLE_UUID;

		assertEncounterProvidersMasked(encounter);
	}

	@Test
	public void getDisplayString_shouldMaskARelationshipsDisplayWhenANameItReadsIsRefused() throws Exception {
		Relationship relationship = Context.getPersonService().getRelationshipByUuid(RELATIONSHIP_UUID);
		assertNotNull(relationship);
		UuidMaskingTestRule.deniedUuid = RELATIONSHIP_PERSON_B_NAME_UUID;

		assertEquals(MASKED_DISPLAY, display(relationship, DEFAULT));
	}

	@Test
	public void getDisplayString_shouldMaskARelationshipsDisplayWhenItsTypeIsRefused() throws Exception {
		Relationship relationship = Context.getPersonService().getRelationshipByUuid(RELATIONSHIP_UUID);
		assertTrue(((String) display(relationship, DISPLAY_ONLY)).contains("Doctor"),
		    "the type should be in the display, got " + display(relationship, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = RELATIONSHIP_TYPE_UUID;

		assertEquals(MASKED_DISPLAY, display(relationship, DEFAULT));
	}

	@Test
	public void getDisplayString_shouldMaskAnOrderGroupsDisplayWhenTheIdentifierItReadsIsRefused() throws Exception {
		executeDataSet("orderGroupTestDataset.xml");
		OrderGroup group = Context.getOrderService().getOrderGroupByUuid(ORDER_GROUP_UUID);
		assertNotNull(group);
		// orderGroupTestDataset.xml gives patient 2 its own identifier and name, so both are read off
		// the group rather than taken from the standard dataset's
		UuidMaskingTestRule.deniedUuid = group.getPatient().getPatientIdentifier().getUuid();

		assertEquals(MASKED_DISPLAY, display(group, DEFAULT));
	}

	@Test
	public void getDisplayString_shouldMaskAnOrderGroupsDisplayWhenTheNameItReadsIsRefused() throws Exception {
		executeDataSet("orderGroupTestDataset.xml");
		OrderGroup group = Context.getOrderService().getOrderGroupByUuid(ORDER_GROUP_UUID);
		UuidMaskingTestRule.deniedUuid = group.getPatient().getPersonName().getUuid();

		assertEquals(MASKED_DISPLAY, display(group, DEFAULT));
	}

	/** The order set closes that display, and is a record of its own that no Patient rule reaches. */
	@Test
	public void getDisplayString_shouldMaskAnOrderGroupsDisplayWhenItsOrderSetIsRefused() throws Exception {
		executeDataSet("orderGroupTestDataset.xml");
		OrderGroup group = Context.getOrderService().getOrderGroupByUuid(ORDER_GROUP_UUID);
		assertTrue(((String) display(group, DISPLAY_ONLY)).contains("Order_Set_1"),
		    "the order set's name should be in the display, got " + display(group, DISPLAY_ONLY));
		UuidMaskingTestRule.deniedUuid = ORDER_SET_UUID;

		assertEquals(MASKED_DISPLAY, display(group, DEFAULT));
	}

	/**
	 * The one case where the leak needs no {@code v=custom}: an order frequency has no name column of
	 * its own, so the {@code name} its default representation declares - and the {@code display}
	 * derived from it - are both read off the concept, which is masked under {@code concept} in the
	 * same response.
	 */
	@Test
	public void getName_shouldMaskAnOrderFrequencysNameAndDisplayWhenItsConceptIsRefused() throws Exception {
		OrderFrequency frequency = Context.getOrderService().getOrderFrequencyByUuid(ORDER_FREQUENCY_UUID);
		assertNotNull(frequency);
		Map<String, Object> unmasked = asMap(ConversionUtil.convertToRepresentation(frequency, DEFAULT));
		assertEquals(ORDER_FREQUENCY_CONCEPT_NAME, unmasked.get("name"),
		    "the name should be the concept's, or this is testing nothing");
		assertEquals(ORDER_FREQUENCY_CONCEPT_NAME, unmasked.get("display"));
		ConceptMaskingTestRule.deniedId = ORDER_FREQUENCY_CONCEPT_ID;

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(frequency, DEFAULT));
		assertTrue(isMasked(converted.get("concept")), "the concept masks as a nested object");
		assertEquals(MASKED_DISPLAY, converted.get("name"), "and must not be spelled out under name");
		assertEquals(MASKED_DISPLAY, converted.get("display"));
	}

	/** And the name itself, which no Concept rule reaches. */
	@Test
	public void getName_shouldMaskAnOrderFrequencysNameWhenTheConceptNameIsRefused() throws Exception {
		OrderFrequency frequency = Context.getOrderService().getOrderFrequencyByUuid(ORDER_FREQUENCY_UUID);
		UuidMaskingTestRule.deniedUuid = frequency.getConcept().getName().getUuid();

		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(frequency, DEFAULT));
		assertEquals(MASKED_DISPLAY, converted.get("name"));
		assertEquals(MASKED_DISPLAY, converted.get("display"));
	}

	private void assertEncounterProvidersMasked(Encounter encounter) throws Exception {
		assertNotNull(encounter);
		List<?> encounterProviders = assertInstanceOf(List.class,
		    asMap(ConversionUtil.convertToRepresentation(encounter, DEFAULT)).get("encounterProviders"));
		assertFalse(encounterProviders.isEmpty(), "encounter " + ENCOUNTER_UUID + " should have a provider");
		for (Object encounterProvider : encounterProviders) {
			assertEquals(MASKED_DISPLAY, asMap(encounterProvider).get("display"));
		}
	}

	private Object display(Object delegate, Representation rep) throws Exception {
		return asMap(ConversionUtil.convertToRepresentation(delegate, rep)).get("display");
	}

	private Obs anObsOn(int personId) {
		Person person = Context.getPersonService().getPerson(personId);
		List<Obs> obs = Context.getObsService().getObservationsByPerson(person);
		assertFalse(obs.isEmpty(), "the test dataset should hold an observation on person " + personId);
		return obs.get(0);
	}

	private boolean isMasked(Object representation) {
		return representation instanceof Map && Boolean.TRUE.equals(((Map<?, ?>) representation).get(MASKED_PROPERTY));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> asMap(Object representation) {
		return assertInstanceOf(Map.class, representation);
	}

	/**
	 * Demands a privilege nobody holds, to pin down which of the two checks answers first.
	 */
	public static class PrivilegedPersonResource extends BaseDelegatingResource<Person> {

		@Override
		public String getRequiredGetPrivilege() {
			return "Test-Privilege-Nobody-Has-4b1e9c2a";
		}

		@Override
		public SimpleObject asRepresentation(Person delegate, Representation rep) {
			return new SimpleObject().add("uuid", delegate.getUuid());
		}

		@Override
		public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
			return null;
		}

		@Override
		public Person getByUniqueId(String uniqueId) {
			return null;
		}

		@Override
		protected void delete(Person delegate, String reason, RequestContext context) {
		}

		@Override
		public void purge(Person delegate, RequestContext context) {
		}

		@Override
		public Person newDelegate() {
			return null;
		}

		@Override
		public Person save(Person delegate) {
			return delegate;
		}

		@Override
		public String getUri(Object delegate) {
			return "";
		}
	}
}
