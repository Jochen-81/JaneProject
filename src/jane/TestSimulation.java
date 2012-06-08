package jane;

import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.gui.ExtendedClickAndPlaySimulationGUI;
import de.uni_trier.jane.gui.SimulationGUI;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.OneHopNeighborDiscoveryService;
import de.uni_trier.jane.service.network.link_layer.LinkLayer;
import de.uni_trier.jane.service.network.link_layer.collision_free.CollisionFreeNetwork;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.Simulation;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySourceSimple;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.RandomMobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.campus.ClickAndPlayMobilitySourceLocation;
import de.uni_trier.jane.simulation.dynamic.mobility_source.campus.DeviceLocation;
import de.uni_trier.jane.simulation.dynamic.mobility_source.campus.FixedPositionLocation;
import de.uni_trier.jane.simulation.kernel.TimeExceeded;


public class TestSimulation extends Simulation {

	@Override
	public void initSimulation(SimulationParameters parameters) {
		MobilitySource mobilitySource = null;
		
		
		//Bewegungsmodell definieren
		
		/*
		// Click and Play mit automatischer Positionierung der mobilen Ger�te
		mobilitySource = new ClickAndPlayMobilitySourceSimple(
				10,
				new Extent(300,300),
				2.0,
				5.0,
				100.0,
				100.0,
				parameters.getDistributionCreator()
				);
		
		
		ExtendedClickAndPlaySimulationGUI gui = new ExtendedClickAndPlaySimulationGUI(
		(ClickAndPlayMobilitySource) mobilitySource );
		
		*/
		
		
		// Click and Play mit fixer Positionierung der mobilen Ger�te
		
		FixedPositionLocation fixed = new FixedPositionLocation
		(new Position[]
		              {
						new Position(20,20),
						new Position(20,60),
						new Position(20,150),
//						new Position(230,50),
//						new Position(110,120),
//						new Position(100,100),
//						new Position(190,150),
//						new Position(150,190),
//						new Position(190,70),
//						new Position(90,90),
//						new Position(90,80),
//						new Position(70,70),
//						new Position(170,270),
//						new Position(75,75),
//						new Position(170,70)
						
					  },
						new Rectangle(new Extent(300,300))
		);
		
		mobilitySource = new ClickAndPlayMobilitySourceLocation(fixed,
				10.0,
				100.0, 
				3, 
				1.0);
				
		
		ExtendedClickAndPlaySimulationGUI gui = new ExtendedClickAndPlaySimulationGUI(
				(ClickAndPlayMobilitySource) mobilitySource );
		
		 
		
		// Zuf�llige Bewegung der mobilen Ger�te
	/*	
		mobilitySource = RandomMobilitySource.createRandomWaypointMobilitySource( parameters.getDistributionCreator(),
				10, // Number of devices
				200, // lifetime
				500, // width 500,
				500, // height
				0.0, // pause
				100, // min sending radius
				100, // max sending radius
				2.0, // min speed
				10.0 // max speed
				);
		
		SimulationGUI gui = new SimulationGUI(new Extent(500, 500));
		*/
		
		//GUI setzen
		parameters.useVisualisation(gui);
		parameters.setMobilitySource( mobilitySource );
		parameters.setTerminalCondition(
				new TimeExceeded(parameters.getEventSet(),1000));	

	}

	@Override
	public void initGlobalServices(ServiceUnit serviceUnit) {
		CollisionFreeNetwork.createInstance(serviceUnit,
				1000*1024,true, false, true);

	}

	//Lokale Services... Laufen auf jedem mobilen Endger�t
	@Override
	public void initServices(ServiceUnit serviceUnit) {
		
		//Ref zum LinkLayer
		ServiceID linkLayerID = serviceUnit.getService(LinkLayer.class);

		//Nachbarschaftsservice anlegen
		OneHopNeighborDiscoveryService.createInstance(serviceUnit, false);
		
		//Ref zum Nachbarschaftsservice
		ServiceID neighborID = serviceUnit.getService(NeighborDiscoveryService.class);
		
		//TwoHopSevice anlegen
		//TwoHopService TwoHopService = new TwoHopService(linkLayerID,neighborID);
		//serviceUnit.addService(TwoHopService);
		
		// DSDVService anlegen
		DSDVService dsdvService = new  DSDVService(linkLayerID,neighborID);
		serviceUnit.addService(dsdvService);
		
		// ChatService anlegen
		ChatService chatService = new  ChatService(linkLayerID,neighborID, dsdvService);
		serviceUnit.addService(chatService);
		
	}
	
	public static void main(String[] args) {
		Simulation simulation = new TestSimulation();
		simulation.run();
	}

}
