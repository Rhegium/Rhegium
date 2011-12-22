package org.rhegium.api.uibinder;

import org.rhegium.api.mvc.ComponentController;
import org.rhegium.api.mvc.View;

public interface UiBinderEventService {

	void registerComponentController(ComponentController<?, ?> componentController);

	void dispatchEvent(View<?, ?> view, String eventName, Object... arguments);

}
