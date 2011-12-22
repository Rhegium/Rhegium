package org.rhegium.api.security.spi;

import org.rhegium.api.security.SecurityGroup;

public interface SecurityGroupFactory {

	SecurityGroup create(long securityGroupId, String name,
			long parentSecurityGroupId);

}
