package org.rhegium.api.security.spi;

import org.rhegium.api.security.Principal;
import org.rhegium.api.security.SecurityGroup;

public interface SecurityGroupResolver {

	SecurityGroup resolveSecurityGroup(Principal principal);

	SecurityGroup resolveSecurityGroup(long securityGroupId);

}
