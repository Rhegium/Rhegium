package org.rhegium.api.uibinder;

import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;

public interface UiBinderEventService {

	void registerComponentController(Controller<?, ?, ?> componentController);

	void dispatchEvent(View<?, ?, ?> view, String eventName, Object... arguments);

}
