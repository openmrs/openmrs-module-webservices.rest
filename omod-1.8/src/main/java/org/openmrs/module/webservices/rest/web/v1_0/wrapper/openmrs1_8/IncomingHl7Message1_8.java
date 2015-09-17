/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8;

import java.io.Serializable;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.APIException;
import org.openmrs.hl7.HL7Constants;
import org.openmrs.hl7.HL7InQueue;
import org.openmrs.hl7.HL7Source;

/**
 * This class represents generic HL7 message (e.g. InQueue/Archive/Error)
 */
public class IncomingHl7Message1_8 extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HL7Source source;
	
	private String sourceKey;
	
	private String data;
	
	/** Status of hl7 message*/
	private Integer messageState;
	
	/**
	 * Empty constructor
	 */
	public IncomingHl7Message1_8() {
		super();
	}
	
	/**
	 * Creates incoming hl7 message from fields
	 * 
	 * @param source the identifier of Hl7Source for this message
	 * @param sourceKey the key of source
	 * @param data hl7 message pay-load
	 * @param messageState (optional) state of hl7 message
	 */
	public IncomingHl7Message1_8(HL7Source hl7Source, String hl7SourceKey, String hl7Data, Integer messageState) {
		super();
		this.source = hl7Source;
		this.sourceKey = hl7SourceKey;
		this.data = hl7Data;
		this.messageState = messageState;
	}
	
	/**
	 * Creates new {@link HL7InQueue} instance from hl7 in queue message
	 */
	public IncomingHl7Message1_8(HL7InQueue message) {
		setSource(message.getHL7Source());
		setSourceKey(message.getHL7SourceKey());
		setData(message.getHL7Data());
		setMessageState(message.getMessageState());
		setUuid(message.getUuid());
	}
	
	/**
	 * Creates new {@link HL7InQueue} instance from itself
	 * 
	 * @return new {@link HL7InQueue} instance
	 */
	public HL7InQueue toHL7InQueue() {
		HL7InQueue result = new HL7InQueue();
		result.setHL7Source(getSource());
		result.setHL7SourceKey(getSourceKey());
		result.setHL7Data(getData());
		if (getMessageState() != null)
			result.setMessageState(getMessageState());
		else
			result.setMessageState(HL7Constants.HL7_STATUS_PENDING);
		return result;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return null;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer arg0) {
		throw new APIException("Setting of hl7 message id currently isn't supported");
	}
	
	/**
	 * @param source the source to set
	 */
	public void setSource(HL7Source source) {
		this.source = source;
	}
	
	/**
	 * @return the source
	 */
	public HL7Source getSource() {
		return source;
	}
	
	/**
	 * @param sourceKey the sourceKey to set
	 */
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	
	/**
	 * @return the sourceKey
	 */
	public String getSourceKey() {
		return sourceKey;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	
	/**
	 * @param messageState the messageState to set
	 */
	public void setMessageState(Integer messageState) {
		this.messageState = messageState;
	}
	
	/**
	 * @return the messageState
	 */
	public Integer getMessageState() {
		return messageState;
	}
}
