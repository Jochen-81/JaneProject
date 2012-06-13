package de.htw;


import java.util.Set;

import de.uni_trier.jane.basetypes.Address;

public interface DSDVService_sync {
	/**
	*
	* @return Gibt alle erreichbaren Ger�te zur�ck
	*/
	public Set getAllReachableDevices();
	/**
	*
	*@param Adresse des Zielhops
	*@return Gibt den n�chsten Hop in Richtung des gew�nschten Zieles zur�ck
	*/
	public Address getNextHop(Address destination);
	/**
	*
	*@param Adresse des Zielhops
	*@return Gibt die Anzahl der Hops zu einem Ziel zur�ck
	*/
	public int getHopCount(Address destination);
	}