package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

public abstract class BaseResource2_0Test extends BaseModuleWebContextSensitiveTest {

    public static final String BASE_URI = "/rest/v1/";

    protected static boolean addedColumn = false;

    @Autowired
    private List<DefaultAnnotationHandlerMapping> handlerMappings;

    @Autowired
    private AnnotationMethodHandlerAdapter handlerAdapter;

    @Before
    public void setUp() throws Exception {
        if (!addedColumn) {
            String sql = "alter table patient add column allergy_status varchar(50)";
            Context.getAdministrationService().executeSQL(sql, false);
            addedColumn = true;
        }
        executeDataSet("allergyTestDataset.xml");
    }

    /**
     * Passes the given request to a proper controller.
     *
     * @param request
     * @return
     * @throws Exception
     */
    public MockHttpServletResponse handle(HttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HandlerExecutionChain handlerExecutionChain = null;
        for (DefaultAnnotationHandlerMapping handlerMapping : handlerMappings) {
            handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain != null) {
                break;
            }
        }
        Assert.assertNotNull("The request URI does not exist", handlerExecutionChain);

        handlerAdapter.handle(request, response, handlerExecutionChain.getHandler());

        return response;
    }

    protected Matcher<Map<String, ?>> allergyMatcher(final Concept allergen, final Concept severity, final Concept... reactions) {
        Collection<Matcher<? super Object>> reactionMatchers = new ArrayList<Matcher<? super Object>>();
        for (Concept reaction : reactions) {
            reactionMatchers.add(hasProperty("reaction.uuid", is(reaction.getUuid())));
        }

        return allOf(
                hasProperty("allergen.codedAllergen.uuid", is(allergen.getUuid())),
                hasProperty("severity.uuid", is(severity.getUuid())),
                hasProperty("reactions", containsInAnyOrder(reactionMatchers)));
    }

    protected Matcher<Object> hasProperty(final String path, final Matcher matcher) {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(Object o) {
                Object actual = path(o, path.split("\\."));
                return matcher.matches(actual);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(path + " ");
                description.appendDescriptionOf(matcher);
            }
        };
    }

    private Object path(Object obj, String... path) {
        try {
            for (String element : path) {
                obj = PropertyUtils.getProperty(obj, element);
            }
            return obj;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Creates a request from the given parameters.
     * <p>
     * The requestURI is automatically preceded with "/rest/" + RestConstants.VERSION_1.
     *
     * @param method
     * @param requestURI
     * @return
     */
    public MockHttpServletRequest request(RequestMethod method, String requestURI, String... params) {
        MockHttpServletRequest request = new MockHttpServletRequest(method.toString(), BASE_URI + requestURI);
        request.addHeader("content-type", "application/json");
        for (int i = 0; i < params.length; i += 2) {
            request.addParameter(params[i], params[i + 1]);
        }
        return request;
    }

    /**
     * Deserializes the JSON response.
     *
     * @param response
     * @return
     * @throws Exception
     */
    public SimpleObject toSimpleObject(MockHttpServletResponse response) throws Exception {
        return new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
    }
}
