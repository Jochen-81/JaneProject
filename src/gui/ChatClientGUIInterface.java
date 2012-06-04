package gui;

import de.uni_trier.jane.basetypes.Address;
import jane.ChatService;
import jane.DSDVService_sync;

public class ChatClientGUIInterface {
	
	private ChatClientGUI gui;
	private ChatService chatService;
	private DSDVService_sync dsdv_interface;

	public ChatClientGUIInterface(ChatService chatService) {
		super();
		gui = new ChatClientGUI();
		gui.setVisible(true);
		this.chatService = chatService;
		this.dsdv_interface = chatService.getDSDV_interface();
		showAllReachableDevices();
	}
	
	public void showAllReachableDevices(){
		gui.lstAllReachables.removeAll();
		for(Object o : dsdv_interface.getAllReachableDevices()){
			Address address = (Address) o;
			int hops = dsdv_interface.getHopCount(address);
			gui.lstAllReachables.add(address.toString() + " ("+hops+")");
		}
	}

}
