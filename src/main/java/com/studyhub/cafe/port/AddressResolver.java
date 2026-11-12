package com.studyhub.cafe.port;

import java.util.Optional;

public interface AddressResolver {

	Optional<ResolvedAddress> resolve(String address);
}
