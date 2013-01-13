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
package org.rhegium;

import java.io.InputStream;
import java.net.URL;

class FirewallingClassLoader extends ClassLoader {

	private final String[] bootDelegatingPackages;

	public FirewallingClassLoader(final String[] bootDelegatingPackages, final ClassLoader parent) {
		super(parent);

		this.bootDelegatingPackages = new String[bootDelegatingPackages.length];
		System.arraycopy(bootDelegatingPackages, 0, this.bootDelegatingPackages, 0, bootDelegatingPackages.length);
	}

	@Override
	public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		final String path = name.substring(0, name.lastIndexOf("."));
		if (!accept(path)) {
			throw new ClassNotFoundException(name);
		}

		return super.loadClass(name, resolve);
	}

	@Override
	public URL getResource(final String name) {
		if (!acceptResourcePath(name)) {
			return null;
		}

		return super.getResource(name);
	}

	@Override
	public InputStream getResourceAsStream(final String name) {
		if (!acceptResourcePath(name)) {
			return null;
		}

		return super.getResourceAsStream(name);
	}

	private boolean acceptResourcePath(final String path) {
		final String normalized = path.replace("/", ".").replace("\\", ".");
		final String normalizedPath = normalized.substring(0, normalized.lastIndexOf("."));

		return accept(normalizedPath);
	}

	private boolean accept(final String path) {
		if (path.startsWith("java.") || path.startsWith("sun.") || path.startsWith("com.sun.")
				|| path.startsWith("com.zeroturnaround.") || path.startsWith("org.zeroturnaround.")) {
			return true;
		}

		for (final String temp : bootDelegatingPackages) {
			if (temp.equals(path)) {
				return true;
			}
		}

		return false;
	}

}
