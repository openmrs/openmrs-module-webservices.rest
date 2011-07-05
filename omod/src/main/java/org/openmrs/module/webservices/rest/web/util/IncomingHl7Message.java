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
package org.openmrs.module.webservices.rest.web.util;

import java.io.Serializable;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7Constants;
import org.openmrs.hl7.HL7InQueue;
import org.openmrs.hl7.HL7Source;

/**
 * This class represents generic HL7 message (e.g. InQueue/Archive/Error)
 */
public class IncomingHl7Message extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer hl7SourceId;
	
	private String hl7SourceKey;
	
	private String hl7Data;
	
	/** Status of hl7 message*/
	private Integer messageState;
	
	/**
	 * Empty constructor
	 */
	public IncomingHl7Message() {
		super();
	}
	
	/**
	 * Creates incoming hl7 message from fields
	 * 
	 * @param hl7SourceId the identifier of Hl7Source for this message
	 * @param hl7SourceKey the key of source
	 * @param hl7Data hl7 message pay-load
	 * @param messageState (optional) state of hl7 message
	 */
	public IncomingHl7Message(Integer hl7SourceId, String hl7SourceKey, String hl7Data, Integer messageState) {
		super();
		this.hl7SourceId = hl7SourceId;
		this.hl7SourceKey = hl7SourceKey;
		this.hl7Data = hl7Data;
		this.messageState = messageState;
	}
	
	/**
	 * Creates new {@link HL7InQueue} instance from hl7 in queue message
	 */
	public IncomingHl7Message(HL7InQueue message) {
		setHl7SourceId(message.getHL7Source().getId());
		setHl7SourceKey(message.getHL7SourceKey());
		setHl7Data(message.getHL7Data());
		setMessageState(message.getMessageState());
		setUuid(message.getUuid());
	}
	
	/**
	 * Creates new {@link HL7InQueue} instance from itself
	 * 
	 * @return new {@link HL7InQueue} instance
	 */
	public HL7InQueue getHl7InQueueMessage() {
		HL7InQueue result = new HL7InQueue();
		result.setHL7Source(Context.getHL7Service().getHL7Source(getHl7SourceId()));
		result.setHL7SourceKey(getHl7SourceKey());
		result.setHL7Data(getHl7Data());
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
	 * @param hl7SourceId the hl7SourceId to set
	 */
	public void setHl7SourceId(Integer hl7SourceId) {
		this.hl7SourceId = hl7SourceId;
	}
	
	/**
	 * @return the hl7SourceId
	 */
	public Integer getHl7SourceId() {
		return hl7SourceId;
	}
	
	/**
	 * @param hl7SourceKey the hl7SourceKey to set
	 */
	public void setHl7SourceKey(String hl7SourceKey) {
		this.hl7SourceKey = hl7SourceKey;
	}
	
	/**
	 * @return the hl7SourceKey
	 */
	public String getHl7SourceKey() {
		return hl7SourceKey;
	}
	
	/**
	 * @param hl7Data the hl7Data to set
	 */
	public void setHl7Data(String hl7Data) {
		this.hl7Data = hl7Data;
	}
	
	/**
	 * @return the hl7Data
	 */
	public String getHl7Data() {
		return hl7Data;
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
