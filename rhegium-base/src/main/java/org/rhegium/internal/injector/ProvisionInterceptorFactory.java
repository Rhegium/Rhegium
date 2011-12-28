/*
 * Copyright (C) 2011 Rhegium Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rhegium.internal.injector;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.rhegium.api.lifecycle.LifecycleManager;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;

public final class ProvisionInterceptorFactory {

	private static final Key<LifecycleManager> LIFECYCLE_MANAGER_KEY = Key.get(LifecycleManager.class);
	private static final Key<Set<ProvisionInterceptor>> PROVISION_INTERCEPTOR_KEY = Key
			.get(new TypeLiteral<Set<ProvisionInterceptor>>() {
			});

	private final Collection<Module> modules = new ArrayList<Module>();

	public ProvisionInterceptorFactory install(Collection<? extends Module> modules) {
		this.modules.addAll(modules);
		return this;
	}

	public Module build() {
		final List<Element> elements = Elements.getElements(modules);

		return new AbstractModule() {

			@Override
			protected void configure() {
				new ModuleRedirector(binder()).redirect(elements);
			}
		};
	}

	private static class ModuleRedirector extends DefaultElementVisitor<Void> {

		private final Binder binder;

		ModuleRedirector(Binder binder) {
			this.binder = binder;
		}

		@Override
		protected Void visitOther(Element element) {
			element.applyTo(binder);
			return null;
		}

		public void redirect(Collection<? extends Element> elements) {
			for (Element element : elements) {
				element.acceptVisitor(this);
			}
		}

		@Override
		public <T> Void visit(Binding<T> binding) {
			final Key<T> key = binding.getKey();

			if (key.equals(PROVISION_INTERCEPTOR_KEY) || key.equals(LIFECYCLE_MANAGER_KEY)
					|| ProvisionInterceptor.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
				return super.visit(binding);
			}

			if (binding instanceof UntargettedBinding) {
				return null;
			}

			Key<T> anonymousKey = Key.get(key.getTypeLiteral(), UniqueAnnotations.create());
			binder.bind(key).toProvider(new ProvisionInterceptorProvider<T>(key, binder.getProvider(anonymousKey)));

			ScopedBindingBuilder scopedBindingBuilder = redirectKeyToTarget(binding, binder, anonymousKey);
			applyScoping(binding, scopedBindingBuilder);

			return null;
		}

		private <T> ScopedBindingBuilder redirectKeyToTarget(final Binding<T> binding, final Binder binder, final Key<T> key) {
			return binding.acceptTargetVisitor(new DefaultBindingTargetVisitor<T, ScopedBindingBuilder>() {

				@Override
				public ScopedBindingBuilder visit(InstanceBinding<? extends T> binding) {
					binder.bind(key).toInstance(binding.getInstance());
					return null;
				}

				@Override
				public ScopedBindingBuilder visit(LinkedKeyBinding<? extends T> binding) {
					return binder.bind(key).to(binding.getLinkedKey());
				}

				@Override
				public ScopedBindingBuilder visit(ProviderInstanceBinding<? extends T> binding) {
					return binder.bind(key).toProvider(binding.getProviderInstance());
				}

				@Override
				public ScopedBindingBuilder visit(ProviderKeyBinding<? extends T> binding) {
					return binder.bind(key).toProvider(binding.getProviderKey());
				}

				@Override
				public ScopedBindingBuilder visit(UntargettedBinding<? extends T> binding) {
					return binder.bind(key);
				}
			});
		}

		private <T> void applyScoping(Binding<T> binding, final ScopedBindingBuilder scopedBindingBuilder) {
			binding.acceptScopingVisitor(new BindingScopingVisitor<Void>() {

				@Override
				public Void visitEagerSingleton() {
					if (scopedBindingBuilder != null) {
						scopedBindingBuilder.asEagerSingleton();
					}
					return null;
				}

				@Override
				public Void visitNoScoping() {
					return null;
				}

				@Override
				public Void visitScope(Scope scope) {
					scopedBindingBuilder.in(scope);
					return null;
				}

				@Override
				public Void visitScopeAnnotation(Class<? extends Annotation> scopeAnnotation) {
					scopedBindingBuilder.in(scopeAnnotation);
					return null;
				}
			});
		}
	}

	private static class ProvisionInterceptorProvider<T> implements Provider<T> {

		private final Key<T> key;
		private final Provider<T> delegate;

		private Set<ProvisionInterceptor> provisionInterceptors = Collections.emptySet();

		public ProvisionInterceptorProvider(Key<T> key, Provider<T> delegate) {
			this.key = key;
			this.delegate = delegate;
		}

		@Inject
		@SuppressWarnings("unused")
		void initialize(Set<ProvisionInterceptor> provisionInterceptors) {
			this.provisionInterceptors = provisionInterceptors;
		}

		@Override
		public T get() {
			T value = delegate.get();

			for (ProvisionInterceptor provisionInterceptor : provisionInterceptors) {
				if (provisionInterceptor.accept(key)) {
					value = provisionInterceptor.intercept(key, value);
				}
			}

			return value;
		}
	}

}
