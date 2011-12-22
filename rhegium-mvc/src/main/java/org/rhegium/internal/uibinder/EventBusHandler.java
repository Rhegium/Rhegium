package org.rhegium.internal.uibinder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.rhegium.api.mvc.View;
import org.rhegium.api.uibinder.UiBinderEventService;
import org.rhegium.api.uibinder.UiBinderException;
import org.rhegium.internal.utils.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Item;
import com.vaadin.data.Item.PropertySetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.DoubleClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.ErrorEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.ComponentContainer.ComponentAttachEvent;
import com.vaadin.ui.ComponentContainer.ComponentDetachEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.Table.FooterClickEvent;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.ResizeEvent;

class EventBusHandler implements TargetHandler {

	private static final Map<String, Class<?>> eventMap = new HashMap<String, Class<?>>();
	static {
		eventMap.put("error", Component.ErrorListener.class);
		eventMap.put("componentAttach", ComponentContainer.ComponentAttachListener.class);
		eventMap.put("componentDetach", ComponentContainer.ComponentDetachListener.class);
		eventMap.put("valueChange", Property.ValueChangeListener.class);
		eventMap.put("readOnlyStatusChange", Property.ReadOnlyStatusChangeListener.class);
		eventMap.put("propertySetChange", Container.PropertySetChangeListener.class);
		eventMap.put("itemSetChange", Container.ItemSetChangeListener.class);
		eventMap.put("blur", FieldEvents.BlurListener.class);
		eventMap.put("focus", FieldEvents.FocusListener.class);
		eventMap.put("layoutClick", LayoutEvents.LayoutClickListener.class);
		eventMap.put("click", Button.ClickListener.class);
		eventMap.put("mouseClick", MouseEvents.ClickListener.class);
		eventMap.put("mouseDoubleClick", MouseEvents.DoubleClickListener.class);
		eventMap.put("itemPropertySetChange", Item.PropertySetChangeListener.class);
		eventMap.put("itemClick", ItemClickEvent.ItemClickListener.class);
		eventMap.put("columnResize", Table.ColumnResizeListener.class);
		eventMap.put("headerClick", Table.HeaderClickListener.class);
		eventMap.put("footerClick", Table.FooterClickListener.class);
		eventMap.put("collapse", Tree.CollapseListener.class);
		eventMap.put("expand", Tree.ExpandListener.class);
		eventMap.put("failed", Upload.FailedListener.class);
		eventMap.put("finished", Upload.FinishedListener.class);
		eventMap.put("progress", Upload.ProgressListener.class);
		eventMap.put("started", Upload.StartedListener.class);
		eventMap.put("succeeded", Upload.SucceededListener.class);
		eventMap.put("close", Window.CloseListener.class);
		eventMap.put("resize", Window.ResizeListener.class);
		eventMap.put("selectedTabChange", TabSheet.SelectedTabChangeListener.class);
	}

	private final ComponentHandler componentHandler;
	private final UiBinderEventService uiBinderEventService;
	private final View<?, ?> view;

	EventBusHandler(View<?, ?> view, ComponentHandler componentHandler, UiBinderEventService uiBinderEventService) {
		this.view = view;
		this.componentHandler = componentHandler;
		this.uiBinderEventService = uiBinderEventService;
	}

	@Override
	public String getTargetNamespace() {
		return "urn:de.heldenreich.wcc.framework.mvc.uibinder.event";
	}

	@Override
	public void handleStartElement(String uri, String name) {
	}

	@Override
	public void handleEndElement(String uri, String name) {
	}

	@Override
	public void handleAttribute(String name, Object value) {
		if (value == null || !(value instanceof String) || StringUtils.isEmpty(value.toString())) {
			throw new UiBinderException("Illegal attribute value found");
		}

		String eventName = value.toString();
		bindListener(componentHandler.getCurrentComponent(), name, eventName);
	}

	private void bindListener(Component component, String eventType, String eventName) {
		Class<?> eventTypeClass = eventMap.get(eventType);
		try {
			Method addListener = component.getClass().getMethod("addListener", new Class[] { eventTypeClass });
			addListener.invoke(component, new DispatchingListenerImpl(uiBinderEventService, eventName));
		}
		catch (Exception e) {
			throw new UiBinderException(String.format("The component %s does not support listeners of type '%s'",
					component.getClass().getName(), eventType.getClass().getName()));
		}
	}

	private String buildEventHandlerName(String prefix, String eventName) {
		return prefix + Character.toUpperCase(eventName.charAt(0)) + eventName.substring(1);
	}

	@SuppressWarnings("serial")
	private class DispatchingListenerImpl implements Component.ErrorListener,
			ComponentContainer.ComponentAttachListener, ComponentContainer.ComponentDetachListener,
			Property.ValueChangeListener, Property.ReadOnlyStatusChangeListener, Container.PropertySetChangeListener,
			Container.ItemSetChangeListener, FieldEvents.BlurListener, FieldEvents.FocusListener,
			LayoutEvents.LayoutClickListener, Button.ClickListener, MouseEvents.ClickListener,
			MouseEvents.DoubleClickListener, Item.PropertySetChangeListener, ItemClickEvent.ItemClickListener,
			Table.ColumnResizeListener, Table.FooterClickListener, Table.HeaderClickListener, Tree.CollapseListener,
			Tree.ExpandListener, TabSheet.SelectedTabChangeListener, Upload.FailedListener, Upload.FinishedListener,
			Upload.ProgressListener, Upload.StartedListener, Upload.SucceededListener, Window.CloseListener,
			Window.ResizeListener {

		private final UiBinderEventService uiBinderEventService;
		private final String eventName;

		DispatchingListenerImpl(UiBinderEventService uiBinderEventService, String eventName) {
			this.uiBinderEventService = uiBinderEventService;
			this.eventName = buildEventHandlerName("on", eventName);
		}

		@Override
		public void windowResized(ResizeEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void windowClose(CloseEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void uploadSucceeded(SucceededEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void uploadStarted(StartedEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void updateProgress(long readBytes, long contentLength) {
			uiBinderEventService.dispatchEvent(view, eventName, readBytes, contentLength);
		}

		@Override
		public void uploadFinished(FinishedEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void uploadFailed(FailedEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void selectedTabChange(SelectedTabChangeEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void nodeExpand(ExpandEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void nodeCollapse(CollapseEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void headerClick(HeaderClickEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void footerClick(FooterClickEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void columnResize(ColumnResizeEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void itemClick(ItemClickEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void itemPropertySetChange(PropertySetChangeEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void doubleClick(DoubleClickEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void click(ClickEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void layoutClick(LayoutClickEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void focus(FocusEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void blur(BlurEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void containerItemSetChange(ItemSetChangeEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void containerPropertySetChange(com.vaadin.data.Container.PropertySetChangeEvent event) {
			// TODO Auto-generated method stub

			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void readOnlyStatusChange(ReadOnlyStatusChangeEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void valueChange(ValueChangeEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void componentDetachedFromContainer(ComponentDetachEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void componentAttachedToContainer(ComponentAttachEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

		@Override
		public void componentError(ErrorEvent event) {
			uiBinderEventService.dispatchEvent(view, eventName, event);
		}

	}

}
