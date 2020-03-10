package com.firstlinecode.sand.client.things.commuication;

import java.util.ArrayList;
import java.util.List;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.core.ProtocolChain;
import com.firstlinecode.basalt.protocol.im.stanza.Message;
import com.firstlinecode.basalt.oxm.IOxmFactory;
import com.firstlinecode.basalt.oxm.OxmService;
import com.firstlinecode.basalt.oxm.binary.IBinaryXmppProtocolConverter;
import com.firstlinecode.basalt.oxm.convention.NamingConventionParserFactory;
import com.firstlinecode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.gem.client.bxmpp.BinaryXmppProtocolFactory;

public class ObmFactory implements IObmFactory {
	private static final byte[] MESSAGE_WRAPPER_DATA = new byte[] {88, 0, 1, 0};
	
	private IOxmFactory oxmFactory;
	private IBinaryXmppProtocolConverter binaryXmppProtocolConverter;
	private List<Object> registeredObjects;
	
	public ObmFactory() {
		oxmFactory = OxmService.createStandardOxmFactory();
		binaryXmppProtocolConverter = new BinaryXmppProtocolFactory().createConverter();
		registeredObjects = new ArrayList<>();
	}

	
	@Override
	public byte[] toBinary(Object obj) {
		registerTypeIfNeed(obj.getClass());
		
		Message message = new Message();
		message.setObject(obj);
		
		byte[] data = binaryXmppProtocolConverter.toBinary(oxmFactory.getTranslatingFactory().translate(message));
		byte[] amendData = new byte[data.length - 4];
		amendData[0] = data[0];
		for (int i = 1; i < data.length - 4; i++) {
			amendData[i] = data[i + 4];
		}
		
		return amendData;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void registerTypeIfNeed(Class<?> type) {
		if (!registeredObjects.contains(type)) {
			ProtocolObject projectObject = type.getAnnotation(ProtocolObject.class);
			ProtocolChain protocolChain = ProtocolChain.first(Message.PROTOCOL).next(
					new Protocol(projectObject.namespace(), projectObject.localName()));
			oxmFactory.register(protocolChain, new NamingConventionParserFactory<>(type));
			oxmFactory.register(type, new NamingConventionTranslatorFactory(type));
		}
	}

	@Override
	public Object toObject(Class<?> type, byte[] data) {
		registerTypeIfNeed(type);
		
		byte[] amendData = new byte[data.length + 4];
		
		amendData[0] = data[0];
		for (int i = 0; i < 4; i++) {
			amendData[i + 1] = MESSAGE_WRAPPER_DATA[i];
		}
		for (int i = 0; i < data.length - 1; i++) {
			amendData[i + 5] = data[i + 1];
		}
		
		String xml = binaryXmppProtocolConverter.toXml(amendData);
		Message message = (Message)oxmFactory.getParsingFactory().parse(xml);
		
		return message.getObject();
	}
}