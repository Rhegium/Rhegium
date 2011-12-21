package org.rhegium.api.modules;

import com.google.inject.Module;

public interface FrameworkPlugin {

	String getName();

	Module configure();

	void initialize() throws Exception;

	void startup() throws Exception;

	void destroy() throws Exception;

}
