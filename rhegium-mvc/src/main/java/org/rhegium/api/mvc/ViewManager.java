package org.rhegium.api.mvc;

public interface ViewManager {

	void close(View<?, ?, ?> view);

	void showMsg(String msg);

	void showWarning(String warning);

	void showError(String error);

	ApplicationLayout<?> getApplicationWindow();

}
