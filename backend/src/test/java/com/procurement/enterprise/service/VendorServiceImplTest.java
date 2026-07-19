package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateVendorRequest;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorServiceImplTest {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;

    @Test
    void createVendorShouldThrowWhenEmailAlreadyExists() {
        CreateVendorRequest request = CreateVendorRequest.builder()
                .vendorName("Acme Supplies")
                .email("vendor@example.com")
                .build();

        when(vendorRepository.existsByEmailAndIsDeletedFalse("vendor@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> vendorService.createVendor(request));
        verify(vendorRepository).existsByEmailAndIsDeletedFalse("vendor@example.com");
    }
}
