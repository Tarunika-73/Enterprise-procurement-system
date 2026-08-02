package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.VendorLoginRequest;
import com.procurement.enterprise.dto.request.VendorRegisterRequest;
import com.procurement.enterprise.dto.response.VendorRegisterResponse;

import java.util.Map;

public interface VendorAuthenticationService {

    /**
     * Authenticates a vendor using the vendors table only.
     * Returns a response map containing token and vendor info.
     */
    Map<String, Object> login(VendorLoginRequest request);

    /**
     * Registers a new vendor. Checks email and GST uniqueness,
     * encodes the password with BCrypt, and saves to the vendors table.
     */
    VendorRegisterResponse register(VendorRegisterRequest request);
}
