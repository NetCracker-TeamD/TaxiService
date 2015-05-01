package com.teamd.taxi.service.email;
/**
 * Standart notifications 
 * @author Ivaniv Ivan
 *
 */
public enum SystemNotification {
	
	
	/**
	 * for client
	 */
	ASSIGNED   ("Your request is accepted"),
	QUEUED     ("Your request in queue"),
	REFUSED    ("Your request is denied"),
	IN_PROGRESS("Your request in progress"),
	COMPLETED  ("Your order is complete"),
	BLACK_LIST ("Your account is blacklisted"),
	/**
	 * only for driver 
	 */
	UPDATED    ("Updated on request");
	
	private String state;
	
	SystemNotification( String state ){
		this.state = state;
	}

	public String getState() {
		String.format("You order %d is accepted", 456);
		return state;
	}

}
