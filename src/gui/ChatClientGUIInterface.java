package gui;

import java.util.HashMap;

import de.uni_trier.jane.basetypes.Address;
import jane.ChatService;
import jane.DSDVService_sync;

public class ChatClientGUIInterface {

	private ChatClientGUI gui;
	private ChatService chatService;
	private DSDVService_sync dsdv_interface;
	private HashMap<String, Address> addressMap = new HashMap<String, Address>();

	public ChatClientGUIInterface(ChatService chatService) {
		super();
		gui = new ChatClientGUI(this);
		gui.setVisible(true);
		this.chatService = chatService;
		this.dsdv_interface = chatService.getDSDV_interface();
		showAllReachableDevices();
	}

	public void showAllReachableDevices() {
		gui.lstAllReachables.removeAll();
		for (Object o : dsdv_interface.getAllReachableDevices()) {
			Address address = (Address) o;
			addressMap.put(address.toString(), address);
			int hops = dsdv_interface.getHopCount(address);
			if (hops != 0) {
				gui.lstAllReachables.add(address.toString() + " (" + hops + ")");
			}
		}
	}

	public void sendMessage(String to, String message) {
		to = to.substring(0,to.indexOf(" ")).trim();
		System.out.println("_"+to+"_"+  addressMap.get(to));
		gui.tbChat.append("I: " + message + "\n");
		chatService.sendChatMessage(message, addressMap.get(to));
	}

	public void showMessage(Address sender, String message) {
		String from = sender.toString();
		gui.tbChat.append(from + ": " + message + "\n");
	}

}
