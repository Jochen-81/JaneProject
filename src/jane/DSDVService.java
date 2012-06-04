package jane;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryListener;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_async;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.visualization.shapes.Shape;

public class DSDVService extends Observable implements DSDVService_sync, RuntimeService, NeighborDiscoveryListener {

	public static ServiceID serviceID;
	private ServiceID linkLayerID;
	private ServiceID neighborID;
	private Address myAddress;

	private LinkLayer_async linkLayer;
	private NeighborDiscoveryService_sync neighborService;
	private RuntimeOperatingSystem runtimeOperatingSystem;

	private HashMap<Address, DeviceRouteData> routingTable;

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public DSDVService(ServiceID linkLayerID, ServiceID neighborID) {
		super();
		this.linkLayerID = linkLayerID;
		this.neighborID = neighborID;
		serviceID = new EndpointClassID(DSDVService.class.getName());

		routingTable = new HashMap<Address, DeviceRouteData>();

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Set getAllReachableDevices() {
		Set reachable = new HashSet<Address>();
		
		for ( Address a : routingTable.keySet()){
			DeviceRouteData entry = routingTable.get(a);
			if (entry.getDistanceToDestination() !=  -1)
				reachable.add(a);
		}
		return reachable;
	}

	@Override
	public int getHopCount(Address destination) {
		
		if (!routingTable.containsKey(destination))
			return -1;
		else
			return (int)routingTable.get(destination).getDistanceToDestination();
	}

	@Override
	public Address getNextHop(Address destination) {
		
		if (!routingTable.containsKey(destination))
			return null;
		else
			return routingTable.get(destination).getNextHop();
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void handleMessage(Address Source, HashMap<Address, DeviceRouteData> routeTable) {
		
		if (Source.toString().equals(myAddress.toString())){
			return;
		}
		
		boolean forwardTable = false;
		
		for (Address a : routeTable.keySet()){
			DeviceRouteData oldEntry = routingTable.get(a);
			DeviceRouteData newEntry = routeTable.get(a);
			if(oldEntry == null){
				routingTable.put(a, new DeviceRouteData ( newEntry.getNextHop() ,newEntry.getDistanceToDestination() , newEntry.getSequenceNumber()));
				forwardTable = true;
			} else {
				if(a.toString().equals(myAddress.toString())){//Information über sich selbst
					if(newEntry.getDistanceToDestination() == -1){//Fehlinformation über sich selbst
						long sequenceNumber = routingTable.get(myAddress).getSequenceNumber();
						routingTable.get(myAddress).setSequenceNumber(sequenceNumber + 2);
						forwardTable = true;
					} else { // Information ueber sich selbst wird ignoriert 
						continue;
					}					
				} else { // Information über anderen Knoten
					if(newEntry.getSequenceNumber() > oldEntry.getSequenceNumber()){ //neuere Information
						routingTable.put(a, new DeviceRouteData ( routeTable.get(a).getNextHop() , routeTable.get(a).getDistanceToDestination() , routeTable.get(a).getSequenceNumber()));
						forwardTable = true;
					} else if (oldEntry.getSequenceNumber() == newEntry.getSequenceNumber()){ // gleiche Freshness
						
						if (oldEntry.getDistanceToDestination() == -1){ // Information Knoten weg, bereits bekannt 
							continue;							
						} 
						if(oldEntry.getDistanceToDestination() > newEntry.getDistanceToDestination()){ // besserer Weg
							routingTable.put(a, new DeviceRouteData ( routeTable.get(a).getNextHop() , routeTable.get(a).getDistanceToDestination() , routeTable.get(a).getSequenceNumber()));
							forwardTable = true;
						}
						 if(oldEntry.getDistanceToDestination() <= newEntry.getDistanceToDestination()){ // schlechterer Weg
							continue;
						}						
					}
				}
			}			
		}
		if(forwardTable){
			linkLayer.sendBroadcast(new RouteTableMessage(cloneAndModifyRoutingTable()));
			routingTableHasChanged();
		}
		
	}
	/**
	 * Routingtable clonen, alle NextHop auf sich selbst setzen, alle Destinations um 1 incrementieren
	 * @return veraenderte und geclonte Map
	 */
	private HashMap<Address,DeviceRouteData> cloneAndModifyRoutingTable(){
		HashMap<Address,DeviceRouteData> clonedMap = new HashMap<Address, DeviceRouteData>();
		
		for (Address a : routingTable.keySet()){
			if (routingTable.get(a).getDistanceToDestination() != -1)
				clonedMap.put(a,new DeviceRouteData(myAddress, routingTable.get(a).getDistanceToDestination()+1,routingTable.get(a).getSequenceNumber()));
			else
				clonedMap.put(a,new DeviceRouteData(null, -1,routingTable.get(a).getSequenceNumber() ));
		}
		return clonedMap;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		this.runtimeOperatingSystem = runtimeOperatingSystem;
		
		//Am LinkLayer registrieren, um diesen aus TestService heraus nutzen zu k�nnen
		linkLayer=(LinkLayer_async)runtimeOperatingSystem.getSignalListenerStub(linkLayerID,
					LinkLayer_async.class);
		
		runtimeOperatingSystem.registerAtService(linkLayerID, LinkLayer_async.class);
		
		//Am Nachbarschaftsservice registrieren, um diesen aus TestService heraus nutzen zu k�nnen
		neighborService = (NeighborDiscoveryService_sync)runtimeOperatingSystem.getSignalListenerStub(neighborID,
				NeighborDiscoveryService_sync.class);
		
		runtimeOperatingSystem.registerAtService(neighborID,
				NeighborDiscoveryService.class);
		
		//sich selbst in die routing table eintragen
		myAddress = runtimeOperatingSystem.getDeviceID();
		routingTable.put(myAddress, new DeviceRouteData(myAddress, 0, 0));
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub

	}

	@Override
	public ServiceID getServiceID() {
		return serviceID;
	}

	@Override
	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void removeNeighborData(Address linkLayerAddress) {
		// Information ueber sich selbst => sequenznumber +2 
		long sequenceNumber = routingTable.get(myAddress).getSequenceNumber();
		routingTable.get(myAddress).setSequenceNumber(sequenceNumber + 2);		
		
		// Knoten f�llt weg => alle dahinter liegenden Knoten auch als notReachable markieren
		for (DeviceRouteData d : routingTable.values()) {
			if (d.getNextHop() != null  && d.getNextHop().toString().equals(linkLayerAddress.toString())) {
				sequenceNumber = d.getSequenceNumber();
				d.setSequenceNumber(sequenceNumber + 1);

				d.setDistanceToDestination(-1);

				d.setNextHop(null);
			}
		}
		
		routingTableHasChanged();
		linkLayer.sendBroadcast(new RouteTableMessage(cloneAndModifyRoutingTable()));
	}

	@Override
	public void setNeighborData(NeighborDiscoveryData neighborData) {
		// verbreite Information ueber sich selbst => sequenzNumber +2
		long sequenceNumber = routingTable.get(myAddress).getSequenceNumber();
		routingTable.get(myAddress).setSequenceNumber(sequenceNumber + 2);

		linkLayer.sendUnicast(neighborData.getSender(), new RouteTableMessage(cloneAndModifyRoutingTable()));

	}

	@Override
	public void updateNeighborData(NeighborDiscoveryData neighborData) {

	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Observable
	private void routingTableHasChanged(){
		setChanged();
		notifyObservers();
	}

}
