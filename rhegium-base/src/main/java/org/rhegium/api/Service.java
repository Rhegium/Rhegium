package org.rhegium.api;

import org.rhegium.api.lifecycle.LifecycleAware;

public interface Service extends LifecycleAware {

	String getServiceName();

}
