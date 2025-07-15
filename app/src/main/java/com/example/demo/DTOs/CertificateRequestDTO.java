package com.example.demo.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

    
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequestDTO {
    
    @NotBlank(message = "Common name is required")
    @Size(max = 64, message = "Common name must not exceed 64 characters")
    private String commonName;
    
    @Size(max = 64, message = "Organization name must not exceed 64 characters")
    private String organizationName;
    
    @Size(max = 64, message = "Organizational unit must not exceed 64 characters")
    private String organizationalUnit;
    
    @Size(min = 2, max = 2, message = "Country must be a 2-letter ISO code")
    private String country;
    
    @Size(max = 128, message = "State must not exceed 128 characters")
    private String state;
    
    @Size(max = 128, message = "Locality must not exceed 128 characters")
    private String locality;
    
    @Email(message = "Email must be valid")
    private String email;
}
