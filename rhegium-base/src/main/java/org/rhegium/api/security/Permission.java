package org.rhegium.api.security;

public interface Permission {

	String getName();

	String getGroup();

	boolean isPermittedByDefault();

}
