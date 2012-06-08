package jane;

import gui.ChatClientGUIInterface;

import java.awt.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
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

public class ChatService implements  RuntimeService, Observer {

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


	public void handleMessage(Address sender, String message, Address source ,Address destination){
		if ( destination.toString().equals(myAddress.toString()) ){
			//notify chat listener
		}
		else{
			Address next = dsdvService.getNextHop(destination);
			linkLayer.sendUnicast(next, new ChatMessage(message,myAddress,destination));
		}
	}
	
	public void sendChatMessage (String Message, Address destination){
		Address next = dsdvService.getNextHop(destination);
		linkLayer.sendUnicast(next, new ChatMessage(Message,myAddress,destination));
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
	//Observer

	@Override
	public void update(Observable arg0, Object arg1) {
		guiInterface.showAllReachableDevices();		
	}


	
}
