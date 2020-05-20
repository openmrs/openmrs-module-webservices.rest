package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_3;

import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.FulfillerDetails2_2;

/**
 * Backing object for FulfillerDetailsResource2_3, expands FulfillerDetails2_2 to add Accession Number
 */
public class FulfillerDetails2_3 extends FulfillerDetails2_2 {

    private String accessionNumber;

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }
}
