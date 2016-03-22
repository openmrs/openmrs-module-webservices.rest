package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.*;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.ConceptStopWord;

import java.util.List;

/**
 * {@link Resource} for {@link ConceptStopWord}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptstopword", supportedClass = ConceptStopWord.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"})
public class ConceptStopwordResource1_9 extends DelegatingCrudResource<ConceptStopWord> {

    /**
     * @see DelegatingCrudResource#getRepresentationDescription(Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addSelfLink();
            return description;
        } else if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("value");
            description.addProperty("locale");
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            description.addSelfLink();
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("value");
            description.addProperty("locale");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @PropertyGetter("display")
    public String getDisplayString(ConceptStopWord delegate) {
        return StringUtils.isEmpty(delegate.getValue()) ? "" : delegate.getValue();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("value");
        description.addProperty("locale");
        return description;
    }

    /**
     * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
     */
    @Override
    public ConceptStopWord getByUniqueId(String uniqueId) {
        List<ConceptStopWord> datatypes = Context.getConceptService().getAllConceptStopWords();
        for (ConceptStopWord datatype : datatypes) {
            if (datatype.getUuid().equals(uniqueId)) {
                return datatype;
            }
        }
        return null;
    }

    /**
     * @see DelegatingCrudResource#newDelegate()
     */
    @Override
    public ConceptStopWord newDelegate() {
        return new ConceptStopWord();
    }

    /**
     * @see DelegatingCrudResource#save(java.lang.Object)
     */
    @Override
    public ConceptStopWord save(ConceptStopWord delegate) {
        return Context.getConceptService().saveConceptStopWord(delegate);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
     * org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public void purge(ConceptStopWord delegate, RequestContext context) throws ResponseException {
        Context.getConceptService().deleteConceptStopWord(delegate.getId());
    }

    @Override
    protected void delete(ConceptStopWord delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {

        List<ConceptStopWord> conceptStopWords = Context.getConceptService().getAllConceptStopWords();
        return new NeedsPaging<ConceptStopWord>(conceptStopWords, context);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
     */
    @Override
    public String getResourceVersion() {
        return RestConstants1_9.RESOURCE_VERSION;
    }
}
