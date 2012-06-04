	import java.io.Serializable;
import java.util.HashMap;
	import java.util.Set;

	import de.uni_trier.jane.basetypes.Address;
	import de.uni_trier.jane.basetypes.Dispatchable;
	import de.uni_trier.jane.basetypes.Extent;
	import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
	import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
	import de.uni_trier.jane.signaling.SignalListener;
	import de.uni_trier.jane.visualization.Color;
	import de.uni_trier.jane.visualization.shapes.RectangleShape;
	import de.uni_trier.jane.visualization.shapes.Shape;


public class ChatServiceMessage implements LinkLayerMessage, Serializable{


	private static final long serialVersionUID = -4389492289287873189L;

	String message ;
	Address dest;

	public ChatServiceMessage(String Message,Address destination) {
		super();
		message =Message ;
		dest=destination;
	}

	@Override
	public void handle(LinkLayerInfo info, SignalListener listener) {

		((ChatService)listener).handleChatMessage(message,dest);

	}	

	@Override
	public Dispatchable copy() {
	// TODO Auto-generated method stub
	return this;
	}

	@Override
	public Class getReceiverServiceClass() {
	// TODO Auto-generated method stub
		return DSDVService.class;
	}

	@Override
	public Shape getShape() {
		return new RectangleShape(new Extent(10,10), Color.RED,false);
	}

	@Override
	public int getSize() {
	// TODO Auto-generated method stub
		return message.length()*32 +500;
	}
	
}
