package de.htw;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.platform.DefaultPlatformParameters;
import de.uni_trier.jane.platform.ExecutionPlatform;
import de.uni_trier.jane.platform.PlatformParameters;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.OneHopNeighborDiscoveryService;
import de.uni_trier.jane.service.network.link_layer.LinkLayer;
import de.uni_trier.jane.service.network.link_layer.packetNetwork.PacketPlatformNetwork;
import de.uni_trier.jane.service.unit.ServiceUnit;

public class ExecutionPlatform_HTW extends ExecutionPlatform {

	private PlatformParameters parameters;

	public void initPlatform(PlatformParameters parameters) {
		this.parameters = parameters;
	}

	public void initServices(ServiceUnit serviceUnit) {

		PacketPlatformNetwork network = null;
		// Netzzugriff
		try {
			network = new PacketPlatformNetwork();
			serviceUnit.addService(network);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			((DefaultPlatformParameters) parameters).setNetworkAddress(Inet4Address.getByName(network
					.getNetworkAddress().toString()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		ServiceID linkLayerID = serviceUnit.getService(LinkLayer.class);

		// init local services
		// Nachbarschaftsservice anlegen
		OneHopNeighborDiscoveryService.createInstance(serviceUnit, false);

		// Ref zum Nachbarschaftsservice
		ServiceID neighborID = serviceUnit.getService(NeighborDiscoveryService.class);

		// TwoHopSevice anlegen
		// TwoHopService TwoHopService = new
		// TwoHopService(linkLayerID,neighborID);
		// serviceUnit.addService(TwoHopService);

		// DSDVService anlegen
		DSDVService dsdvService = new DSDVService(linkLayerID, neighborID);
		serviceUnit.addService(dsdvService);

		// ChatService anlegen
		ChatService chatService = new ChatService(linkLayerID, neighborID, dsdvService);
		serviceUnit.addService(chatService);
	}

	public static void main(String[] args) {
		ExecutionPlatform_HTW executionPlatform_HTW = new ExecutionPlatform_HTW();
		executionPlatform_HTW.run();

	}

}
