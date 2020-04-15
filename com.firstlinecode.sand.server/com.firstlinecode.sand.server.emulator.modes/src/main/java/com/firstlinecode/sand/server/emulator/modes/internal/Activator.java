package com.firstlinecode.sand.server.emulator.modes.internal;

import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.firstlinecode.sand.emulators.modes.Ge01ModeDescriptor;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.server.device.IDeviceModesProvider;

public class Activator implements BundleActivator {
	private ServiceRegistration<IDeviceModesProvider> srModesProvider;
	
	@Override
	public void start(BundleContext context) throws Exception {
		srModesProvider = context.registerService(IDeviceModesProvider.class, new DeviceModesProvider(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		srModesProvider.unregister();
	}
	
	private class DeviceModesProvider implements IDeviceModesProvider {
		private Map<String, ModeDescriptor> modes;
		
		public DeviceModesProvider() {
			Ge01ModeDescriptor ge01 = new Ge01ModeDescriptor();
			modes.put(ge01.getName(), ge01);
		}
		
		@Override
		public Map<String, ModeDescriptor> provide() {
			return modes;
		}
		
	}
}
