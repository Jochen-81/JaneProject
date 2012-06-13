package de.htw;

import gui.ChatClientGUIInterface;

import java.util.Observable;
import java.util.Observer;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryServiceStub;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_async;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.visualization.shapes.Shape;

public class ChatService implements RuntimeService, Observer {

	public static ServiceID serviceID;
	private ServiceID linkLayerID;
	private ServiceID neighborID;
	private Address myAddress;
	private ServiceID dsdvServiceID;
	private DSDVService dsdvService;

	private LinkLayer_async linkLayer;
	private NeighborDiscoveryService_sync neighborService;
	private RuntimeOperatingSystem runtimeOperatingSystem;

	private ChatClientGUIInterface guiInterface;

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public ChatService(ServiceID linkLayerID, ServiceID neighborID, DSDVService dsdvService) {
		super();
		this.linkLayerID = linkLayerID;
		this.neighborID = neighborID;
		this.dsdvService = dsdvService;
		dsdvService.addObserver(this);
		serviceID = new EndpointClassID(ChatService.class.getName());

		guiInterface = new ChatClientGUIInterface(this);

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void handleMessage(Address sender, String message, Address source, Address destination) {
		if (destination.toString().equals(myAddress.toString())) {
			guiInterface.showMessage(source, message);
		} else {
			Address dsdv_next = dsdvService.getNextHop(destination);
			Address next = translateDSDVAddressToNeighbourAddress(dsdv_next);
			linkLayer.sendUnicast(next, new ChatMessage(message, source, destination));
		}
	}

	public void sendChatMessage(String Message, Address destination) {
		Address dsdv_next = (Address) dsdvService.getNextHop(destination);
		Address next = translateDSDVAddressToNeighbourAddress(dsdv_next);
		
		linkLayer.sendUnicast(next, new ChatMessage(Message, myAddress, destination));
	}
	
	private Address translateDSDVAddressToNeighbourAddress(Address dsdv_address){
		Address address = null; 
		NeighborDiscoveryServiceStub neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(
				runtimeOperatingSystem, neighborID);
		for(Address a :neighborDiscoveryServiceStub.getNeighbors()){
			if(a.toString().equals(dsdv_address.toString())){
				address = a;
			}
		}
		return address;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		this.runtimeOperatingSystem = runtimeOperatingSystem;

		// Am LinkLayer registrieren, um diesen aus TestService heraus nutzen zu
		// k�nnen
		linkLayer = (LinkLayer_async) runtimeOperatingSystem.getSignalListenerStub(linkLayerID, LinkLayer_async.class);

		runtimeOperatingSystem.registerAtService(linkLayerID, LinkLayer_async.class);

		// Am Nachbarschaftsservice registrieren, um diesen aus TestService
		// heraus nutzen zu k�nnen
		neighborService = (NeighborDiscoveryService_sync) runtimeOperatingSystem.getSignalListenerStub(neighborID,
				NeighborDiscoveryService_sync.class);

		runtimeOperatingSystem.registerAtService(neighborID, NeighborDiscoveryService.class);

		// sich selbst in die routing table eintragen
		// myAddress = runtimeOperatingSystem.getDeviceID();
		NeighborDiscoveryServiceStub neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(
				runtimeOperatingSystem, neighborID);
		this.myAddress = neighborDiscoveryServiceStub.getOwnAddress();

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

	public DSDVService_sync getDSDV_interface() {
		return dsdvService;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Observer

	@Override
	public void update(Observable arg0, Object arg1) {
		guiInterface.showAllReachableDevices();
	}

}
