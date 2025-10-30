package org.openmrs.module.webservices;

import org.junit.After;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.context.ContextMockHelper;
import org.openmrs.api.context.UserContext;

public class RestBaseContextMockTest {

    @Mock
    protected UserContext userContext;

    @InjectMocks
    protected ContextMockHelper contextMockHelper;

    /**
     * Initializes fields annotated with {@link Mock}. Sets userContext and authenticatedUser.
     */
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void revertContextMocks() {
        contextMockHelper.revertMocks();
    }

}
