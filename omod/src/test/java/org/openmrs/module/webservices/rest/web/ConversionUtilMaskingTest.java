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
import org.openmrs.OrderGroup;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.api.MedicationDispenseService;
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
