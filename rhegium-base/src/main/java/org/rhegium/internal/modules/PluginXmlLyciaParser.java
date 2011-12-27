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
package org.rhegium.internal.modules;

import java.util.List;

import org.rhegium.api.modules.FrameworkPlugin;
import org.rhegium.internal.utils.StringUtils;
import org.sourceprojects.lycia.annotations.Attribute;
import org.sourceprojects.lycia.annotations.Child;
import org.sourceprojects.lycia.annotations.ContextObject;
import org.sourceprojects.lycia.annotations.Tag;
import org.sourceprojects.lycia.annotations.TagParser;
import org.sourceprojects.lycia.annotations.TextBody;

public class PluginXmlLyciaParser {
	private final String NS = "http://www.yujinserver.com/schema/plugins";

	@SuppressWarnings("unchecked")
	@TagParser({ @Tag(value = "plugin", namespace = NS) })
	public void parse(
			@Child(value = @Tag(value = "meta", namespace = NS)) @Attribute("id") final String id,
			@Child(value = @Tag(value = "meta", namespace = NS)) @Attribute("prioritized") final boolean prioritized,
			@Child(value = @Tag(value = "meta", namespace = NS)) @Attribute("api") final boolean apiBundle,
			@Child(value = @Tag(value = "meta\\class", namespace = NS)) @TextBody final String className,
			@Child(value = @Tag(value = "meta\\name", namespace = NS)) @TextBody final String name,
			@Child(value = @Tag(value = "dependencies\\dependency", namespace = NS), converter = DependencyTypeConverter.class, listType = String.class) final List<String> dependencies,
			@Child(value = @Tag(value = "exports\\export", namespace = NS), converter = PackageTypeConverter.class, listType = String.class) final List<String> exports,
			@ContextObject final PluginLyciaContextObject contextObject) {

		if (StringUtils.isEmpty(id)) {
			return;
		}

		if (StringUtils.isEmpty(name)) {
			return;
		}

		try {
			final Class<? extends FrameworkPlugin> pluginClass;
			if (className != null && !className.isEmpty()) {
				pluginClass = (Class<? extends FrameworkPlugin>) contextObject
						.getClassLoader().loadClass(className);

			}
			else {
				pluginClass = null;
			}

			contextObject.setPluginDescriptor(new PluginDescriptor(id, name,
					prioritized, apiBundle || pluginClass == null, pluginClass,
					contextObject.getClassLoader(), dependencies, exports));

		}
		catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
